package my.ygo.ccl.domain;

import lombok.Getter;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

//TODO: Have this pull from duelingbook OR google sheets instead
public enum Deck {
    // Cross Banlist
    JUNK_DOPPEL("Junk Doppel", YearMonth.of(2011, Month.MARCH), Format.CROSS_BANLIST, false),
    GLADIATOR_BEASTS("Glad Beasts", YearMonth.of(2008, Month.SEPTEMBER), Format.CROSS_BANLIST, false),
    INFERNITY("Infernity", YearMonth.of(2013, Month.MARCH), Format.CROSS_BANLIST, false, "INFERNITY_PORTED"),
    QD_DANDYWARRIOR("Quickdraw Dandywarrior", YearMonth.of(2010, Month.SEPTEMBER), Format.CROSS_BANLIST, false),
    KARAKURI_MACHINA_PLANT("Karakuri Machina Plant", YearMonth.of(2010, Month.SEPTEMBER), Format.CROSS_BANLIST, false),
    FUSION_HERO("Fusion HERO", YearMonth.of(2012, Month.MARCH), Format.CROSS_BANLIST, false),
    LIGHTSWORNS("Dawn of the Lightsworns", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, false),
    AA_BATTERIES("AA Batteries", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    TENGU_PLANT("Tengu Plant", YearMonth.of(2011, Month.MARCH), Format.CROSS_BANLIST, false),
    RANK_UP_GADGETS("Rank-Up Gadgets", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    ROCK_STUN("Rock Stun", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    FROG_MONARCHS("Frog Monarchs", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    SAFFIRA_RITUAL("Saffira Ritual", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    SPELLBOOK_OF_PROPHECY("Spellbook of Prophecy", YearMonth.of(2013, Month.MARCH), Format.CROSS_BANLIST, false),
    PACMAN_STUN("PACMAN Stun", YearMonth.of(2012, Month.MARCH), Format.CROSS_BANLIST, false, false),
    QUASAR_SYNCHRON("Quasar Synchron", YearMonth.of(2011, Month.SEPTEMBER), Format.CROSS_BANLIST, false),
    CHAOS_DRAGONS("Chaos Dragons", YearMonth.of(2012, Month.MARCH), Format.CROSS_BANLIST, false),
    MYTHIC_RULERS("Mythic Rulers", YearMonth.of(2013, Month.OCTOBER), Format.CROSS_BANLIST, false),
    BROTHERHOOD_OF_KOAKI_MEIRU("Brotherhood of Koa’ki Meiru", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, false),
    DAD_RETURN("DAD Return", YearMonth.of(2008, Month.MARCH), Format.CROSS_BANLIST, false),
    ZOMBIE_TELEDAD("Zombie TeleDAD", YearMonth.of(2008, Month.SEPTEMBER), Format.CROSS_BANLIST, false),
    LEMURIA_FROGS("Lemuria Frogs", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    SYLVANS("Sylvans", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    GUSTO_DRAGUNITY("Gusto Dragunity", YearMonth.of(2013, Month.MARCH), Format.CROSS_BANLIST, false),
    MAGICAL_DIMEX("Magical Dimex", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    WINDUPS("Windups", YearMonth.of(2012, Month.SEPTEMBER), Format.CROSS_BANLIST, false),
    TRAINS("Trains", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    ROCKET_BARRAGE("Rocket Barrage", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    REKINDLING("Rekindling", YearMonth.of(2013, Month.MARCH), Format.CROSS_BANLIST, true),
    AIRBLADE_TURBO("Airblade Turbo", YearMonth.of(2006, Month.SEPTEMBER), Format.CROSS_BANLIST, false),
    REAPERBOOKS("Reaperbooks", YearMonth.of(2013, Month.MARCH), Format.CROSS_BANLIST, true),
    ALIENS("Aliens", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    DEEZE_FROGS("Deeze Frogs", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, false),
    SYNCHRON_MASH("Synchron Mash", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    BABY_RULERS("Baby Rulers", YearMonth.of(2013, Month.MARCH), Format.CROSS_BANLIST, false),
    HIERATIC_RULERS("Hieratic Rulers", YearMonth.of(2014, Month.JANUARY), Format.CROSS_BANLIST, false, "MYTHIC_RULERS"),
    TELLARKNIGHTS("Tellarknights", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, LocalDate.of(2022, Month.APRIL, 14), true),
    MAGISTUS("Magistus", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, LocalDate.of(2022, Month.SEPTEMBER, 4), true),
    WITCHCRAFTERS("Witchcrafters", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, LocalDate.of(2022, Month.NOVEMBER, 1), true),
    ADAMANCIPATORS("Adamancipators", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, LocalDate.of(2023, Month.JUNE, 17), true),
    PALEOZOIC_FISH("Paleozoic Fish", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    INFERNITY_PORTED("Infernity", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    CHAOS("Chaos", YearMonth.of(2018, Month.SEPTEMBER), Format.CROSS_BANLIST, true),
    PALEOZOIC_JACK("Paleozoic Jack", YearMonth.of(2024, Month.APRIL), Format.CROSS_BANLIST, false, "PALEOZOIC_FISH"),
    // Modern
    ADAMANCIPATORS_MODERN("Adamancipators", YearMonth.of(2024, Month.JANUARY), Format.MODERN),
    CONSTELLARKNIGHTS("Constellarknights", YearMonth.of(2023, Month.JUNE), Format.MODERN),
    GHOTI("Ghoti", YearMonth.of(2024, Month.APRIL), Format.MODERN),
    // Edison
    ALIEN_CONTROL("Alien Control", YearMonth.of(2010, Month.MARCH), Format.EDISON),
    MACHINA_GEARTOWN("Machina Geartown", YearMonth.of(2010, Month.MARCH), Format.EDISON),
    QD_DANDYWARRIOR_EDISON("Quickdraw Dandy", YearMonth.of(2010, Month.MARCH), Format.EDISON),
    // Keywords
    KEYWORD_STAPLES("[Staples]", YearMonth.of(2002, Month.MARCH), Format.UNLISTED),
    KEYWORD_UNLISTED("[Unlisted]", YearMonth.of(2002, Month.MARCH), Format.UNLISTED);

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

    Deck(final String name,
         final YearMonth banList,
         final Format format) {
        this(name, banList, format, false);
    }

    Deck(final String name,
         final YearMonth banList,
         final Format format,
         final boolean ported) {
        this(name, banList, format, ported, true);
    }

    Deck(final String name,
         final YearMonth banList,
         final Format format,
         final boolean ported,
         final boolean active) {
        this(name, banList, format, LocalDate.of(2019, Month.JULY, 21), ported, active, "");
    }

    Deck(final String name,
         final YearMonth banList,
         final Format format,
         final boolean ported,
         final String shared) {
        this(name, banList, format, LocalDate.of(2019, Month.JULY, 21), ported, true, shared);
    }

    Deck(final String name,
         final YearMonth banList,
         final Format format,
         final LocalDate implementedDate,
         final boolean ported) {
        this(name, banList, format, implementedDate, ported, true, "");
    }

    Deck(final String name,
         final YearMonth banList,
         final Format format,
         final LocalDate implementedDate,
         final boolean ported,
         final boolean active,
         final String shared) {
        this.name = name;
        this.banList = banList;
        this.format = format;
        this.implementedDate = implementedDate;
        this.ported = ported;
        this.active = active;
        this.shared = shared;
    }

    @Override
    public String toString() {
        if (format != Format.CROSS_BANLIST) {
            return name;
        }

        return (banList != null ? banList.format(DateTimeFormatter.ofPattern("yyyy-MM")) : "")
            + (ported ? "X" : "")
            + (isNotEmpty(shared) ? "Z" : "")
            + " - " + name;
    }

    public static Map<String, Deck> getDeckStringToObjectMapForFormat(final Format format) {
        return Arrays.stream(Deck.values())
            .filter(deck -> format == deck.getFormat() || deck.getFormat().equals(Format.UNLISTED))
            .collect(Collectors.toMap(Deck::toString, deck -> deck, (o1, o2) -> o1, LinkedHashMap::new));
    }

}
