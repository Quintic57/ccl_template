package my.dw.ccl.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CardInList {

    private final String name;

    // Total quantity per shop
    private Integer quantity;
    // How much of the quantity is in cart
    private Map<Vendor, Integer> quantityInCart;

    // Used when splitting quantityMap by shop
    public CardInList(final String name, final Integer quantity) {
        this.name = name;
        this.quantity = quantity;
        this.quantityInCart = new HashMap<>();
    }

    public void addQuantityInCart(final Vendor vendor, final Integer quantity) {
        quantityInCart.put(vendor, quantityInCart.getOrDefault(vendor, 0) + quantity);
    }

}
