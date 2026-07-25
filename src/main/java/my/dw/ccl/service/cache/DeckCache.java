package my.dw.ccl.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.Getter;
import my.dw.ccl.adapter.GoogleSheetsNotebookAdapter;
import my.dw.ccl.domain.Format;
import my.dw.ccl.domain.Game;
import my.dw.ccl.domain.deck.Deck;

public abstract class DeckCache {

  private final LoadingCache<Format, List<Deck>> deckCache;

  @Getter
  private final Game game;

  public DeckCache(
      final GoogleSheetsNotebookAdapter googleSheetsNotebookAdapter,
      final CacheBuilder<Object, Object> deckCacheBuilder,
      final String spreadSheetId,
      final Game game) {
    deckCache = deckCacheBuilder.build(
        new CacheLoader<>() {
          @Override
          public List<Deck> load(final Format key) throws Exception {
            final List<Map<String, String>> sheetValues = googleSheetsNotebookAdapter
                .getSheetValues(spreadSheetId, key);
            return convert(sheetValues, key);
          }
        }
    );
    this.game = game;
  }

  public Map<Format, List<Deck>> getAllDecks(final Game game) {
    try {
      return deckCache.getAll(
          Arrays.stream(Format.values())
              .filter(format -> format.getGame() == game)
              .toList()
      );
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Deck> getDecksByFormat(final Format format) {
    return deckCache.getUnchecked(format);
  }

  abstract List<Deck> convert(List<Map<String, String>> rows, Format format);

}
