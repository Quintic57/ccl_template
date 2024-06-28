package my.ygo.ccl.domain;

import lombok.Data;
import lombok.Setter;
import my.ygo.ccl.dto.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Setter
public class ShopReport {

    private final Shop shop;

    private final StringBuilder header;

    private final StringBuilder body;

    private final StringBuilder footer;

    private Integer cardCount;

    public ShopReport(final Shop shop) {
        this.shop = shop;
        header = new StringBuilder().append(shop.getPrefix()).append(") ").append(shop.getName()).append("\n");
        body = new StringBuilder();
        footer = new StringBuilder();
    }

    // TODO: Can likely simplify logic here too
    public void enrichWithBuyList(final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList) {
        // Body
        final DiscrepancyReport dReport = generateDiscrepancyReport(formatToDeckBuyList);
        final Map<String, Integer> itemsFromCart = dReport.getItemsFromCart();
        int cardCount = 0;
        for (final Format format: formatToDeckBuyList.keySet()) {
            if (!isShopInFormat(format, formatToDeckBuyList)
                || isFormatExcludedFromCart(format ,formatToDeckBuyList, dReport.getItemsExcludedFromList())) {
                continue;
            }
            final Map<Deck, List<Card>> deckToBuyList = formatToDeckBuyList.get(format);
            body.append(format).append("\n");

            for (final Deck deck: deckToBuyList.keySet()) {
                final List<Card> buyList = deckToBuyList.get(deck);
                if (!isShopInBuyList(buyList)
                    || isBuyListExcludedFromCart(buyList, dReport.getItemsExcludedFromList())) {
                    continue;
                }
                body.append("  ").append(deck.getName()).append("\n");

                for (final Card card: buyList) {
                    if (!card.getQuantityMap().containsKey(shop) || !itemsFromCart.containsKey(card.getName())) {
                        continue;
                    }

                    final int cardQuantityInCart = itemsFromCart.get(card.getName());
                    int cardQuantityInList = card.getQuantityMap().get(shop);
                    if (cardQuantityInCart < cardQuantityInList) {
                        itemsFromCart.remove(card.getName());
                        body.append("    ").append(new Item(card.getName(), cardQuantityInCart)).append("\n");
                        cardQuantityInList = cardQuantityInCart;
                    } else {
                        itemsFromCart.put(card.getName(), cardQuantityInCart - cardQuantityInList);
                        body.append("    ").append(card.convertToItemForShop(shop).toString()).append("\n");
                    }
                    cardCount = cardCount + cardQuantityInList;
                }
            }
        }

        // Footer
        setCardCount(cardCount);
        footer.append("Card Count: ").append(cardCount).append("\n");
        footer.append("Total: ").append(shop.getAdapter().extractTotalPrice()).append("\n");
        // TODO: Add card count
        if (!dReport.getItemsExcludedFromList().isEmpty()) {
            footer.append("[In list but not in cart]\n");
            dReport.getItemsExcludedFromList().entrySet()
                .stream()
                .map(entry -> new Item(entry.getKey(), entry.getValue()))
                .forEach(item -> footer.append("    ").append(item).append("\n"));
        }
        // TODO: Add card count
        if (!dReport.getItemsExcludedFromCart().isEmpty()) {
            footer.append("[In cart but not in list]\n");
            dReport.getItemsExcludedFromCart().entrySet()
                .stream()
                .map(entry -> new Item(entry.getKey(), entry.getValue()))
                .forEach(item -> footer.append("    ").append(item).append("\n"));
        }
        footer.append("\n");
    }

    // TODO: Is there a way to make card names case-insensitive?
    private DiscrepancyReport generateDiscrepancyReport(final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList) {
        final Map<String, Integer> itemsFromCart = shop.getAdapter().extractPackages()
            .stream()
            .collect(Collectors.toMap(Item::getCardName, Item::getQuantity));

        final Map<String, Integer> itemsFromList = formatToDeckBuyList.values()
            .stream()
            .flatMap(buyList -> buyList.values().stream())
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .filter(card -> card.getQuantityMap().containsKey(shop))
            .map(card -> card.convertToItemForShop(shop))
            .collect(Collectors.collectingAndThen(
                Collectors.toMap(Item::getCardName, Function.identity(), Item::mergeItems), m -> new ArrayList<>(m.values())))
            .stream()
            .collect(Collectors.toMap(Item::getCardName, Item::getQuantity));

        // TODO: Consolidate duplicated code
        final Map<String, Integer> itemsExcludedFromList = itemsFromList.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            .entrySet()
            .stream()
            .filter(entry -> {
                if (!itemsFromCart.containsKey(entry.getKey())) {
                    return true;
                } else if (itemsFromCart.get(entry.getKey()) < entry.getValue()) {
                    entry.setValue(entry.getValue() - itemsFromCart.get(entry.getKey()));
                    return true;
                }
                return false;
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final Map<String, Integer> itemsExcludedFromCart = itemsFromCart.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            .entrySet()
            .stream()
            .filter(entry -> {
                if (!itemsFromList.containsKey(entry.getKey())) {
                    return true;
                } else if (itemsFromList.get(entry.getKey()) < entry.getValue()) {
                    entry.setValue(entry.getValue() - itemsFromList.get(entry.getKey()));
                    return true;
                }
                return false;
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new DiscrepancyReport(itemsFromCart, itemsExcludedFromList, itemsExcludedFromCart);
    }

    private boolean isShopInFormat(final Format format,
                                   final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList) {
        return formatToDeckBuyList.get(format).values()
            .stream()
            .flatMap(Collection::stream)
            .anyMatch(card -> card.getQuantityMap().containsKey(shop));
    }

    private boolean isFormatExcludedFromCart(final Format format,
                                             final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList,
                                             final Map<String, Integer> itemsExcludedFromList) {
        final Map<Deck, List<Card>> deckToBuyList = formatToDeckBuyList.get(format);
        return deckToBuyList.values()
            .stream()
            .filter(buyList -> !buyList.isEmpty())
            .filter(this::isShopInBuyList)
            .allMatch(buyList -> isBuyListExcludedFromCart(buyList, itemsExcludedFromList));
    }

    private boolean isShopInBuyList(final List<Card> buyList) {
        return buyList.stream().anyMatch(card -> card.getQuantityMap().containsKey(shop));
    }

    private boolean isBuyListExcludedFromCart(final List<Card> buyList,
                                              final Map<String, Integer> itemsExcludedFromList) {
        return buyList
            .stream()
            .allMatch(card -> itemsExcludedFromList.containsKey(card.getName())
                && itemsExcludedFromList.get(card.getName()).equals(card.getQuantityMap().get(shop)));
    }

}
