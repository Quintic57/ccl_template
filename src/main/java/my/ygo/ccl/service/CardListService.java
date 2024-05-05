package my.ygo.ccl.service;

import my.ygo.ccl.domain.Card;
import my.ygo.ccl.domain.Deck;
import my.ygo.ccl.domain.DiscrepancyReport;
import my.ygo.ccl.domain.Format;
import my.ygo.ccl.domain.Shop;
import my.ygo.ccl.domain.ShopReport;
import my.ygo.ccl.dto.Item;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 - If card name matches exactly 1 in the shop, list it as included
 - If card name does not match anything in the shop, list it as not included in shop
 - If card exists in shop but not in list, list it as [Unlisted]
 */
//@Service
public class CardListService {

    public String generateCardReport(final String input) throws IOException {
        final StringBuilder output = new StringBuilder();
        final Reader reader = new StringReader(input);
        final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList = convertInput(reader);
        int totalCardCount = 0;
        final Set<Shop> shopsInCardList = Arrays.stream(Shop.values())
            .filter(shop -> input.contains(shop.getIdentifier()))
            .collect(Collectors.toCollection(LinkedHashSet::new));

        for (final Shop shop: shopsInCardList) {
            final ShopReport shopReport = new ShopReport();
            shopReport.getReportBody().append(shop.getPrefix()).append(") ").append(shop.getName()).append("\n");
            enrichShopReport(shopReport, shop, formatToDeckBuyList);
            totalCardCount = totalCardCount + shopReport.getCardCount();
            output.append(shopReport.getReportBody());
        }
        output.append("Total Card Count: " + totalCardCount + "\n");
        output.append("Grand Total: $\n");

        return output.toString();
    }

    // TODO: Fix deck list being out of order
    // TODO: Exclude pack name from card name (i.e. Rekindling [MYFI] should just be Rekindling)
    private Map<Format, Map<Deck, List<Card>>> convertInput(final Reader reader) throws IOException {
        final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList = Deck.generateEmptyDeckBuyListMap();
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final List<String> lines = bufferedReader.lines().collect(Collectors.toList());

        // Parse all lines by format
        final Map<Format, List<String>> formatToInputs = new LinkedHashMap<>();
        Format currentFormat = Format.getListedFormats()
            .stream()
            .filter(format -> lines.get(0).contains(format.getName()))
            .findAny()
            .orElse(Format.CROSS_BANLIST);
        int prevIndex = 0;
        for (int i = 1; i < lines.size(); i++) {
            if (i == lines.size() - 1) {
                formatToInputs.put(currentFormat, lines.subList(prevIndex, i));
                break;
            }

            final String line = lines.get(i);
            for (Format format: Format.getListedFormats()) {
                if (line.contains(format.getName())) {
                    formatToInputs.put(currentFormat, lines.subList(prevIndex, i));
                    currentFormat = format;
                    prevIndex = i;
                }
            }
        }

        // Parse each format by deck
        for (final Format format: formatToInputs.keySet()) {
            final Map<Deck, List<Card>> currentBuyList = formatToDeckBuyList.get(format);
            Deck currentDeck = null;
            for (int i = 1; i < formatToInputs.get(format).size(); i++) {
                final String line = formatToInputs.get(format).get(i);
                for (final Deck deck: currentBuyList.keySet()) {
                    if (line.contains(deck.getName())
                        && (deck.getFormat() != Format.CROSS_BANLIST || line.contains(deck.getBanlistAsString()))) {
                        currentDeck = deck;
                    }
                }
                final List<Card> cards = currentBuyList.get(currentDeck);
                if (cards != null && Arrays.stream(Shop.values()).map(Shop::getIdentifier).anyMatch(line::contains)) {
                    cards.add(Card.convertFromLine(line));
                }
            }
        }

        bufferedReader.close();
        return formatToDeckBuyList;
    }

    // TODO: This should only include cards that are both in the list AND in a cart.
    private void enrichShopReport(final ShopReport shopReport,
                                  final Shop shop,
                                  final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList) {
        final DiscrepancyReport dReport = generateDiscrepancyReport(shop, formatToDeckBuyList);
        final Map<String, Integer> itemsFromCart = dReport.getItemsFromCart();
        final StringBuilder reportBody = shopReport.getReportBody();
        int cardCount = 0;
        for (final Format format: formatToDeckBuyList.keySet()) {
            if (!isShopInFormat(shop, format, formatToDeckBuyList)
                || isFormatExcludedFromCart(shop, format ,formatToDeckBuyList, dReport.getItemsExcludedFromList())) {
                continue;
            }
            final Map<Deck, List<Card>> deckToBuyList = formatToDeckBuyList.get(format);
            reportBody.append(format.getName()).append("\n");

            for (final Deck deck: deckToBuyList.keySet()) {
                final List<Card> buyList = deckToBuyList.get(deck);
                if (!isShopInBuyList(shop, buyList)
                    || isBuyListExcludedFromCart(shop, buyList, dReport.getItemsExcludedFromList())) {
                    continue;
                }
                reportBody.append("  ").append(deck.getName()).append("\n");

                for (final Card card: buyList) {
                    if (!card.getQuantityMap().containsKey(shop) || !itemsFromCart.containsKey(card.getName())) {
                        continue;
                    }

                    final int cardQuantityInCart = itemsFromCart.get(card.getName());
                    int cardQuantityInList = card.getQuantityMap().get(shop);
                    if (cardQuantityInCart < cardQuantityInList) {
                        itemsFromCart.remove(card.getName());
                        reportBody.append("    ").append(new Item(card.getName(), cardQuantityInCart)).append("\n");
                        cardQuantityInList = cardQuantityInCart;
                    } else {
                        itemsFromCart.put(card.getName(), cardQuantityInCart - cardQuantityInList);
                        reportBody.append("    ").append(card.convertToItemForShop(shop).toString()).append("\n");
                    }
                    cardCount = cardCount + cardQuantityInList;
                }
            }
        }

        shopReport.setCardCount(cardCount);
        reportBody.append("Card Count: ").append(cardCount).append("\n");
        final Double subtotal = shop.getAdapter().extractTotalPrice();
        shopReport.setSubtotal(subtotal);
        reportBody.append("Total: ").append(subtotal).append("\n");
        // TODO: Add card count
        if (!dReport.getItemsExcludedFromList().isEmpty()) {
            reportBody.append("[In list but not in cart]\n");
            dReport.getItemsExcludedFromList().entrySet()
                .stream()
                .map(entry -> new Item(entry.getKey(), entry.getValue()))
                .forEach(item -> reportBody.append("    ").append(item).append("\n"));
        }
        // TODO: Add card count
        if (!dReport.getItemsExcludedFromCart().isEmpty()) {
            reportBody.append("[In cart but not in list]\n");
            dReport.getItemsExcludedFromCart().entrySet()
                .stream()
                .map(entry -> new Item(entry.getKey(), entry.getValue()))
                .forEach(item -> reportBody.append("    ").append(item).append("\n"));
        }
        reportBody.append("\n");
    }

    // TODO: Is there a way to make card names case-insensitive?
    private DiscrepancyReport generateDiscrepancyReport(final Shop shop,
                                                        final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList) {
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

    private static boolean isShopInFormat(final Shop shop,
                                          final Format format,
                                          final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList) {
        return formatToDeckBuyList.get(format).values()
            .stream()
            .flatMap(Collection::stream)
            .anyMatch(card -> card.getQuantityMap().containsKey(shop));
    }

    private static boolean isFormatExcludedFromCart(final Shop shop,
                                                    final Format format,
                                                    final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList,
                                                    final Map<String, Integer> itemsExcludedFromList) {
        final Map<Deck, List<Card>> deckToBuyList = formatToDeckBuyList.get(format);
        return deckToBuyList.values()
            .stream()
            .filter(buyList -> !buyList.isEmpty())
            .filter(buyList -> isShopInBuyList(shop, buyList))
            .allMatch(buyList -> isBuyListExcludedFromCart(shop, buyList, itemsExcludedFromList));
    }

    private static boolean isShopInBuyList(final Shop shop, final List<Card> buyList) {
        return buyList.stream().anyMatch(card -> card.getQuantityMap().containsKey(shop));
    }

    private static boolean isBuyListExcludedFromCart(final Shop shop,
                                                     final List<Card> buyList,
                                                     final Map<String, Integer> itemsExcludedFromList) {
        return buyList
            .stream()
            .allMatch(card -> itemsExcludedFromList.containsKey(card.getName())
                && itemsExcludedFromList.get(card.getName()).equals(card.getQuantityMap().get(shop)));
    }

}
