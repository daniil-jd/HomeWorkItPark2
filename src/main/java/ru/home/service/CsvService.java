package ru.home.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import ru.home.domain.Auto;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CsvService {
    private static String[] HEADERS = { "id", "name", "description", "year", "power", "color", "image"};
    private final String UPLOAD_PATH = System.getenv("UPLOAD_PATH");
    private final String CAR_IMAGE = "car";

    public String saveOneItemCsv(Auto auto) throws IOException {
        StringWriter sw = new StringWriter();
        try (
                CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.withHeader(HEADERS))
        ) {
            csvPrinter.printRecord(
                    auto.getId(), auto.getName(), auto.getDescription(),
                    auto.getYear(), auto.getPower(), auto.getColor(), auto.getImage()
            );
            csvPrinter.flush();
        }
        sw.flush();
        return sw.toString();
    }

    public String saveAllItemsCsv(List<Auto> autos) throws IOException {
        StringWriter sw = new StringWriter();
        try (
                CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.withHeader(HEADERS))
        ) {
            for (Auto auto : autos) {
                csvPrinter.printRecord(
                        auto.getId(), auto.getName(), auto.getDescription(),
                        auto.getYear(), auto.getPower(), auto.getColor(), auto.getImage()
                );
            }
            csvPrinter.flush();
        }
        sw.flush();
        return sw.toString();
    }

    public void readCsv(AutoService autoService, InputStream is, String text) throws IOException {
        StringReader sr = new StringReader(text);
        List<Auto> autos = new ArrayList<>();
        try (
                CSVParser csvParser = new CSVParser(sr, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withHeader(HEADERS));
        ) {
            for (CSVRecord csvRecord : csvParser) {
                Auto auto = Auto.builder()
                        .id(csvRecord.get("id"))
                        .name(csvRecord.get("name"))
                        .description(csvRecord.get("description"))
                        .year(csvRecord.get("year"))
                        .power(Double.parseDouble(csvRecord.get("power")))
                        .color(csvRecord.get("color"))
                        .image(csvRecord.get("image"))
                        .build();
                autos.add(auto);
            }
        }

        for (Auto auto : autos) {
            if (StringUtils.isEmpty(auto.getImage()) || !isImagePresent(auto.getImage())) {
                if (!isImagePresent(CAR_IMAGE)) {
                    Files.copy(is, Paths.get(UPLOAD_PATH).resolve(CAR_IMAGE));
                }
                auto.setImage(CAR_IMAGE);
            }

            if (autoService.getById(auto.getId()).isEmpty()) {
                autoService.create(auto);
            } else {
                autoService.update(auto);
            }
        }
    }

    private boolean isImagePresent(String id) {
        return Paths.get(UPLOAD_PATH).resolve(id).toFile().canRead();
    }

}
