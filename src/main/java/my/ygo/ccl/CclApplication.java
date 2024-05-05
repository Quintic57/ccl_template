package my.ygo.ccl;

import feign.Feign;
import lombok.RequiredArgsConstructor;
import my.ygo.ccl.feign.DuelingBookClient;
import my.ygo.ccl.service.CardListService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/*
destr@DESKTOP-PSASKJP MINGW64 ~
$ curl -X POST https://www.duelingbook.com/php-scripts/login-user.php -d "username=quintic57&password=oRRktNtPZV7LJ-BxFc_9&remember_me=0"
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   225    0   163  100    62    259     98 --:--:-- --:--:-- --:--:--   357{"action":"Logged in","user_id":524430,"username":"Quintic57","password":"98717b28e7b5a09e4ec7a7524c83e120ada09814","admin":false,"firstLogin":false,"logins":null}
*/

@RequiredArgsConstructor
public class CclApplication {

    // TODO: Temporary. Once converted to spring app, this should just have the SpringBootApplication annotation,
    // and the logic to read input should be delineated to another service, also should just return the output as
    // opposed to writing it to a file
    public static void main(String[] args) throws IOException {
//        final DuelingBookClient duelingBookClient = Feign.builder()
//            .target(DuelingBookClient.class, "https://www.duelingbook.com");
//        duelingBookClient.login();
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
