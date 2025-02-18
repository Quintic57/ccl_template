package my.dw.ccl.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class Item {

    public static String MAIN_VENDOR_NAME = "Main Vendor";

    private final String cardName;

    private final Integer quantity;

    private final Double price;

    private final String vendorName;

    public Item(final String cardName, final Integer quantity) {
        this(cardName, quantity, 0.0, "N/A");
    }

    public Item(final String cardName, final Integer quantity, final Double price, final String vendorName) {
        this.cardName = cardName;
        this.quantity = quantity;
        this.price = price;
        this.vendorName = vendorName;
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

        return cardName.equals(item.cardName) && quantity.equals(item.quantity) && vendorName.equals(item.vendorName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardName, quantity, vendorName);
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
            String.join(" / ", item1.getVendorName(), item2.getVendorName()));
    }

}
