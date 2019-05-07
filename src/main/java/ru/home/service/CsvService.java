package ru.home.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import ru.home.domain.Auto;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class CsvService {
    private static String[] HEADERS = { "id", "name", "description", "year", "power", "color", "image"};

    public String saveCsv(Auto auto) throws IOException {
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

    public List<Auto> readCsv(String text) throws IOException {
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
        return autos;
    }

}
