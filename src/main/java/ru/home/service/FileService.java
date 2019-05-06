package ru.home.service;

import org.apache.commons.lang3.StringUtils;
import ru.home.domain.Auto;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

public class FileService {
    private String uploadPath;

    public FileService() throws IOException {
        uploadPath = System.getenv("UPLOAD_PATH");
        Files.createDirectories(Paths.get(uploadPath));
    }

    public void readFile(String id, ServletOutputStream os) throws IOException {
        var path = Paths.get(uploadPath).resolve(id);
        Files.copy(path, os);
    }

    private boolean isImagePresent(String id) {
        return Paths.get(uploadPath).resolve(id).toFile().canRead();
    }

    public String writeFile(Part part) throws IOException {
        var id = UUID.randomUUID().toString();
        part.write(Paths.get(uploadPath).resolve(id).toString());
        return id;
    }

    public void saveCsvFile(Auto auto, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition",
                "Attachment; filename=" + auto.getId() + ".csv");

        try
        {
            OutputStream outputStream = response.getOutputStream();
            String format = "Auto: %s, %s, %s, %s";
            outputStream.write(String.format(
                    format,
                    auto.getId(),
                    auto.getName(),
                    auto.getDescription(),
                    auto.getImage()
            ).getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<Auto> loadCsvFile(Part file, HttpServletRequest request) throws IOException {
        Optional<Auto> auto = Optional.empty();
        try {
            auto = Optional.of(parseAutoFromString(new String(file.getInputStream().readAllBytes())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StringUtils.isEmpty(auto.get().getImage()) || !isImagePresent(auto.get().getImage())) {
            if (!isImagePresent("car")) {
                InputStream is = request.getServletContext().getResourceAsStream("/WEB-INF/static/car");
                Files.copy(is, Paths.get(uploadPath).resolve("car"));
            }
            auto.get().setImage("car");
        }

        return auto;
    }

    private Auto parseAutoFromString(String text) throws Exception {
        if (!text.startsWith("Auto: ")) {
            throw new Exception("CSV-file is missing 'Auto: ' start word");
        }
        text = text.split("Auto: ")[1];

        String[] data = text.split(", ");
        if (data.length != 4) {
            throw new Exception("CSV-file does't match pattern");
        }
//        return new Auto(data[0], data[1], data[2], data[3]);

        return null;
    }


}
