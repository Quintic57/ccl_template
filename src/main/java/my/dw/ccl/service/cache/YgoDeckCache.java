package my.dw.ccl.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.micrometer.common.util.StringUtils;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import my.dw.ccl.adapter.GoogleSheetsNotebookAdapter;
import my.dw.ccl.domain.deck.Deck;
import my.dw.ccl.domain.Format;
import my.dw.ccl.domain.Game;
import my.dw.ccl.domain.deck.YgoDeck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class YgoDeckCache implements DeckCache {

  private final LoadingCache<Format, List<Deck>> deckCache;
  // TODO: spreadSheetId will probably be passed via API?
  @Value("${app.ygo.spread-sheet-id}")
  private String spreadSheetId;

  public YgoDeckCache(final GoogleSheetsNotebookAdapter googleSheetsNotebookAdapter,
                      final CacheBuilder<Object, Object> deckCacheBuilder) {
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
  }

  @Override
  public Map<Format, List<Deck>> getAllDecks() {
    try {
      return deckCache.getAll(Arrays.asList(Format.values()));
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<Deck> getDecksByFormat(final Format format) {
    return deckCache.getUnchecked(format);
  }

  @Override
  public Game game() {
    return Game.YUGIOH;
  }

  private List<Deck> convert(final List<Map<String, String>> rows, final Format format) {
        return rows.stream()
            .map(m -> new YgoDeck(
                            m.get("Name"),
                            format,
                            StringUtils.isNotBlank(m.get("Implemented Date")) ?  LocalDate.parse(m.get("Implemented Date")) : null,
                            Boolean.parseBoolean(m.get("Active")),
                            m.get("Shared"),
                            Boolean.parseBoolean(m.get("Custom")),
                            YearMonth.of(
                                    Integer.parseInt((m.get("Banlist")).split("-")[0]),
                                    Integer.parseInt(( m.get("Banlist")).split("-")[1])
                            ),
                            Boolean.parseBoolean(m.get("Ported")),
                            Boolean.parseBoolean(m.get("OCG"))
                    )
            )
            .collect(Collectors.toList());
  }

}
