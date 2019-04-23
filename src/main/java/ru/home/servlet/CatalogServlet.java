package ru.home.servlet;

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
@MultipartConfig(location = "D:/idea.projects/itpark/HomeWorkItPark2/upload")
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
        //показать все
        var list  = autoService.getAll();
        request.setAttribute("items", list);
        //перед WEB-INF надо слэш
        request.getRequestDispatcher("/WEB-INF/catalog.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //поиск
        if (request.getParameter("inputSelect") != null && request.getParameter("query") != null) {
            String query = request.getParameter("query");
            String selectBy = request.getParameter("inputSelect");

            List<Auto> searchResult = new ArrayList<>();
            if (selectBy.equals(AutoService.NAME)) {
                searchResult = autoService.findByName(query);
            } else if (selectBy.equals(AutoService.DESCRIPTION)) {
                searchResult = autoService.findByDescription(query);
            }
            request.setAttribute("items", searchResult);
            request.getRequestDispatcher("/WEB-INF/catalog.jsp").forward(request, response);
            //создание
        } else if (request.getPart("file") != null
                && request.getParameter("name") != null
                && request.getPart("description") != null) {
            var file = request.getPart("file");
            var name = request.getParameter("name");
            var description = request.getParameter("description");

            var image = fileService.writeFile(file);
            autoService.create(name, description, image);

            response.sendRedirect(String.join("/", request.getServletPath()));
        }

    }
}
