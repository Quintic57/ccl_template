package my.dw.ccl.adapter;

import my.dw.ccl.dto.Item;

import java.util.Collection;
import java.util.Set;

public class DefaultAdapter implements Adapter {

    @Override
    public Collection<Item> extractPackages() {
        return Set.of();
    }

    @Override
    public Double extractTotalPrice() {
        return 0.0;
    }

    @Override
    public void extractVendors(final Set<String> vendors) {
        // do nothing
    }

}
