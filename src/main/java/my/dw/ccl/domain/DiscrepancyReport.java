package my.dw.ccl.domain;

import lombok.Data;

import java.util.Map;

@Data
public class DiscrepancyReport {

    private final Map<String, Integer> itemsFromCart;

    private final Map<String, Integer> excludedFromCart;

    private final Map<String, Integer> excludedFromList;

}
