package my.dw.ccl.controller;

import my.dw.ccl.dto.CardListDto;
import my.dw.ccl.service.CardListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/*
TODO: Rebuild project to pull lists from DB. Connect to wss://duel.duelingbook.com:8443/ via websocketsecure
   (find out how DB handles logins). From there, websocket.send(JSON.stringify({"action":"Load deck"})) will return list
   of deck names associated with the logged in account, {"action":"Load deck", "deck":deck_name_here} gets actual deck contents
   Also, add another controller function that will automatically sync duelingbook and google drive directory*/
//@RestController
public class CardListController {

    //TODO: Implement once this converted to a spring app
    private final CardListService cardListService;

    @Autowired
    public CardListController(final CardListService cardListService) {
        this.cardListService = cardListService;
    }

    //TODO: Currently not used atm, as this application only generates a local file that is used to manually check carts.
    // In the future, this should take both the input card list AND current TCG/coolstuff carts in the request body, and
    // should return a report on the discrepancies (if any) between the two
    @PostMapping
    public String generateCardList(@RequestBody final CardListDto dto) {
        return "";
    }

}
