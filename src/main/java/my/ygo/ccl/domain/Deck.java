package my.ygo.ccl.domain;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import lombok.Getter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

//TODO: Have this pull from duelingbook OR google sheets instead
public class Deck {

    @Getter
    private final String name;
    private final YearMonth banList;
    @Getter
    private final Format format;
    private final LocalDate implementedDate;
    private final boolean ported;
    private final String shared;
    @Getter
    private final boolean active;
    private final boolean tcg;

    Deck(final String name,
         final YearMonth banList,
         final Format format) {
        this(name, banList, format, null, false, true, "", true);
    }

    Deck(final String name,
         final YearMonth banList,
         final Format format,
         final LocalDate implementedDate,
         final boolean ported,
         final boolean active,
         final String shared,
         final boolean tcg) {
        this.name = name;
        this.banList = banList;
        this.format = format;
        this.implementedDate = implementedDate;
        this.ported = ported;
        this.active = active;
        this.shared = shared;
        this.tcg = tcg;
    }

    @Override
    public String toString() {
        if (format != Format.CROSS_BANLIST) {
            return name;
        }

        return (banList != null ? banList.format(DateTimeFormatter.ofPattern("yyyy-MM")) : "")
            + (!tcg ? "O" : "")
            + (ported ? "X" : "")
            + (isNotEmpty(shared) ? "Z" : "")
            + " - " + name;
    }

}
