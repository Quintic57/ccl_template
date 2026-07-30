package my.dw.ccl.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import my.dw.ccl.domain.deck.Deck;
import my.dw.ccl.domain.Format;
import my.dw.ccl.domain.Game;
import my.dw.ccl.service.cache.CacheService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeckListService {

  private final CacheService cacheService;

  public Map<Format, List<Deck>> getAllDecks(final Game game) {
    return cacheService.getAllDecks(game);
  }

  public List<Deck> getActiveDecksByFormat(final Game game, final String format) {
    return getActiveDecksByFormat(Format.fromGameAndName(game, format));
  }

  public List<Deck> getActiveDecksByFormat(final Format format) {
    return cacheService.getDecksByFormat(format)
        .stream()
        .filter(Deck::isActive)
        .toList();
  }

}
