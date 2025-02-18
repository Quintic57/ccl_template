package my.dw.ccl.domain;

import io.micrometer.common.util.StringUtils;
import lombok.Data;
import lombok.Setter;
import my.dw.ccl.dto.Item;

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

    private Integer totalCardCount;

    public ShopReport(final Shop shop) {
        this.shop = shop;
        header = new StringBuilder().append(shop.getPrefix()).append(") ").append(shop.getName()).append("\n");
        body = new StringBuilder();
        footer = new StringBuilder();
        totalCardCount = 0;
    }

    public void generateReportBody(final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList) {
        // Body
        for (final String vendorName: shop.getVendors()) {
            final DiscrepancyReport vendorDReport = generateDiscrepancyReport(formatToDeckBuyList, false, vendorName);
            body.append(vendorName).append("\n");
            generateReportBodyForVendor(formatToDeckBuyList, vendorDReport);
        }

        // Footer
        final DiscrepancyReport shopDReport = generateDiscrepancyReport(formatToDeckBuyList, true, null);
        footer.append("Total: ").append(shop.getAdapter().extractTotalPrice()).append(" (estimated)\n");
        if (!shopDReport.getExcludedFromCart().isEmpty()) {
            footer.append("[Not in cart]\n");
            final Set<Item> items = shopDReport.getExcludedFromCart().entrySet()
                    .stream()
                    .map(entry -> new Item(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toSet());
            for (final Item item: items) {
                footer.append("  ").append(item).append("\n");
            }
        }
        footer.append("\n");
    }

    // TODO: Can likely simplify logic here too
    private void generateReportBodyForVendor(final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList,
                                             final DiscrepancyReport dReport) {
        final Map<String, Integer> itemsFromCart = dReport.getItemsFromCart();
        final Map<String, Integer> excludedFromCart = dReport.getExcludedFromCart();
        final Map<String, Integer> excludedFromList = dReport.getExcludedFromList();
        int buyListCardCount = 0;
        for (final Format format: formatToDeckBuyList.keySet()) {
            if (!isVendorInFormat(format, formatToDeckBuyList, itemsFromCart)
                    || isFormatExcludedFromCart(format ,formatToDeckBuyList, itemsFromCart, excludedFromCart)) {
                continue;
            }
            final Map<Deck, List<Card>> deckToBuyList = formatToDeckBuyList.get(format);
            body.append("  ").append(format).append("\n");

            for (final Deck deck: deckToBuyList.keySet()) {
                final List<Card> buyList = deckToBuyList.get(deck);
                if (!isVendorInBuyList(buyList, itemsFromCart)
                        || isBuyListExcludedFromCart(buyList, excludedFromCart)) {
                    continue;
                }
                body.append("    ").append(deck.getName()).append("\n");

                for (final Card card: buyList) {
                    if (!card.getQuantityMap().containsKey(shop) || !itemsFromCart.containsKey(card.getName())) {
                        continue;
                    }

                    final int cardQuantityInCart = itemsFromCart.get(card.getName());
                    int cardQuantityInList = card.getQuantityMap().get(shop);
                    if (cardQuantityInCart < cardQuantityInList) {
                        itemsFromCart.remove(card.getName());
                        body.append("      ").append(new Item(card.getName(), cardQuantityInCart)).append("\n");
                        cardQuantityInList = cardQuantityInCart;
                    } else {
                        itemsFromCart.put(card.getName(), cardQuantityInCart - cardQuantityInList);
                        body.append("      ").append(card.convertToItemForShop(shop).toString()).append("\n");
                    }
                    buyListCardCount = buyListCardCount + cardQuantityInList;
                }
            }
        }
        body.append("Cart Card Count: ").append(buyListCardCount).append("\n");

        int exclusionsCardCount = 0;
        if (!dReport.getExcludedFromList().isEmpty()) {
            body.append("[Not in list]\n");
            final Set<Item> items = excludedFromList.entrySet()
                    .stream()
                    .map(entry -> new Item(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toSet());
            for (final Item item: items) {
                body.append("  ").append(item).append("\n");
                exclusionsCardCount = exclusionsCardCount + item.getQuantity();
            }
            body.append("Excluded Card Count: ").append(exclusionsCardCount).append("\n");
        }

        totalCardCount = totalCardCount + buyListCardCount + exclusionsCardCount;
    }

    // TODO: Is there a way to make card names case-insensitive?
    private DiscrepancyReport generateDiscrepancyReport(
            final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList,
            final boolean forShop,
            final String vendorName) {
    /*  TODO: Fix edge case where two separate quantities of the same card name are bought (i.e. different rarities).
         this current implementation will get a merge exception when mapping Set<Item> -> Map<cardName, cardQuantity) */
        Set<Item> packages = shop.getAdapter().extractPackages();
        if (forShop) {
            packages = packages
                .stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Item::getCardName, Function.identity(), Item::mergeItems), m -> new HashSet<>(m.values())));
        }
        final Map<String, Integer> itemsFromCart = packages
            .stream()
            .filter(item -> forShop
                    || (StringUtils.isNotEmpty(vendorName) && item.getVendorName().contains(vendorName)))
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

    private boolean isVendorInFormat(final Format format,
                                     final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList,
                                     final Map<String, Integer> itemsFromCart) {
        return formatToDeckBuyList.get(format).values()
            .stream()
            .anyMatch(buyList -> isVendorInBuyList(buyList, itemsFromCart));
    }

    private boolean isFormatExcludedFromCart(final Format format,
                                             final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList,
                                             final Map<String, Integer> itemsFromCart,
                                             final Map<String, Integer> excludedFromCart) {
        final Map<Deck, List<Card>> deckToBuyList = formatToDeckBuyList.get(format);
        return deckToBuyList.values()
            .stream()
            .filter(buyList -> !buyList.isEmpty())
            .filter(buyList -> isVendorInBuyList(buyList, itemsFromCart))
            .allMatch(buyList -> isBuyListExcludedFromCart(buyList, excludedFromCart));
    }

    private boolean isVendorInBuyList(final List<Card> buyList, final Map<String, Integer> itemsFromCart) {
        return buyList
            .stream()
            .anyMatch(card -> itemsFromCart.containsKey(card.getName()) && card.getQuantityMap().containsKey(shop));
    }

    private boolean isBuyListExcludedFromCart(final List<Card> buyList,
                                              final Map<String, Integer> excludedFromCart) {
        return buyList
            .stream()
            .allMatch(card -> excludedFromCart.containsKey(card.getName())
                && excludedFromCart.get(card.getName()).equals(card.getQuantityMap().get(shop)));
    }

}
