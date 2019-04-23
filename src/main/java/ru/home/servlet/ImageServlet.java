package ru.home.servlet;

import ru.home.service.FileService;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "imageServlet", urlPatterns = "/images/*")
public class ImageServlet extends HttpServlet {
    private FileService fileService;

    @Override
    public void init() throws ServletException {
        try {
            var context = new InitialContext();
            fileService = (FileService) context.lookup("java:/comp/env/bean/file-service");

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() != null) {
            String[] split = request.getPathInfo().split("/");
            if (split.length != 2) {
                throw new RuntimeException("are you kidding me?");
            }
            fileService.readFile(split[1], response.getOutputStream());

//      resp.setHeader("Content-Type", "application/octet-stream");
//      resp.setHeader("Content-Disposition", "Attachment; filename=exported.csv");
//      Files.copy(path, resp.getOutputStream());
        }
    }
}
