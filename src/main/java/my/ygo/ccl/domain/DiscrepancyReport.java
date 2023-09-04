package my.ygo.ccl.domain;

import lombok.Data;

import java.util.Map;

@Data
public class DiscrepancyReport {

    private final Map<String, Integer> itemsFromCart;

    private final Map<String, Integer> itemsExcludedFromList;

    private final Map<String, Integer> itemsExcludedFromCart;

}
