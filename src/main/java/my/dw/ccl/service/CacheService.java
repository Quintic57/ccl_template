package my.dw.ccl.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import my.dw.ccl.adapter.GoogleSheetsNotebookAdapter;
import my.dw.ccl.domain.Deck;
import my.dw.ccl.domain.Format;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheService {

  // TODO: This cache will have to support multiple games' formats. How to approach this?
  private final LoadingCache<Format, List<Deck>> deckCache;
  // TODO: spreadSheetId will probably be passed via API?
  @Value("${app.spread-sheet-id}")
  private String spreadSheetId;

  public CacheService(final GoogleSheetsNotebookAdapter googleSheetsNotebookAdapter,
      final CacheBuilder<Object, Object> deckCacheBuilder) {
    deckCache = deckCacheBuilder.build(
        new CacheLoader<>() {
          @Override
          public List<Deck> load(Format key) throws Exception {
            return googleSheetsNotebookAdapter.getDecksByFormat(spreadSheetId, key);
          }
        }
    );
  }

  public Map<Format, List<Deck>> getAllDecks() {
    try {
      return deckCache.getAll(Arrays.asList(Format.values()));
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Deck> getDecksByFormat(final Format format) {
    return deckCache.getUnchecked(format);
  }

}
