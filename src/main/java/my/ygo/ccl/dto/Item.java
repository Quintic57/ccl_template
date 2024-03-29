package my.ygo.ccl.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class Item {

    private final String cardName;

    private final Integer quantity;

    private final Double price;

    private final String packageName;

    public Item(final String cardName, final Integer quantity) {
        this(cardName, quantity, 0.0, "N/A");
    }

    public Item(final String cardName, final Integer quantity, final Double price, final String packageName) {
        this.cardName = cardName;
        this.quantity = quantity;
        this.price = price;
        this.packageName = packageName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Item item = (Item) o;

        return cardName.equals(item.cardName) && quantity.equals(item.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardName, quantity);
    }

    @Override
    public String toString() {
        return this.quantity + "x " + this.cardName;
    }

    public static Item mergeItems(final Item item1, final Item item2) {
        if (!item1.getCardName().equals(item2.getCardName())) {
            throw new IllegalArgumentException();
        }

        return new Item(item1.getCardName(),
            item1.getQuantity() + item2.getQuantity(),
            item1.getPrice() + item2.getPrice(),
            "[multiple stores]");
    }

}
