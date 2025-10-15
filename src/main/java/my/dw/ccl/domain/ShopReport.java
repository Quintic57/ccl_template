package my.dw.ccl.domain;

import io.micrometer.common.util.StringUtils;
import lombok.Data;
import lombok.Setter;
import my.dw.ccl.dto.Item;

import java.sql.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Setter
public class ShopReport {

    private final Shop shop;

    private final StringBuilder header;

    private final StringBuilder body;

    private final StringBuilder footer;

    public ShopReport(final Shop shop) {
        this.shop = shop;
        header = new StringBuilder();
        body = new StringBuilder();
        footer = new StringBuilder();
    }

    public void generateReportHeader() {
        header.append(shop.getPrefix()).append(") ").append(shop.getName()).append("\n");
    }

    public void generateReportBody(final Map<Deck, List<CardInList>> shopBuyList,
                                   final Map<Vendor, Map<String, Integer>> notInList,
                                   final Map<Vendor, Integer> cardCount) {
        // TODO: Can probably simplify/restructure this
        final Map<Vendor, List<CardInReport>> shopBuyListByVendor = new HashMap<>();
        for (final Deck deck: shopBuyList.keySet()) {
            for (final CardInList card: shopBuyList.get(deck)) {
                card.getQuantityInCart().forEach(((vendor, cardQuantity) -> {
                    if (shopBuyListByVendor.containsKey(vendor)) {
                        shopBuyListByVendor.get(vendor).add(new CardInReport(card.getName(), cardQuantity, deck));
                    } else {
                        shopBuyListByVendor.put(vendor,
                            new ArrayList<>(List.of(new CardInReport(card.getName(), cardQuantity, deck))));
                    }
                }));
            }
        }

        shopBuyListByVendor.forEach((vendor, cardsInList) -> {
            final int quantityInCartAndList = cardsInList.stream().mapToInt(CardInReport::getQuantity).sum();
            final int quantityInCartButNotList = notInList.containsKey(vendor)
                ? notInList.get(vendor).values().stream().mapToInt(quantity -> quantity).sum()
                : 0;
            cardCount.put(vendor, quantityInCartAndList + quantityInCartButNotList);
            body.append(vendor).append(" - ").append(quantityInCartAndList + quantityInCartButNotList).append(" card(s)\n");
            cardsInList
                .stream()
                .collect(Collectors.groupingBy(
                    card -> card.getDeck().getFormat(),
                    Collectors.groupingBy(
                        CardInReport::getDeck,
                        Collectors.toList()
                    )
                ))
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getName()))
                .forEach(formatEntry -> {
                    body.append("  {").append(formatEntry.getKey().toString()).append("}").append("\n");
                    formatEntry.getValue()
                        .entrySet()
                        .stream()
                        .sorted(Comparator.comparing(deckEntry -> deckEntry.getKey().toString()))
                        .forEach(deckEntry -> {
                            body.append("  ").append(deckEntry.getKey().toString()).append("\n");
                            deckEntry.getValue().forEach(card -> body.append("    ").append(card.toString()).append("\n"));
                    });
                });

            if (notInList.containsKey(vendor)) {
                body.append("  ").append("{Not in List}").append("\n");
                notInList.get(vendor).keySet()
                    .stream()
                    .sorted()
                    .forEach(cardName -> {
                        final Integer quantity = notInList.get(vendor).get(cardName);
                        body.append("    ").append(formatCardString(cardName, quantity)).append("\n");
                    });
            }
        });
    }

    public void generateReportFooter(final Map<String, Integer> notInCart, final Integer totalCardCount) {
        if (!notInCart.isEmpty()) {
            footer.append("{Not in Cart}").append("\n");
        }
        notInCart.keySet()
            .stream()
            .sorted()
            .forEach(cardName -> {
                final Integer quantity = notInCart.get(cardName);
                footer.append("  ").append(formatCardString(cardName, quantity)).append("\n");
            });

        footer.append("Total Card Count: ").append(totalCardCount).append("\n");
        footer.append("Total Cost: $\n\n");
    }

    private String formatCardString(final String cardName, final Integer quantity) {
        return quantity + "x " + cardName;
    }

}
