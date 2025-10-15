package my.dw.ccl.domain;

import lombok.Data;

@Data
public class CardInReport {

    private final String name;

    private final Integer quantity;

    private final Deck deck;

    public CardInReport(final String name, final Integer quantity, final Deck deck) {
        this.name = name;
        this.quantity = quantity;
        this.deck = deck;
    }

    @Override
    public String toString() {
        return quantity + "x " + name;
    }
}
