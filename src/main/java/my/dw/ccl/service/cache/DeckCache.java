package my.dw.ccl.service.cache;

import java.util.List;
import java.util.Map;
import my.dw.ccl.domain.deck.Deck;
import my.dw.ccl.domain.Format;
import my.dw.ccl.domain.Game;

public interface DeckCache {

    Map<Format, List<Deck>> getAllDecks();

    List<Deck> getDecksByFormat(Format format);

    Game game();

}
