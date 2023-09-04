package my.ygo.ccl.adapter.shop;

import lombok.SneakyThrows;
import my.ygo.ccl.adapter.Adapter;
import my.ygo.ccl.dto.Item;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

//TODO: In the future, this should be able to pull the cart information just from the user context, but since that code
// is obfuscated, for now just pull it from the HTML
public class CoolstuffAdapter implements Adapter {

    private final Document document;

    @SneakyThrows
    public CoolstuffAdapter() {
        final String html = Files.readString(Path.of("src/main/resources/in/coolstuff_input.html"));
        this.document = Jsoup.parse(html);
    }

    @Override
    public Set<Item> extractPackages() {
        final List<Item> itemList = new ArrayList<>();
        final Elements cartElement = document.getElementsByClass("row cart-row main-container");

        for (final Element cardElement: cartElement) {
            final String cardName = cardElement.getElementsByAttributeValue("itemprop", "name").get(0).text();
            final Integer quantity = Integer.parseInt(
                cardElement.getElementsByClass("int input-add-qty").get(0).attributes().get("value"));
            final Double price = Double.parseDouble(cardElement.getElementsByClass("subtotal").get(0).child(0)
                .text().replace("$", "").strip());
            final Item item = new Item(cardName, quantity, price, "Coolstuff");
            itemList.add(item);
        }

        return itemList
            .stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toMap(Item::getCardName, Function.identity(), Item::mergeItems), m -> new HashSet<>(m.values())));
    }

    @Override
    public Double extractTotalPrice() {
        return Double.parseDouble(
            document.getElementsByClass("flr text-right")
                .get(0)
                .getElementsByClass("darkred total")
                .text()
                .replace("$", "")
                .strip());
    }

}
