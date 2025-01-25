package my.ygo.ccl.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CsvSerializer {

    public static List<Map<String, String>> readObjectsFromCsv(File file) {
        CsvSchema bootstrap = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        try (MappingIterator<Map<?, ?>> mappingIterator = csvMapper.readerFor(Map.class).with(bootstrap).readValues(file)) {
            final List<Map<String, String>> csvData = new ArrayList<>();
            mappingIterator.readAll()
                .forEach(map -> {
                    final Map<String, String> deck = map.entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
                    csvData.add(deck);
                });
            return csvData;
        } catch (IOException e) {
            System.out.println("Unable to read file: " + file.getAbsolutePath());
            throw new RuntimeException(e);
        }
    }

}