package my.dw.ccl.domain;

import my.dw.ccl.adapter.Adapter;
import my.dw.ccl.adapter.shop.CoolstuffAdapter;
import my.dw.ccl.adapter.DefaultAdapter;
import my.dw.ccl.adapter.shop.TcgPlayerAdapter;

import java.util.HashSet;
import java.util.Set;

import static my.dw.ccl.dto.Item.MAIN_VENDOR_NAME;

public enum Shop {

    COOLSTUFF("Coolstuff", "a", "+", new CoolstuffAdapter()),
    TCG_PLAYER("TCGplayer", "b", "^", new TcgPlayerAdapter(), new HashSet<>()),
    TROLL_AND_TOAD("Troll and Toad", "c", "/"),
    EBAY("eBay", "d", "{");

    private final String name;

    private final String prefix;

    private final String identifier;

    private final Set<String> vendors;

    private final Adapter adapter;

    Shop(final String name, final String prefix, final String identifier) {
        this(name, prefix, identifier, new DefaultAdapter(), Set.of());
    }

    Shop(final String name, final String prefix, final String identifier, final Adapter adapter) {
        this(name, prefix, identifier, adapter, Set.of(MAIN_VENDOR_NAME));
    }

    Shop(final String name,
         final String prefix,
         final String identifier,
         final Adapter adapter,
         final Set<String> vendors) {
        this.name = name;
        this.prefix = prefix;
        this.identifier = identifier;
        this.adapter = adapter;
        this.vendors = vendors;

        adapter.extractVendors(vendors);
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

    public Set<String> getVendors() {
        return vendors;
    }

}
