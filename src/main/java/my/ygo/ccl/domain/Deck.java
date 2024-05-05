package my.ygo.ccl.domain;

import lombok.Getter;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

//TODO: Have this pull from duelingbook OR google sheets instead
public enum Deck {
    // Cross Banlist
    JUNK_DOPPEL("Junk Doppel", YearMonth.of(2011, Month.MARCH), Format.CROSS_BANLIST, false),
    GLADIATOR_BEASTS("Glad Beasts", YearMonth.of(2008, Month.SEPTEMBER), Format.CROSS_BANLIST, false),
    INFERNITY("Infernity", YearMonth.of(2013, Month.MARCH), Format.CROSS_BANLIST, false),
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
    HIERATIC_RULERS("Hieratic Rulers", YearMonth.of(2014, Month.JANUARY), Format.CROSS_BANLIST, false),
    TELLARKNIGHTS("Tellarknights", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, LocalDate.of(2022, Month.APRIL, 14), true),
    MAGISTUS("Magistus", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, LocalDate.of(2022, Month.SEPTEMBER, 4), true),
    WITCHCRAFTERS("Witchcrafters", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, LocalDate.of(2022, Month.NOVEMBER, 1), true),
    ADAMANCIPATORS("Adamancipators", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, LocalDate.of(2023, Month.JUNE, 17), true),
    PALEOZOIC_FISH("Paleozoic Fish", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    INFERNITY_PORTED("Infernity", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    CHAOS("Chaos", YearMonth.of(2014, Month.JULY), Format.CROSS_BANLIST, true),
    // Modern
    ADAMANCIPATORS_MODERN("Adamancipators", YearMonth.of(2024, Month.APRIL), Format.MODERN),
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
    private final YearMonth banlist;
    @Getter
    private final Format format;
    private final LocalDate implementedDate;
    private final boolean ported;
    @Getter
    private final boolean active;

    Deck(final String name,
         final YearMonth banlist,
         final Format format) {
        this(name, banlist, format, false);
    }

    Deck(final String name,
         final YearMonth banlist,
         final Format format,
         final boolean ported) {
        this(name, banlist, format, ported, true);
    }

    Deck(final String name,
         final YearMonth banlist,
         final Format format,
         final boolean ported,
         final boolean active) {
        this(name, banlist, format, LocalDate.of(2019, Month.JULY, 21), ported, active);
    }

    Deck(final String name,
         final YearMonth banlist,
         final Format format,
         final LocalDate implementedDate,
         final boolean ported) {
        this(name, banlist, format, implementedDate, ported, true);
    }

    Deck(final String name,
         final YearMonth banlist,
         final Format format,
         final LocalDate implementedDate,
         final boolean ported,
         final boolean active) {
        this.name = name;
        this.banlist = banlist;
        this.format = format;
        this.implementedDate = implementedDate;
        this.ported = ported;
        this.active = active;
    }

    public String getBanlistAsString() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        final String text = banlist.format(formatter);
        return text;
    }

    public static Map<Format, Map<Deck, List<Card>>> generateEmptyDeckBuyListMap() {
        final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList = new LinkedHashMap<>();
        for (Format format: Format.getListedFormats()) {
            final Map<Deck, List<Card>> buyList = Arrays.stream(Deck.values())
                .filter(Deck::isActive)
                .filter(deck -> deck.getFormat().equals(format) || deck.getFormat().equals(Format.UNLISTED))
                .collect(Collectors.toMap(
                    deck -> deck,
                    deck -> new ArrayList<>(),
                    (u, v) -> {
                        throw new IllegalStateException(String.format("Duplicate key %s", u));
                    },
                    LinkedHashMap::new)
                );
            formatToDeckBuyList.put(format, buyList);
        }

        return formatToDeckBuyList;
    }

}
