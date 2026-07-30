package my.dw.ccl.domain;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public abstract class Deck {

    private final String name;
    private final Format format;
    private final LocalDate implementedDate;
    private final String shared;
    private final boolean active;
    private final boolean custom;

    public Deck(final String name,
         final Format format,
         final LocalDate implementedDate,
         final boolean active,
         final String shared,
         final boolean custom) {
        this.name = name;
        this.format = format;
        this.implementedDate = implementedDate;
        this.active = active;
        this.shared = shared;
        this.custom = custom;
    }

}
