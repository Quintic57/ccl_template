package my.dw.ccl.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import my.dw.ccl.domain.Deck;
import my.dw.ccl.domain.Format;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeckListService {

  private final CacheService cacheService;

  public Map<Format, List<Deck>> getAllDecks() {
    return cacheService.getAllDecks();
  }

  public List<Deck> getActiveDecksByFormat(final Format format) {
    return cacheService.getDecksByFormat(format)
        .stream()
        .filter(Deck::isActive)
        .toList();
  }

}
