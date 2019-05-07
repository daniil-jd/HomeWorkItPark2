package ru.home.servlet;

import org.apache.commons.lang3.StringUtils;
import ru.home.domain.Auto;
import ru.home.service.AutoService;
import ru.home.service.CsvService;
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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "catalogServlet", urlPatterns = "/catalog/*")
@MultipartConfig
public class CatalogServlet extends HttpServlet {
    private AutoService autoService;
    private CsvService csvService;
    private FileService fileService;
    private String uploadPath;

    @Override
    public void init() throws ServletException {
        uploadPath = System.getenv("UPLOAD_PATH");
        try {
            var context = new InitialContext();
            autoService = (AutoService) context.lookup("java:/comp/env/bean/auto-service");
            fileService = (FileService) context.lookup("java:/comp/env/bean/file-service");
            csvService = (CsvService) context.lookup("java:/comp/env/bean/csv-service");

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
        List<Auto> autos = csvService.readCsv(new String(file.getInputStream().readAllBytes()));

        for (Auto auto : autos) {
            if (StringUtils.isEmpty(auto.getImage()) || !isImagePresent(auto.getImage())) {
                if (!isImagePresent("car")) {
                    InputStream is = request.getServletContext().getResourceAsStream("/WEB-INF/static/car");
                    Files.copy(is, Paths.get(uploadPath).resolve("car"));
                }
                auto.setImage("car");
            }

            if (autoService.getById(auto.getId()).isEmpty()) {
                autoService.create(auto);
            } else {
                autoService.update(auto);
            }
        }

    }

    private boolean isImagePresent(String id) {
        return Paths.get(uploadPath).resolve(id).toFile().canRead();
    }

}
