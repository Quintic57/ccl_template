package my.dw.ccl.adapter.shop;

import lombok.SneakyThrows;
import my.dw.ccl.adapter.Adapter;
import my.dw.ccl.dto.Item;
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
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: In the future, this should be able to pull the cart information just from the user context, but since that code
// is obfuscated, for now just pull it from the HTML
@Component
public class TcgPlayerAdapter implements Adapter {

    private final Document document;

    private static final String ALL_SUFFIXES;
    private static final String RARITY_SUFFIXES;
    private static final String OTHER_SUFFIXES;

    static {
        RARITY_SUFFIXES = String.join(
            ", ",
            Set.of(
                "(PCR)",
                "(PUR)",
                "(Platinum Secret Rare)",
                "(Quarter Century Secret Rare)",
                "(Secret Rare)",
                "(UR)",
                "(SR)"
            )
        );
        OTHER_SUFFIXES = String.join(", ", Set.of("(A)", "(B)", "(C)", "(Alternate Art)"));
        ALL_SUFFIXES = String.join(" | ", RARITY_SUFFIXES, OTHER_SUFFIXES);
    }

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
            final String vendorName = pkg.getElementsByAttributeValue("data-testid", "linkPackageSellerName")
                .get(0).text();
            final Elements items = pkg.getElementsByClass("package-item");

            for (final Element item: items) {
                // ---Card Name---
                String cardName = item.getElementsByAttributeValue("data-testid", "productName").text();
                // strip suffixes
                final Matcher matcher = Pattern.compile("\\([\\w\\s]*\\)").matcher(cardName);
                final List<String> matches = matcher.results()
                        .map(MatchResult::group)
                        .filter(ALL_SUFFIXES::contains)
                        .toList();
                for (final String match: matches) {
                    cardName = cardName.replace(match, "").strip();
                }
                // ---Quantity---
                final Integer quantity = Integer.parseInt(
                    item.getElementsByClass("add-to-cart__dropdown__overlay").get(0).textNodes().get(0).text().strip());
                // ---Price---
                final Double price = Double.parseDouble(item.getElementsByAttributeValue("data-testid", "txtItemPrice")
                    .get(0).text().replace("$", "").strip());

                itemList.add(new Item(cardName, quantity, price, vendorName));
            }
        }

        return new HashSet<>(itemList);
    }

    @Override
    public Double extractTotalPrice() {
        return Double.parseDouble(document.getElementsByAttributeValue("data-testid", "txtCartSubtotal")
            .get(0).text().replace("$", "").strip());
    }

    @Override
    public void extractVendors(Set<String> vendors) {
        final Elements packageElement = document.getElementsByClass("package");
        packageElement.remove(0);

        for (final Element pkg: packageElement) {
            final String vendorName = pkg.getElementsByAttributeValue("data-testid", "linkPackageSellerName")
                    .get(0).text();
            vendors.add(vendorName);
        }
    }

}
