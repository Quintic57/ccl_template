package my.dw.ccl.domain;

import lombok.Data;
import my.dw.ccl.dto.Item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class CardDto {

    private final String name;

    private final Map<Shop, Integer> quantityMap;

    public CardDto(final String name) {
        this.name = name;
        this.quantityMap = new HashMap<>();
    }

    public static CardDto convertFromLine(final String line) {
        final Matcher lineMatcher = Pattern.compile(String.join("",
            "\\s*\\dx\\s*(.*?)([",
            Arrays.stream(Shop.values()).map(Shop::getIdentifier).collect(Collectors.joining("")),
            "]+).*")).matcher(line);
        if (!lineMatcher.matches()) {
            return null;
        }

        // Strip appended pack name
        String cardName = lineMatcher.group(1);
        final Matcher packNameMatcher = Pattern.compile("\\[.*]").matcher(cardName);
        if (packNameMatcher.find()) {
            cardName = cardName.replace(packNameMatcher.group(0), "").strip();
        }

        final CardDto card = new CardDto(cardName);
        for (final Shop shop: Shop.values()) {
            final String shopUID = shop.getIdentifier();

            if (!line.contains(shopUID)) {
                continue;
            }

            final int numOfCardsForShop = lineMatcher.group(2).length() - lineMatcher.group(2).replace(shopUID, "").length();
            card.quantityMap.put(shop, numOfCardsForShop);
        }

        return card;
    }

}
