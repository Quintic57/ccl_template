package my.dw.ccl.domain.ygo;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import my.dw.ccl.domain.Deck;
import my.dw.ccl.domain.Format;

@Getter
public class YgoDeck extends Deck {

  private final YearMonth banList;
  private final boolean ported;
  private final boolean ocg;

  public YgoDeck(
      final String name,
      final Format format,
      final LocalDate implementedDate,
      final boolean active,
      final String shared,
      final boolean custom,
      final YearMonth banList,
      final boolean ported,
      final boolean ocg) {
    super(name, format, implementedDate, active, shared, custom);
    this.banList = banList;
    this.ported = ported;
    this.ocg = ocg;
  }

  @Override
  public String toString() {
    if (getFormat() != Format.CROSS_BANLIST) {
      return getName();
    }

    return (banList != null ? banList.format(DateTimeFormatter.ofPattern("yyyy-MM")) : "")
        + (isCustom() ? "C" : "")
        + (ocg ? "O" : "")
        + (ported ? "X" : "")
        + (isNotEmpty(getShared()) ? "Z" : "")
        + " - " + getName();
  }

}
