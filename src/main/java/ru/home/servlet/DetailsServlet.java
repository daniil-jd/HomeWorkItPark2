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
import java.io.OutputStream;

@WebServlet(name = "detailsServlet", urlPatterns = "/details/*")
@MultipartConfig
public class DetailsServlet extends HttpServlet {
    private AutoService autoService;
    private FileService fileService;
    private CsvService csvService;

    @Override
    public void init() throws ServletException {
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
        //details/id
        if (request.getPathInfo() != null) {
            String[] split = request.getPathInfo().split("/");
            if (split.length == 2) {
                var id = split[1];
                var auto = autoService.getById(id).get();
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
        if ((request.getParameter("delete")) != null) {
            autoService.delete(request.getPathInfo().split("/")[1]);

            response.sendRedirect("/");
            //load
        } else if ((request.getParameter("load")) != null) {
            saveCsvFile(autoService.getById(request.getPathInfo()
                    .split("/")[1]).get(), response);
        } else {
            //изменение
            updateAuto(request);
            response.sendRedirect((request.getServletPath() + request.getPathInfo()));
        }
    }

    private void updateAuto(HttpServletRequest request) throws IOException, ServletException {
        String autoName = request.getParameter("autoName");
        String autoDescription = request.getParameter("autoDescription");
        String autoYear = request.getParameter("autoYear");

        double autoPower = 0;
        if (StringUtils.isNotEmpty(request.getParameter("autoPower"))) {
            autoPower = Double.parseDouble(request.getParameter("autoPower"));
        }

        String autoColor = request.getParameter("autoColor");
        Auto item = autoService.getById(request.getPathInfo().split("/")[1]).get();
        var autoFile = request.getPart("autoFile");
        if (StringUtils.isNoneBlank(autoName)) {
            item.setName(autoName);
        }
        if (StringUtils.isNoneBlank(autoDescription)) {
            item.setDescription(autoDescription);
        }
        if (StringUtils.isNoneBlank(autoYear)) {
            item.setYear(autoYear);
        }
        if (autoPower > 0) {
            item.setPower(autoPower);
        }
        if (StringUtils.isNoneBlank(autoColor)) {
            item.setColor(autoColor);
        }
        if (autoFile == null) {
            item.setImage(fileService.writeFile(autoFile));
        }
        autoService.update(item);
    }

    private void saveCsvFile(Auto auto, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition",
                "Attachment; filename=" + auto.getId() + ".csv");

        try {
            String result = csvService.saveOneItemCsv(auto);

            OutputStream outputStream = response.getOutputStream();
            outputStream.write(result.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
