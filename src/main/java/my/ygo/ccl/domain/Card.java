package my.ygo.ccl.domain;

import lombok.Data;
import my.ygo.ccl.dto.Item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class Card {

    private final String name;

    private final Map<Shop, Integer> quantityMap;

    public Card(final String name) {
        this.name = name;
        this.quantityMap = new HashMap<>();
    }

    public Item convertToItemForShop(final Shop shop) {
        return new Item(this.name, this.quantityMap.get(shop));
    }

    public static Card convertFromLine(final String line) {
        final Pattern p = Pattern.compile(String.join("",
            "\\s*\\dx\\s*(.*?)([",
            Arrays.stream(Shop.values()).map(Shop::getIdentifier).collect(Collectors.joining("")),
            "]+).*"));
        final Matcher m = p.matcher(line);
        if (!m.matches()) {
            return null;
        }

        // Strip appended pack name
        String cardName = m.group(1);
        final Pattern p2 = Pattern.compile("\\[.*]");
        final Matcher m2 = p2.matcher(cardName);
        if (m2.find()) {
            cardName = cardName.replace(m2.group(0), "").strip();
        }

        final Card card = new Card(cardName);
        for (final Shop shop: Shop.values()) {
            final String shopUID = shop.getIdentifier();

            if (!line.contains(shopUID)) {
                continue;
            }

            final int numOfCardsForShop = m.group(2).length() - m.group(2).replace(shopUID, "").length();
            card.quantityMap.put(shop, numOfCardsForShop);
        }

        return card;
    }

}
