package my.ygo.ccl;

import lombok.RequiredArgsConstructor;
import my.ygo.ccl.service.CardListService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class CclApplication {

    // TODO: Temporary. Once converted to spring app, this should just have the SpringBootApplication annotation,
    // and the logic to read input should be delineated to another service, also should just return the output as
    // opposed to writing it to a file
    public static void main(String[] args) throws IOException {
        final CardListService cardListService = new CardListService();
        final String cardList = Files.readString(Path.of("src/main/resources/in/card_list.txt"));
        final String cardReport = cardListService.generateCardReport(cardList);
        writeToFile(cardReport);
    }

    private static void writeToFile(final String output) throws IOException {
        final LocalDate now = LocalDate.now();
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final String fileName = "Report_" + dtf.format(now) + ".txt";
        final BufferedWriter out = new BufferedWriter(new FileWriter("src/main/resources/out/" + fileName));
        out.write(output);
        out.close();
    }

}
