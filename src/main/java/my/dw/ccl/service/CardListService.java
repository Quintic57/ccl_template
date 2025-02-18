package my.dw.ccl.service;

import my.dw.ccl.domain.Card;
import my.dw.ccl.domain.Deck;
import my.dw.ccl.domain.DeckList;
import my.dw.ccl.domain.Format;
import my.dw.ccl.domain.Shop;
import my.dw.ccl.domain.ShopReport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
        int totalCardCount = 0;

        final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList = convertInput(reader);
        final Set<Shop> shopsInCardList = Arrays.stream(Shop.values())
            .filter(shop -> input.contains(shop.getIdentifier()))
            .collect(Collectors.toCollection(LinkedHashSet::new));

        for (final Shop shop: shopsInCardList) {
            final ShopReport shopReport = new ShopReport(shop);
            shopReport.generateReportBody(formatToDeckBuyList);
            totalCardCount = totalCardCount + shopReport.getTotalCardCount();
            output.append(shopReport.getHeader()).append(shopReport.getBody()).append(shopReport.getFooter());
        }
        output.append("Total Card Count: " + totalCardCount + "\n");
        output.append("Grand Total: $\n");

        return output.toString();
    }

    private Map<Format, Map<Deck, List<Card>>> convertInput(final Reader reader) throws IOException {
        final Map<Format, Map<Deck, List<Card>>> formatToDeckBuyList = new LinkedHashMap<>();
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
            final Map<Deck, List<Card>> deckToCardList = new LinkedHashMap<>();
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

                final Card card = Optional.ofNullable(Card.convertFromLine(line))
                    .orElseThrow(() -> new IllegalStateException("Card can not be null if this line contains a shop unique identifier"));
                if (deckToCardList.containsKey(currentDeck)) {
                    deckToCardList.get(currentDeck).add(card);
                } else {
                    deckToCardList.put(currentDeck, new ArrayList<>(Collections.singletonList(card)));
                }
            }
            formatToDeckBuyList.put(entry.getKey(), deckToCardList);
        }

        bufferedReader.close();
        return formatToDeckBuyList;
    }

    // [startLine:endLine]
    public class Line {
        int startLine;
        int endLine;

        public Line(final int startLine) {
            this.startLine = startLine;
        }

        public void setEndLine(final int endLine) {
            this.endLine = endLine;
        }
    }

}
