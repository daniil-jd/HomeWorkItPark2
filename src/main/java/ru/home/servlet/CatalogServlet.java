package ru.home.servlet;

import ru.home.domain.Auto;
import ru.home.service.AutoService;

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
    private AutoService service;

    @Override
    public void init() throws ServletException {
        try {
            var context = new InitialContext();
            service = (AutoService) context.lookup("java:/comp/env/bean/auto-service");

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //показать все
        var list  = service.getAll();
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
                searchResult = service.findByName(query);
            } else if (selectBy.equals(AutoService.DESCRIPTION)) {
                searchResult = service.findByDescription(query);
            }
            request.setAttribute("items", searchResult);
            request.getRequestDispatcher("/WEB-INF/catalog.jsp").forward(request, response);
            //создание
        } else if (request.getPart("file") != null && request.getParameter("name") != null) {
            var file = request.getPart("file");
            var name = request.getParameter("name");
            var description = request.getParameter("description");

            service.create(name, description, file);

            var list = service.getAll();
            request.setAttribute("items", list);
            request.getRequestDispatcher("/WEB-INF/catalog.jsp").forward(request, response);
        }

    }
}
