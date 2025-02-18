package my.dw.ccl.adapter;

import my.dw.ccl.dto.Item;

import java.util.Set;

public interface Adapter {

    Set<Item> extractPackages();

    Double extractTotalPrice();

    void extractVendors(final Set<String> vendors);

}
