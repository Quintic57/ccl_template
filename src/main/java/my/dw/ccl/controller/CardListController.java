package my.dw.ccl.controller;

import lombok.RequiredArgsConstructor;
import my.dw.ccl.service.CardListService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/*
TODO: Rebuild project to pull lists from DB. Connect to wss://duel.duelingbook.com:8443/ via websocketsecure
   (find out how DB handles logins). From there, websocket.send(JSON.stringify({"action":"Load deck"})) will return list
   of deck names associated with the logged in account, {"action":"Load deck", "deck":deck_name_here} gets actual deck contents
   Also, add another controller function that will automatically sync duelingbook and google drive directory*/
@RequiredArgsConstructor
@RestController
public class CardListController {

    private final CardListService cardListService;

    @PostMapping("/generate-report")
    public void generateCardReport() {
        cardListService.generateCardReport();
    }

    @PostMapping("/generate-deck-list")
    public void generateDeckList() {
        cardListService.generateDeckList();
    }

}
