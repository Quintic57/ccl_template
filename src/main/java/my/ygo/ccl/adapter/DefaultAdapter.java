package my.ygo.ccl.adapter;

import my.ygo.ccl.dto.Item;

import java.util.Set;

public class DefaultAdapter implements Adapter {

    @Override
    public Set<Item> extractPackages() {
        return Set.of();
    }

    @Override
    public Double extractTotalPrice() {
        return 0.0;
    }

}
