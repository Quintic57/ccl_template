package my.dw.ccl;

import lombok.RequiredArgsConstructor;
import my.dw.ccl.domain.Format;
import my.dw.ccl.service.CardListService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

@SpringBootApplication
public class CclApplication {

    public static void main(String[] args) {
        SpringApplication.run(CclApplication.class, args);
        /*
        final DuelingBookClient duelingBookClient = Feign.builder()
            .target(DuelingBookClient.class, "https://www.duelingbook.com");
        duelingBookClient.login();
         */
    }

}
