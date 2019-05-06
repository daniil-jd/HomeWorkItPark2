package ru.home.servlet;

import org.apache.commons.lang3.StringUtils;
import ru.home.domain.Auto;
import ru.home.service.AutoService;
import ru.home.service.FileService;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "catalogServlet", urlPatterns = "/catalog/*")
@MultipartConfig
public class CatalogServlet extends HttpServlet {
    private AutoService autoService;
    private FileService fileService;

    @Override
    public void init() throws ServletException {
        try {
            var context = new InitialContext();
            autoService = (AutoService) context.lookup("java:/comp/env/bean/auto-service");
            fileService = (FileService) context.lookup("java:/comp/env/bean/file-service");

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //поиск
        if (request.getParameter("inputSelect") != null && request.getParameter("query") != null) {
            request.setAttribute("items", search(request));
        } else {
            //показать все
            var list = autoService.getAll();
            request.setAttribute("items", list);
        }
        //перед WEB-INF надо слэш
        request.getRequestDispatcher("/WEB-INF/catalog.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //создание
        if (request.getPart("file") != null
                && request.getParameter("name") != null
                && request.getPart("description") != null) {
            create(request);
            response.sendRedirect(String.join("/", request.getServletPath()));
        } else if (request.getPart("csvFile") != null) {
            loadFromFile(request);
            response.sendRedirect(String.join("/", request.getServletPath()));
        }

    }

    private List<Auto> search(HttpServletRequest request) {
        String query = request.getParameter("query");
        String selectBy = request.getParameter("inputSelect");

        List<Auto> searchResult = new ArrayList<>();
        if (StringUtils.isNotEmpty(query)) {
            searchResult = autoService.findByField(selectBy, query);
        } else {
            searchResult = autoService.getAll();
        }
        return searchResult;
    }

    private void create(HttpServletRequest request) throws IOException, ServletException {
        var file = request.getPart("file");
        var name = request.getParameter("name");
        var description = request.getParameter("description");
        var year = request.getParameter("year");
        var power = Double.parseDouble(request.getParameter("power"));
        var color = request.getParameter("color");

        var image = fileService.writeFile(file);
        autoService.create(new Auto(name, description, year, power, color, image));
    }

    private void loadFromFile(HttpServletRequest request) throws IOException, ServletException {
        var file = request.getPart("csvFile");
        Auto auto = fileService.loadCsvFile(file, request).get();
        if (autoService.getById(auto.getId()).isEmpty()) {
            autoService.create(auto);
        } else {
            autoService.update(auto);
        }
    }

}
