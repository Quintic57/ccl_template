package my.dw.ccl.service.cache;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import my.dw.ccl.domain.deck.Deck;
import my.dw.ccl.domain.Format;
import my.dw.ccl.domain.Game;
import org.springframework.stereotype.Component;

@Component
public class CacheService {

  private final Map<Game, DeckCache> cacheMap;

  public CacheService(final List<DeckCache> deckCacheList) {
      cacheMap = deckCacheList
          .stream()
          .collect(Collectors.toMap(DeckCache::game, deckCache -> deckCache));
  }

  public Map<Format, List<Deck>> getAllDecks(final Game game) {
    if (!cacheMap.containsKey(game)) {
      throw new IllegalStateException(String.format("Game" + " %s is not supported", game.getAbbreviation()));
    }

    return cacheMap.get(game).getAllDecks();
  }

  public List<Deck> getDecksByFormat(final Format format) {
    if (!cacheMap.containsKey(format.getGame())) {
      throw new IllegalStateException(String.format("Game" + " %s is not supported", format.getGame().getAbbreviation()));
    }

    return cacheMap.get(format.getGame()).getDecksByFormat(format);
  }
}
