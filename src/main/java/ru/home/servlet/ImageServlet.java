package ru.home.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet(name = "imageServlet", urlPatterns = "/images/*")
public class ImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() != null) {
            String[] split = request.getPathInfo().split("/");
            if (split.length != 2) {
                throw new RuntimeException("are you kidding me?");
            }
            var id = split[1];
            var path = Paths.get("D:/idea.projects/itpark/HomeWorkItPark2/upload").resolve(id);
            if (!Files.exists(path)) {
                throw new RuntimeException("404");
            }

//      resp.setHeader("Content-Type", "text/plain");
            Files.copy(path, response.getOutputStream());

//      resp.setHeader("Content-Type", "application/octet-stream");
//      resp.setHeader("Content-Disposition", "Attachment; filename=exported.csv");
//      Files.copy(path, resp.getOutputStream());
        }
    }
}
