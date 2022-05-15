package my.ygo.ccl.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Deck {
    JUNK_DOPPEL("Junk Doppel", "2011 March", 1),
    GLADIATOR_BEASTS("Glad Beasts", "2008 September", 2),
    INFERNITY("Infernity", "2013 March", 3),
    QD_DANDYWARRIOR("Quickdraw Dandywarrior", "2010 September", 4),
    KARAKURI_MACHINA_PLANT("Karakuri Machina Plant", "2010 September", 5),
    FUSION_HERO("Fusion HERO", "2012 March", 6),
    LIGHTSWORNS("Dawn of the Lightsworns", "2014 July", 7),
    AA_BATTERIES("AA Batteries", "2014 August", 8),
    TENGU_PLANT("Tengu Plant", "2011 March", 9),
    RANK_UP_GADGETS("Rank-Up Gadgets", "2014 August", 10),
    ROCK_STUN("Rock Stun", "2014 August", 11),
    FROG_MONARCHS("Frog Monarchs", "2014 August", 12),
    SAFFIRA_RITUAL("Saffira Ritual", "2014 August", 13),
    SPELLBOOK_OF_PROPHECY("Spellbook of Prophecy", "2013 March", 14),
    PACMAN_STUN("PACMAN Stun", "2012 March", 15),
    QUASAR_SYNCHRON("Quasar Synchron", "2011 September", 16),
    CHAOS_DRAGONS("Chaos Dragons", "2012 March", 17),
    MYTHIC_RULERS("Mythic Rulers", "2013 October", 18),
    BROTHERHOOD_OF_KOAKI_MEIRU("Brotherhood of Koa’ki Meiru", "2014 July", 19),
    DAD_RETURN("DAD Return", "2008 March", 20),
    ZOMBIE_TELEDAD("Zombie TeleDAD", "2008 September", 21),
    NIMBLE_FROGS("Nimble Frogs", "2014 August", 22),
    SYLVANS("Sylvans", "2014 August", 23),
    GUSTO_DRAGUNITY("Gusto Dragunity", "2013 March", 24),
    CITADEL_OF_EXEMPLAR("Citadel of Exemplar", "2014 July", 25),
    WINDUPS("Windups", "2012 September", 26),
    TRAINS("Trains", "2014 August", 27),
    ROCKET_BARRAGE("Rocket Barrage", "2014 August", 28),
    REKINDLING("Rekindling", "2014 August", 29),
    AIRBLADE_TURBO("Airblade Turbo", "2006 September", 30),
    REAPERBOOKS("Reaperbooks", "2014 August", 31),
    ALIENS("Aliens", "2014 August", 32),
    DEEZE_FROGS("Deeze Frogs", "2014 July", 33),
    SYNCHRON_MASH("Synchron Mash", "2014 August", 34),
    BABY_RULERS("Baby Rulers", "2013 March", 35),
    HIERATIC_RULERS("Hieratic Rulers", "2014 January", 36);

    private String name;
    private String format;
    private int index;

    private Deck(String name, String format, int index) {
        this.name = name;
        this.format = format;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public static List<String> getDeckNames() {
        return Arrays.stream(values()).map(deck -> deck.getName()).collect(Collectors.toList());
    }
}
