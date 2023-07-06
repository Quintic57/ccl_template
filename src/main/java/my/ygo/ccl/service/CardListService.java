package my.ygo.ccl.service;

import my.ygo.ccl.domain.Deck;
import my.ygo.ccl.domain.Format;
import my.ygo.ccl.domain.Shop;
import org.springframework.data.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//@Service
public class CardListService {

    public String generateCardList(final String input) {
        final StringBuilder output = new StringBuilder();

        try {
            final Reader reader = new StringReader(input);
            final Map<Format, Map<Deck, List<String>>> formatToDeckBuyList = convertInput(reader);
            int totalCardCount = 0;
            for (Shop shop: Shop.values()) {
                final String shopHeader = shop.getPrefix() + ") " + shop.getName() + "\n";
                output.append(shopHeader);
                final Pair<String, Integer> shopOutput = getShopOutputAndCardCount(shop.getIdentifier(), formatToDeckBuyList);
                output.append(shopOutput.getFirst());
                totalCardCount = totalCardCount + shopOutput.getSecond();
                output.append("Total: $\n\n");
            }
            output.append("Total Card Count: " + totalCardCount + "\n");
            output.append("Grand Total: $" + "\n");
        } catch (final Exception e) {
            System.out.println("Exception thrown while processing input");
        }

        return output.toString();
    }

    private Map<Format, Map<Deck, List<String>>> convertInput(final Reader reader) {
        final Map<Format, Map<Deck, List<String>>> formatToDeckBuyList = new LinkedHashMap<>();
        for (Format format: Format.getListedFormats()) {
            final Map<Deck, List<String>> buyList = Arrays.stream(Deck.values())
                .filter(deck -> deck.getFormat().equals(format) || deck.getFormat().equals(Format.UNLISTED))
                .collect(Collectors.toMap(deck -> deck,
                    deck -> new ArrayList<>(),
                    (u, v) -> {
                        throw new IllegalStateException(String.format("Duplicate key %s", u));
                    },
                    LinkedHashMap::new)
                );
            formatToDeckBuyList.put(format, buyList);
        }

        try {
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
                final Map<Deck, List<String>> currentBuyList = formatToDeckBuyList.get(format);
                Deck currentDeck = null;
                for (int i = 1; i < formatToInputs.get(format).size(); i++) {
                    final String line = formatToInputs.get(format).get(i);
                    for (final Deck deck: currentBuyList.keySet()) {
                        if (line.contains(deck.getName())) {
                            currentDeck = deck;
                        }
                    }
                    if (currentDeck != null) {
                        currentBuyList.get(currentDeck).add(line);
                    }
                }

            }

            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Oops");
        }

        return formatToDeckBuyList;
    }

    //TODO: For each deck, list all of the cards that are not currently in a cart (i.e. they don't have a marker)
    private Pair<String, Integer> getShopOutputAndCardCount(final String shopUID,
                                                            final Map<Format, Map<Deck, List<String>>> formatToDeckBuyList) {
        final StringBuilder output = new StringBuilder();
        int cardCount = 0;
        for (final Format format: formatToDeckBuyList.keySet()) {
            if (!isShopInFormat(shopUID, format, formatToDeckBuyList)) {
                continue;
            }
            final Map<Deck, List<String>> deckToBuyList = formatToDeckBuyList.get(format);
            output.append(format.getName()).append("\n");

            for (final Deck deck: deckToBuyList.keySet()) {
                final List<String> buyList = deckToBuyList.get(deck);
                if (!isShopInBuyList(shopUID, buyList)) {
                    continue;
                }
                output.append("  ").append(deck.getName()).append("\n");

                for (final String line: buyList) {
                    if (!line.contains(shopUID)) {
                        continue;
                    }

                    final Pattern pattern = Pattern.compile(String.join("",
                        "\\s*\\dx\\s*(.*?)([",
                        Arrays.stream(Shop.values()).map(Shop::getIdentifier).collect(Collectors.joining("")),
                        "]+).*"));
                    final Matcher m = pattern.matcher(line);
                    String temp = "";
                    if (m.matches()) {
                        int numCards = m.group(2).length() - m.group(2).replace(shopUID, "").length();
                        temp = "    " + numCards + "x " + m.group(1) + "\n";
                        cardCount = cardCount + numCards;
                    }
                    output.append(temp);
                }
            }
        }
        output.append("Card Count: ").append(cardCount).append("\n");

        return Pair.of(output.toString(), cardCount);
    }

    private static boolean isShopInFormat(final String shopUID,
                                          final Format format,
                                          final Map<Format, Map<Deck, List<String>>> formatToDeckBuyList) {
        return formatToDeckBuyList.get(format).values()
            .stream()
            .flatMap(Collection::stream)
            .anyMatch(line -> line.contains(shopUID));
    }

    private static boolean isShopInBuyList(final String shopUID, final List<String> buyList) {
        return buyList.stream().anyMatch(line -> line.contains(shopUID));
    }

}
