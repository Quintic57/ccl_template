package my.dw.ccl.dto;

import lombok.Data;
import my.dw.ccl.domain.Vendor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Data
public class Item {

    private final String cardName;

    private final Integer quantity;

    private final Double price;

    private final Vendor vendor;

    public Item(final String cardName, final Integer quantity) {
        this(cardName, quantity, 0.0, null);
    }

    public Item(final String cardName, final Integer quantity, final Double price, final Vendor vendor) {
        this.cardName = cardName;
        this.quantity = quantity;
        this.price = price;
        this.vendor = vendor;
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

        return cardName.equals(item.cardName)
            && quantity.equals(item.quantity)
            && vendor.equals(item.vendor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardName, quantity, vendor);
    }

    @Override
    public String toString() {
        return this.quantity + "x " + this.cardName;
    }

    public String getCardAndVendorName() {
        return cardName + " / " + vendor;
    }

    public static Item mergeItems(final Item item1, final Item item2) {
        if (!item1.getCardName().equals(item2.getCardName())) {
            throw new IllegalArgumentException();
        }

        final BigDecimal roundedPrice = new BigDecimal(Double.toString((item1.getPrice() + item2.getPrice()) / 2.0));
        return new Item(item1.getCardName(),
            item1.getQuantity() + item2.getQuantity(),
            roundedPrice.setScale(2, RoundingMode.HALF_UP).doubleValue(),
            item1.getVendor());
    }

}
