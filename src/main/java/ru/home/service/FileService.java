package ru.home.service;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class FileService {
    private String uploadPath;
    private String temp = "D:\\idea.projects\\itpark\\HomeWorkItPark2\\upload";

    public FileService() throws IOException {
        uploadPath = System.getenv("UPLOAD_PATH");
        if (StringUtils.isEmpty(uploadPath)) {
            uploadPath = temp;
        }

        Files.createDirectories(Paths.get(uploadPath));
    }

    public void readFile(String id, ServletOutputStream os) throws IOException {
        var path = Paths.get(uploadPath).resolve(id);
        Files.copy(path, os);
    }

    public String writeFile(Part part) throws IOException {
        var id = UUID.randomUUID().toString();
        part.write(Paths.get(uploadPath).resolve(id).toString());
        return id;
    }


}
