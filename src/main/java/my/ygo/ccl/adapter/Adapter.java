package my.ygo.ccl.adapter;

import my.ygo.ccl.dto.Item;

import java.util.Set;

public interface Adapter {

    Set<Item> extractPackages();

    Double extractTotalPrice();

}
