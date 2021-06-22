package my.ygo.ccl.controller;

import my.ygo.ccl.domain.CardListDto;
import my.ygo.ccl.service.CardListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardListController {

    private final CardListService cardListService;

    @Autowired
    public CardListController(final CardListService cardListService) {
        this.cardListService = cardListService;
    }

    @PostMapping
    public String generateCardList(@RequestBody final CardListDto dto) {
        return this.cardListService.generateManualCardList();
    }
}
