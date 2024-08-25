package my.ygo.ccl.domain;

import lombok.Data;
import lombok.Setter;
import my.ygo.ccl.dto.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Setter
public class ShopReport {

    private final Shop shop;

    private final StringBuilder header;

    private final StringBuilder body;

    private final StringBuilder footer;

    private Integer totalCardCount;

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
        int buyListCardCount = 0;
        for (final Format format: formatToDeckBuyList.keySet()) {
            if (!isShopInFormat(format, formatToDeckBuyList)
                || isFormatExcludedFromCart(format ,formatToDeckBuyList, dReport.getExcludedFromCart())) {
                continue;
            }
            final Map<Deck, List<Card>> deckToBuyList = formatToDeckBuyList.get(format);
            body.append(format).append("\n");

            for (final Deck deck: deckToBuyList.keySet()) {
                final List<Card> buyList = deckToBuyList.get(deck);
                if (!isShopInBuyList(buyList)
                    || isBuyListExcludedFromCart(buyList, dReport.getExcludedFromCart())) {
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
                    buyListCardCount = buyListCardCount + cardQuantityInList;
                }
            }
        }

        // Footer
        footer.append("Card Count: ").append(buyListCardCount).append("\n");
        int exclusionsCardCount = 0;
        if (!dReport.getExcludedFromList().isEmpty()) {
            footer.append("[In cart but not in list]\n");
            exclusionsCardCount = exclusionsCardCount + appendExclusions(dReport.getExcludedFromList());
        }
        footer.append("Card Count: ").append(exclusionsCardCount).append("\n");
        setTotalCardCount(buyListCardCount + exclusionsCardCount);
        footer.append("Total: ").append(shop.getAdapter().extractTotalPrice()).append("\n");
        if (!dReport.getExcludedFromCart().isEmpty()) {
            footer.append("[In list but not in cart]\n");
            appendExclusions(dReport.getExcludedFromCart());
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

        // In list but not in cart
        final Map<String, Integer> excludedFromCart = generateExclusionSet(itemsFromList, itemsFromCart);
        // In cart but not in list
        final Map<String, Integer> excludedFromList = generateExclusionSet(itemsFromCart, itemsFromList);

        return new DiscrepancyReport(itemsFromCart, excludedFromCart, excludedFromList);
    }

    private Map<String, Integer> generateExclusionSet(final Map<String, Integer> set1,
                                                      final Map<String, Integer> set2) {
        return set1.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            .entrySet()
            .stream()
            .filter(entry -> {
                if (!set2.containsKey(entry.getKey())) {
                    return true;
                } else if (set2.get(entry.getKey()) < entry.getValue()) {
                    entry.setValue(entry.getValue() - set2.get(entry.getKey()));
                    return true;
                }
                return false;
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private int appendExclusions(final Map<String, Integer> exclusionMap) {
        final Set<Item> items = exclusionMap.entrySet()
            .stream()
            .map(entry -> new Item(entry.getKey(), entry.getValue()))
            .collect(Collectors.toSet());
        int cardCount = 0;
        for (final Item item: items) {
            footer.append("    ").append(item).append("\n");
            cardCount = cardCount + item.getQuantity();
        }

        return cardCount;
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
