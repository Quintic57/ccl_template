package my.dw.ccl.adapter.shop;

import lombok.SneakyThrows;
import my.dw.ccl.adapter.Adapter;
import my.dw.ccl.dto.Item;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static my.dw.ccl.dto.Item.MAIN_VENDOR_NAME;

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
            // ---Card Name---
            String cardName = cardElement.getElementsByAttributeValue("itemprop", "name").get(0).text();
            // strip rarity suffix
            final Matcher matcher = Pattern.compile("\\s\\(.*Rare\\)").matcher(cardName);
            if (matcher.find()) {
                cardName = cardName.replace(matcher.group(0), "");
            }
            // ---Quantity---
            Integer quantity = Integer.parseInt(
                cardElement.getElementsByClass("int input-add-qty").get(0).attributes().get("value"));
            if (!cardElement.getElementsByClass("b1-gx-free").isEmpty()) {
                quantity = quantity + 2;
            }
            // ---Price---
            final Double price = Double.parseDouble(cardElement.getElementsByClass("subtotal").get(0).child(0)
                .text().replace("$", "").strip());

            itemList.add(new Item(cardName, quantity, price, MAIN_VENDOR_NAME));
        }

        return new HashSet<>(itemList);
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

    @Override
    public void extractVendors(Set<String> vendors) {
        // do nothing
    }

}
