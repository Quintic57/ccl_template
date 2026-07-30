package my.dw.ccl.controller;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import my.dw.ccl.domain.Deck;
import my.dw.ccl.domain.Format;
import my.dw.ccl.domain.Game;
import my.dw.ccl.service.DeckListService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: Should probably create separate DTO for decks; keeps the domain model and dto separate
@RequiredArgsConstructor
@RestController
@RequestMapping("/decks")
public class DeckListController {

    private final DeckListService deckListService;

    @GetMapping("/game/{game}/all")
    public Map<Format, List<Deck>> getAllDecks(@PathVariable("game") final Game game) {
        return deckListService.getAllDecks();
    }

    @GetMapping("/game/{game}/format/{format}")
    public List<Deck> getDecksByFormat(
        @PathVariable("game") final Game game,
        @PathVariable("format") final Format format) {
        return deckListService.getActiveDecksByFormat(format);
    }

}
