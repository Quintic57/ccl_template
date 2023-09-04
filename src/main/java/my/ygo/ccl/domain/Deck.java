package my.ygo.ccl.domain;

import java.time.LocalDate;
import java.time.Month;

//TODO: Have this pull from duelingbook OR google sheets instead
public enum Deck {
    JUNK_DOPPEL("Junk Doppel", "2011 March", Format.CROSS_BANLIST, false),
    GLADIATOR_BEASTS("Glad Beasts", "2008 September", Format.CROSS_BANLIST, false),
    INFERNITY("Infernity", "2013 March", Format.CROSS_BANLIST, false),
    QD_DANDYWARRIOR("Quickdraw Dandywarrior", "2010 September", Format.CROSS_BANLIST, false),
    KARAKURI_MACHINA_PLANT("Karakuri Machina Plant", "2010 September", Format.CROSS_BANLIST, false),
    FUSION_HERO("Fusion HERO", "2012 March", Format.CROSS_BANLIST, false),
    LIGHTSWORNS("Dawn of the Lightsworns", "2014 July", Format.CROSS_BANLIST, false),
    AA_BATTERIES("AA Batteries", "2014 July", Format.CROSS_BANLIST, true),
    TENGU_PLANT("Tengu Plant", "2011 March", Format.CROSS_BANLIST, false),
    RANK_UP_GADGETS("Rank-Up Gadgets", "2014 July", Format.CROSS_BANLIST, true),
    ROCK_STUN("Rock Stun", "2014 July", Format.CROSS_BANLIST, true),
    FROG_MONARCHS("Frog Monarchs", "2014 July", Format.CROSS_BANLIST, true),
    SAFFIRA_RITUAL("Saffira Ritual", "2014 July", Format.CROSS_BANLIST, true),
    SPELLBOOK_OF_PROPHECY("Spellbook of Prophecy", "2013 March", Format.CROSS_BANLIST, false),
    PACMAN_STUN("PACMAN Stun", "2012 March", Format.CROSS_BANLIST, false),
    QUASAR_SYNCHRON("Quasar Synchron", "2011 September", Format.CROSS_BANLIST, false),
    CHAOS_DRAGONS("Chaos Dragons", "2012 March", Format.CROSS_BANLIST, false),
    MYTHIC_RULERS("Mythic Rulers", "2013 October", Format.CROSS_BANLIST, false),
    BROTHERHOOD_OF_KOAKI_MEIRU("Brotherhood of Koa’ki Meiru", "2014 July", Format.CROSS_BANLIST, false),
    DAD_RETURN("DAD Return", "2008 March", Format.CROSS_BANLIST, false),
    ZOMBIE_TELEDAD("Zombie TeleDAD", "2008 September", Format.CROSS_BANLIST, false),
    NIMBLE_FROGS("Nimble Frogs", "2014 July", Format.CROSS_BANLIST, true),
    SYLVANS("Sylvans", "2014 July", Format.CROSS_BANLIST, true),
    GUSTO_DRAGUNITY("Gusto Dragunity", "2013 March", Format.CROSS_BANLIST, false),
    MAGICAL_DIMEX("Magical Dimex", "2014 July", Format.CROSS_BANLIST, true),
    WINDUPS("Windups", "2012 September", Format.CROSS_BANLIST, false),
    TRAINS("Trains", "2014 July", Format.CROSS_BANLIST, true),
    ROCKET_BARRAGE("Rocket Barrage", "2014 July", Format.CROSS_BANLIST, true),
    REKINDLING("Rekindling", "2013 March", Format.CROSS_BANLIST, true),
    AIRBLADE_TURBO("Airblade Turbo", "2006 September", Format.CROSS_BANLIST, false),
    REAPERBOOKS("Reaperbooks", "2013 March", Format.CROSS_BANLIST, true),
    ALIENS("Aliens", "2014 July", Format.CROSS_BANLIST, true),
    DEEZE_FROGS("Deeze Frogs", "2014 July", Format.CROSS_BANLIST, false),
    SYNCHRON_MASH("Synchron Mash", "2014 July", Format.CROSS_BANLIST, true),
    BABY_RULERS("Baby Rulers", "2013 March", Format.CROSS_BANLIST, false),
    HIERATIC_RULERS("Hieratic Rulers", "2014 January", Format.CROSS_BANLIST, false),
    TELLARKNIGHTS("Tellarknights", "2014 July", Format.CROSS_BANLIST, LocalDate.of(2022, Month.APRIL, 14), true),
    MAGISTUS("Magistus", "2014 July", Format.CROSS_BANLIST, LocalDate.of(2022, Month.SEPTEMBER, 4), true),
    WITCHCRAFTERS("Witchcrafters", "2014 July", Format.CROSS_BANLIST, LocalDate.of(2022, Month.NOVEMBER, 1), true),
    ADAMANCIPATORS("Adamancipators", "2014 July", Format.CROSS_BANLIST, LocalDate.of(2023, Month.JUNE, 17), true),
    MODERN_ADAMANCIPATORS("Adamancipators", "2023 June", Format.MODERN),
    CONSTELLARKNIGHTS("Constellarknights", "2023 June", Format.MODERN),
    GHOTI("Ghoti", "2023 June", Format.MODERN),
    MACHINA_GEARTOWN("Machina Geartown", "2010 March", Format.EDISON),
    KEYWORD_STAPLES("[Staples]", "2002 March", Format.UNLISTED),
    KEYWORD_UNLISTED("[Unlisted]", "2002 March", Format.UNLISTED);

    private final String name;
    private final String banlist;
    private final Format format;
    private final LocalDate implementedDate;
    private final boolean ported;

    Deck(final String name,
                 final String banlist,
                 final Format format) {
        this(name, banlist, format, false);
    }

    Deck(final String name,
                 final String banlist,
                 final Format format,
                 final boolean ported) {
        this(name, banlist, format, LocalDate.of(2019, Month.JULY, 21), ported);
    }

    Deck(final String name,
                 final String banlist,
                 final Format format,
                 final LocalDate implementedDate,
                 final boolean ported) {
        this.name = name;
        this.banlist = banlist;
        this.format = format;
        this.implementedDate = implementedDate;
        this.ported = ported;
    }

    public String getName() {
        return name;
    }

    public Format getFormat() {
        return format;
    }

}
