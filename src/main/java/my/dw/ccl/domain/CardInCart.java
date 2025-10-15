package my.dw.ccl.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class CardInCart {

    private final String name;

    private final Map<Vendor, Integer> quantityMap;

    private final Map<Vendor, Double> priceMap;

    public CardInCart(final String name, final Map<Vendor, Integer> quantityMap, final Map<Vendor, Double> priceMap) {
        this.name = name;
        this.quantityMap = new HashMap<>(quantityMap);
        this.priceMap = new HashMap<>(priceMap);
    }

    public static CardInCart merge(final CardInCart card1, final CardInCart card2) {
        card1.quantityMap.putAll(card2.getQuantityMap());
        card1.priceMap.putAll(card2.getPriceMap());
        return card1;
    }

    public void removeQuantity(final Vendor vendor, final Integer quantity) {
        if (!quantityMap.containsKey(vendor) || quantityMap.get(vendor) < quantity) {
            throw new IllegalArgumentException("Can not remove quantity if vendor DNE or if stored quantity is smaller than target quantity");
        }
        quantityMap.put(vendor, quantityMap.get(vendor) - quantity);
    }

    public Integer getTotalQuantity() {
        return quantityMap.values().stream().mapToInt(quantity -> quantity).sum();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final CardInCart cardInCart = (CardInCart) o;

        return name.equals(cardInCart.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
