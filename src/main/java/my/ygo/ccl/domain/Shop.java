package my.ygo.ccl.domain;

import my.ygo.ccl.adapter.Adapter;
import my.ygo.ccl.adapter.shop.CoolstuffAdapter;
import my.ygo.ccl.adapter.DefaultAdapter;
import my.ygo.ccl.adapter.shop.TcgPlayerAdapter;

public enum Shop {

    COOLSTUFF("Coolstuff", "a", "+", new CoolstuffAdapter()),
    TCG_PLAYER("TCGplayer", "b", "^", new TcgPlayerAdapter()),
    TROLL_AND_TOAD("Troll and Toad", "c", "/"),
    EBAY("eBay", "d", "{");

    private final String name;

    private final String prefix;

    private final String identifier;

    private final Adapter adapter;

    Shop(final String name, final String prefix, final String identifier) {
        this(name, prefix, identifier, new DefaultAdapter());
    }

    Shop(final String name, final String prefix, final String identifier, final Adapter adapter) {
        this.name = name;
        this.prefix = prefix;
        this.identifier = identifier;
        this.adapter = adapter;
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

    public Adapter getAdapter() {
        return adapter;
    }

}
