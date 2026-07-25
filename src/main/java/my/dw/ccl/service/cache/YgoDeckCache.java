package my.dw.ccl.service.cache;

import com.google.common.cache.CacheBuilder;
import io.micrometer.common.util.StringUtils;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import my.dw.ccl.adapter.GoogleSheetsNotebookAdapter;
import my.dw.ccl.domain.Format;
import my.dw.ccl.domain.Game;
import my.dw.ccl.domain.deck.Deck;
import my.dw.ccl.domain.deck.YgoDeck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class YgoDeckCache extends DeckCache {

  public YgoDeckCache(
      // TODO: spreadSheetId needs to be configured per user, thus we'll need user context
      @Value("${app.ygo.spread-sheet-id}") final String spreadSheetId,
      final GoogleSheetsNotebookAdapter googleSheetsNotebookAdapter,
      final CacheBuilder<Object, Object> deckCacheBuilder) {
    super(googleSheetsNotebookAdapter, deckCacheBuilder, spreadSheetId, Game.YUGIOH);
  }

  @Override
  List<Deck> convert(final List<Map<String, String>> rows, final Format format) {
    return rows.stream()
        .map(m -> new YgoDeck(
                m.get("Name"),
                format,
                StringUtils.isNotBlank(m.get("Implemented Date"))
                    ? LocalDate.parse(m.get("Implemented Date")) : null,
                Boolean.parseBoolean(m.get("Active")),
                m.get("Shared"),
                Boolean.parseBoolean(m.get("Custom")),
                YearMonth.of(
                    Integer.parseInt((m.get("Banlist")).split("-")[0]),
                    Integer.parseInt((m.get("Banlist")).split("-")[1])
                ),
                Boolean.parseBoolean(m.get("Ported")),
                Boolean.parseBoolean(m.get("OCG"))
            )
        )
        .collect(Collectors.toList());
  }

}
