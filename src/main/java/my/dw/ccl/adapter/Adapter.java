package my.dw.ccl.adapter;

import my.dw.ccl.dto.Item;

import java.util.Collection;
import java.util.Set;

public interface Adapter {

    Collection<Item> extractPackages();

    Double extractTotalPrice();

    void extractVendors(final Set<String> vendors);

}
