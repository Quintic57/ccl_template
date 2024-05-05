package my.ygo.ccl.adapter.shop;

import lombok.SneakyThrows;
import my.ygo.ccl.adapter.Adapter;
import my.ygo.ccl.dto.Item;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

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
// TODO: Update parser to only grab name of card if the rarity is in parenthesis in the card title
@Component
public class TcgPlayerAdapter implements Adapter {

    private final Document document;

    @SneakyThrows
    public TcgPlayerAdapter() {
        final String html = Files.readString(Path.of("src/main/resources/in/tcg_player_input.html"));
        this.document = Jsoup.parse(html);
    }

    @Override
    public Set<Item> extractPackages() {
        final List<Item> itemList = new ArrayList<>();
        final Elements packageElement = document.getElementsByClass("package");
        packageElement.remove(0);

        for (final Element pkg: packageElement) {
            final String packageName = pkg.getElementsByAttributeValue("data-testid", "linkPackageSellerName")
                .get(0).text();
            final Elements items = pkg.getElementsByClass("package-item");

            for (final Element item: items) {
                final String cardName = item.getElementsByAttributeValue("data-testid", "productName").text();
                final Integer quantity = Integer.parseInt(
                    item.getElementsByClass("add-to-cart__dropdown__overlay").get(0).textNodes().get(0).text().strip());
                final Double price = Double.parseDouble(item.getElementsByAttributeValue("data-testid", "txtItemPrice")
                    .get(0).text().replace("$", "").strip());
                final Item packageItem = new Item(cardName, quantity, price, packageName);
                itemList.add(packageItem);
            }
        }

        return itemList
            .stream()
            .collect(Collectors.collectingAndThen(
            Collectors.toMap(Item::getCardName, Function.identity(), Item::mergeItems), m -> new HashSet<>(m.values())));
    }

    @Override
    public Double extractTotalPrice() {
        return Double.parseDouble(document.getElementsByAttributeValue("data-testid", "txtCartSubtotal")
            .get(0).text().replace("$", "").strip());
    }

}
