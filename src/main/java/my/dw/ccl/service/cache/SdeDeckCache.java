package my.dw.ccl.service.cache;

import com.google.common.cache.CacheBuilder;
import io.micrometer.common.util.StringUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import my.dw.ccl.adapter.GoogleSheetsNotebookAdapter;
import my.dw.ccl.domain.Format;
import my.dw.ccl.domain.Game;
import my.dw.ccl.domain.deck.Deck;
import my.dw.ccl.domain.deck.SdeDeck;
import my.dw.ccl.domain.sde.SdeClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SdeDeckCache extends DeckCache {

  public SdeDeckCache(
      // TODO: spreadSheetId needs to be configured per user, thus we'll need user context
      @Value("${app.sde.spread-sheet-id}") final String spreadSheetId,
      final GoogleSheetsNotebookAdapter googleSheetsNotebookAdapter,
      final CacheBuilder<Object, Object> deckCacheBuilder) {
    super(googleSheetsNotebookAdapter, deckCacheBuilder, spreadSheetId, Game.SHADOWVERSE_EVOLVE);
  }

  @Override
  List<Deck> convert(final List<Map<String, String>> rows, final Format format) {
    return rows.stream()
        .map(m -> new SdeDeck(
            m.get("Name"),
            format,
            StringUtils.isNotBlank(m.get("Implemented Date"))
                ? LocalDate.parse(m.get("Implemented Date")) : null,
            Boolean.parseBoolean(m.get("Active")),
            m.get("Shared"),
            Boolean.parseBoolean(m.get("Custom")),
            m.get("Class") != null ? SdeClass.fromAbbreviation(m.get("Class")) : null,
            m.get("Set"))
        )
        .collect(Collectors.toList());
  }

}
