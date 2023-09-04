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
        final Pattern pattern = Pattern.compile(String.join("",
            "\\s*\\dx\\s*(.*?)([",
            Arrays.stream(Shop.values()).map(Shop::getIdentifier).collect(Collectors.joining("")),
            "]+).*"));
        final Matcher m = pattern.matcher(line);
        if (!m.matches()) {
            return null;
        }

        final Card card = new Card(m.group(1));
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
