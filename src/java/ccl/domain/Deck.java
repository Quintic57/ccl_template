package ccl.domain;

public enum Deck {
    JUNK_DOPPEL("Junk Doppel", "2011 March", 1),
    GLADIATOR_BEASTS("GB", "2008 September", 2),
    INFERNITY("Infernity", "2013 March", 3),
    SYNCHRON_MASH("Synchron Mash", "2010 September", 4),
    KARAKURI_MACHINA_PLANT("KMP", "2010 September", 5),
    FUSION_HERO("Fusion HERO", "2012 March", 6),
    DRAGONSWORN("Dragonsworn", "2012 March", 7),
    BATTERYMAN_OTK("Batteryman OTK", "2014 July", 8),
    TENGU_PLANT("Tengu Plant", "2011 March", 9),
    MACHINA_GADGETS("Machina Gadgets", "2013 March", 10),
    BROTHERHOOD_OF_KOAKI_MEIRU("Brotherhood of KM", "2014 July", 11),
    FROG_MONARCHS("Frog Monarchs", "2014 July", 12),
    HIERATIC_SAFFIRA("Hieratic Saffira", "2014 July", 13),
    SPELLBOOK_OF_PROPHECY("Spellbook of Prophecy", "2014 July", 14),
    PACMAN_STUN("PACMAN Stun", "2012 March", 15),
    QUASAR_SYNCHRON("Quasar Synchron", "2011 September", 16),
    MYTHIC_RULERS("Mythic Rulers", "2013 October", 17),
    ROCK_STUN("Rock Stun", "2014 July", 18),
    DAD_RETURN("DAD Return", "2008 March", 19);

    private String name;
    private String format;
    private int number;

    private Deck(String name, String format, int number) {
        this.name = name;
        this.format = format;
        this.number = number;
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
