package ru.home.servlet;

import org.apache.commons.lang3.StringUtils;
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
import java.util.UUID;

@WebServlet(name = "detailsServlet", urlPatterns = "/details/*")
@MultipartConfig(location = "D:/idea.projects/itpark/HomeWorkItPark2/upload")
public class DetailsServlet extends HttpServlet {
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
        //details/id
        if (request.getPathInfo() != null) {
            String[] split = request.getPathInfo().split("/");
            if (split.length == 2) {
                var id = split[1];
                var auto = service.getById(id);
                request.setAttribute("item", auto);
                // косяк был здесь - перед WEB-INF надо слэш
                request.getRequestDispatcher("/WEB-INF/details.jsp").forward(request, response);
                return;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //удаление
        if (StringUtils.isEmpty(request.getParameter("delete"))) {
            service.delete(request.getPathInfo().split("/")[1]);

            request.setAttribute("items", service.getAll());
            request.getRequestDispatcher("/WEB-INF/catalog.jsp").forward(request, response);
            return;
        }

        String autoName = request.getParameter("autoName");
        String autoDescription = request.getParameter("autoDescription");
        Auto item = service.getById(request.getPathInfo().split("/")[1]);
        String fileName = item.getImage();
        var autoFile = request.getPart("autoFile");
        //изменение
        if (!StringUtils.isNoneBlank(autoName)) {
            autoName = item.getName();
        }
        if (!StringUtils.isNoneBlank(autoDescription)) {
            autoDescription = item.getDescription();
        }
        if (autoFile != null) {
            fileName = service.saveImageAndGetUid(autoFile);
        }
        service.update(autoName, autoDescription, fileName, item.getId());

        request.setAttribute("item", item);
        doGet(request,response);
    }
}
