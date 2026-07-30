package my.dw.ccl.adapter.shop;

import my.dw.ccl.adapter.shop.dto.Item;

import java.util.Collection;
import java.util.Set;

public interface Adapter {

    Collection<Item> extractPackages();

    Double extractTotalPrice();

    void extractVendors(final Set<String> vendors);

}
