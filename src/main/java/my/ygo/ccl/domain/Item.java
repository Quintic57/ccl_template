package my.ygo.ccl.domain;

import lombok.Data;

@Data
public class Item {

    private final String cardName;

    private final Integer quantity;

    private final Double price;

}
