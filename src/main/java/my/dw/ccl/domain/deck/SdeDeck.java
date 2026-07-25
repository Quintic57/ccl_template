package my.dw.ccl.domain.deck;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Getter;
import my.dw.ccl.domain.Format;
import my.dw.ccl.domain.sde.SdeClass;

@Getter
public class SdeDeck extends Deck {

  @JsonProperty("class")
  private final SdeClass sdeClass;
  private final String set;

  public SdeDeck(
      final String name,
      final Format format,
      final LocalDate implementedDate,
      final boolean active,
      final String shared,
      final boolean custom,
      final SdeClass sdeClass,
      final String set) {
    super(name, format, implementedDate, active, shared, custom);

    this.sdeClass = sdeClass;
    this.set = set;
  }
}
