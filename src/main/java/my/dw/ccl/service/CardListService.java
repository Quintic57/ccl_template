package my.dw.ccl.service;

import lombok.Setter;
import my.dw.ccl.domain.CardInList;
import my.dw.ccl.domain.CardDto;
import my.dw.ccl.domain.Deck;
import my.dw.ccl.domain.DeckList;
import my.dw.ccl.domain.Format;
import my.dw.ccl.domain.Shop;
import my.dw.ccl.domain.CardInCart;
import my.dw.ccl.domain.ShopReport;
import my.dw.ccl.domain.Vendor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
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
        final Set<Shop> shopsInBuyList = Arrays.stream(Shop.values())
            .filter(shop -> input.contains(shop.getIdentifier()))
            .collect(Collectors.toCollection(LinkedHashSet::new));
        // convert input string -> buyList Map
        final Map<Deck, List<CardDto>> buyList = convertInput(new StringReader(input));

        int grandTotalCardCount = 0;
        for (final Shop shop: shopsInBuyList) {
            // create copy of buyList to not overwrite existing Map
            final Map<Deck, List<CardInList>> shopBuyList = new LinkedHashMap<>();
            // filter only for cards with shop identifier
            buyList.forEach((deck, cardList) -> {
                final List<CardInList> filteredCardList = cardList
                    .stream()
                    .filter(card -> card.getQuantityMap().containsKey(shop))
                    .map(card -> new CardInList(card.getName(), card.getQuantityMap().get(shop)))
                    .toList();
                if (!filteredCardList.isEmpty()) {
                    shopBuyList.put(deck, filteredCardList);
                }
            });
            // check if cards are present in cart
            final List<CardInList> cardsInList = shopBuyList.values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
            final Map<String, CardInCart> cardsInCart = shop.getAdapter().extractPackages()
                .stream()
                .map(item -> new CardInCart(
                    item.getCardName(),
                    new HashMap<>(Map.of(item.getVendor(), item.getQuantity())),
                    new HashMap<>(Map.of(item.getVendor(), item.getPrice()))
                ))
                .collect(Collectors.toMap(CardInCart::getName, Function.identity(), CardInCart::merge, LinkedHashMap::new));

            final Map<String, Integer> notInCart = checkIfCardsAreInCart(cardsInList, cardsInCart);
            final Map<Vendor, Map<String, Integer>> notInList = new HashMap<>();
            for (final String cardName: cardsInCart.keySet()) {
                cardsInCart.get(cardName).getQuantityMap()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() > 0)
                    .forEach(entry -> {
                        if (notInList.containsKey(entry.getKey())) {
                            notInList.get(entry.getKey()).put(cardName, entry.getValue());
                        } else {
                            notInList.put(entry.getKey(), new HashMap<>(Map.of(cardName, entry.getValue())));
                        }

                    });
            }

            // generate report
            final Map<Vendor, Integer> cardCount = new HashMap<>();
            final ShopReport shopReport = new ShopReport(shop);
            shopReport.generateReportHeader();
            shopReport.generateReportBody(shopBuyList, notInList, cardCount);
            shopReport.generateReportFooter(notInCart, cardCount.values().stream().mapToInt(count -> count).sum());
            output.append(shopReport.getHeader()).append(shopReport.getBody()).append(shopReport.getFooter());
            grandTotalCardCount = grandTotalCardCount + cardCount.values().stream().mapToInt(count -> count).sum();
        }

        output.append("Grand Total Card Count: ").append(grandTotalCardCount).append("\n");
        output.append("Grand Total: $").append("\n");
        return output.toString();
    }

    private Map<Deck, List<CardDto>> convertInput(final Reader reader) throws IOException {
        final Map<Deck, List<CardDto>> buyList = new LinkedHashMap<>();
        final BufferedReader bufferedReader = new BufferedReader(reader);

        // Find line numbers for each format
        final List<String> lines = bufferedReader.lines()
            .map(String::strip) // strip white space for consistency -- IMPORTANT
            .collect(Collectors.toList());
        final Map<Format, Line> formatToLine = new LinkedHashMap<>();
        Format currentFormat = Optional.ofNullable(Format.getFormatStringToObjectMap().get(lines.get(0)))
            .orElse(Format.CROSS_BANLIST);
        formatToLine.put(currentFormat, new Line(1));
        for (int i = 1; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (!Format.getFormatStringToObjectMap().containsKey(line)) {
                continue;
            }

            // prev format
            formatToLine.get(currentFormat).setEndLine(i - 1);
            // current format
            currentFormat = Format.getFormatStringToObjectMap().get(line);
            formatToLine.put(currentFormat, new Line(i + 1));
        }
        formatToLine.get(currentFormat).setEndLine(lines.size() - 1);

        // Parse each format by deck
        for (final Map.Entry<Format, Line> entry: formatToLine.entrySet()) {
            final List<String> linesForFormat = lines.subList(entry.getValue().startLine, entry.getValue().endLine + 1);
            final Map<Deck, List<CardDto>> deckBuyList = new LinkedHashMap<>();
            Deck currentDeck =
                Optional.of(DeckList.getDeckStringToObjectMapForFormat(entry.getKey()).get(linesForFormat.get(0)))
                .orElse(null);
            for (int i = 1; i < linesForFormat.size(); i++) {
                final String line = linesForFormat.get(i);
                if (DeckList.getDeckStringToObjectMapForFormat(entry.getKey()).containsKey(line)) {
                    currentDeck = DeckList.getDeckStringToObjectMapForFormat(entry.getKey()).get(line);
                    continue;
                }
                if (!Arrays.stream(Shop.values()).map(Shop::getIdentifier).anyMatch(line::contains)) {
                    continue;
                }

                final CardDto card = Optional.ofNullable(CardDto.convertFromLine(line))
                    .orElseThrow(() -> new IllegalStateException("Card can not be null if this line contains a shop unique identifier"));
                if (deckBuyList.containsKey(currentDeck)) {
                    deckBuyList.get(currentDeck).add(card);
                } else {
                    deckBuyList.put(currentDeck, new ArrayList<>(Collections.singletonList(card)));
                }
            }
            buyList.putAll(deckBuyList);
        }

        bufferedReader.close();
        return buyList;
    }

    private Map<String, Integer> checkIfCardsAreInCart(final List<CardInList> cardsInList,
                                                       final Map<String, CardInCart> cardsInCart) {
        final Map<String, Integer> notInCart = new HashMap<>();

        for (final CardInList cardInList : cardsInList) {
            if (!cardsInCart.containsKey(cardInList.getName())) {
                continue;
            } else if (cardsInCart.get(cardInList.getName()).getTotalQuantity() == 0) {
                notInCart.put(
                    cardInList.getName(),
                    notInCart.getOrDefault(cardInList.getName(), 0) + cardInList.getQuantity()
                );
                continue;
            }

            int targetQuantity = cardInList.getQuantity();
            final CardInCart cardInCart = cardsInCart.get(cardInList.getName());
            for (final Map.Entry<Vendor, Integer> entry: cardInCart.getQuantityMap().entrySet()) {
                final int cartQuantity = entry.getValue();
                if (cartQuantity == 0) { // Skip vendors that have already assigned to different decks
                    continue;
                }

                if (cartQuantity >= targetQuantity) {
                    cardInCart.removeQuantity(entry.getKey(), targetQuantity);
                    cardInList.addQuantityInCart(entry.getKey(), targetQuantity);
                    targetQuantity = 0;
                    break;
                } else {
                    cardInCart.removeQuantity(entry.getKey(), cartQuantity);
                    cardInList.addQuantityInCart(entry.getKey(), cartQuantity);
                    targetQuantity = targetQuantity - cartQuantity;
                }
            }

            if (targetQuantity > 0) {
                notInCart.put(cardInList.getName(), notInCart.getOrDefault(cardInList.getName(), 0) + targetQuantity);
            }
        }

        return notInCart;
    }

    // [startLine:endLine]
    public class Line {
        int startLine;
        @Setter
        int endLine;

        public Line(final int startLine) {
            this.startLine = startLine;
        }

    }

}
