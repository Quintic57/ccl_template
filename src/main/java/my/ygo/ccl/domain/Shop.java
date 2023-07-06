package my.ygo.ccl.domain;

public enum Shop {

    COOLSTUFF("Coolstuff", "a", "+"),
    TROLL_AND_TOAD("Troll and Toad", "b", "/"),
    TCG_PLAYER("TCGplayer", "c", "^"),
    EBAY("eBay", "d", "{");

    private final String name;

    private final String prefix;

    private final String identifier;

    Shop(final String name, final String prefix, final String identifier) {
        this.name = name;
        this.prefix = prefix;
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getIdentifier() {
        return identifier;
    }

}
