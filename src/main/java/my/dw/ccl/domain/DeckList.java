package my.dw.ccl.domain;

import io.micrometer.common.util.StringUtils;
import my.dw.ccl.util.CsvSerializer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeckList {

    public static final List<Deck> DECK_LIST;

    static {
        final List<Deck> crossBanList =
            Files.exists(Paths.get("src/main/resources/decklist/decklist_cb.csv"), LinkOption.NOFOLLOW_LINKS)
                ? readFromCSVAndConvert(
                    new File("src/main/resources/decklist/decklist_cb.csv"),
                    Format.CROSS_BANLIST)
                : List.of();
        final List<Deck> modern =
            Files.exists(Paths.get("src/main/resources/decklist/decklist_md.csv"), LinkOption.NOFOLLOW_LINKS)
                ? readFromCSVAndConvert(
                new File("src/main/resources/decklist/decklist_md.csv"),
                Format.MODERN)
                : List.of();
        final List<Deck> edison =
            Files.exists(Paths.get("src/main/resources/decklist/decklist_ed.csv"), LinkOption.NOFOLLOW_LINKS)
                ? readFromCSVAndConvert(
                new File("src/main/resources/decklist/decklist_ed.csv"),
                Format.EDISON)
                : List.of();
        final List<Deck> goat =
            Files.exists(Paths.get("src/main/resources/decklist/decklist_gt.csv"), LinkOption.NOFOLLOW_LINKS)
                ? readFromCSVAndConvert(
                new File("src/main/resources/decklist/decklist_gt.csv"),
                Format.GOAT)
                : List.of();
        // KEYWORDS
        final List<Deck> keywords = List.of(
            new Deck("[Staples]", YearMonth.of(2002, Month.MARCH), Format.UNLISTED),
            new Deck("[Unlisted]", YearMonth.of(2002, Month.MARCH), Format.UNLISTED)
        );
        DECK_LIST = Stream.of(crossBanList, modern, edison, goat, keywords)
            .flatMap(Collection::stream)
            .toList();
    }

    public static Map<String, Deck> getDeckStringToObjectMapForFormat(final Format format) {
        return DECK_LIST.stream()
            .filter(Deck::isActive)
            .filter(deck -> format == deck.getFormat() || deck.getFormat() == Format.UNLISTED)
            .sorted(Comparator.comparing(Deck::toString))
            .collect(Collectors.toMap(Deck::toString, deck -> deck, (o1, o2) -> o1, LinkedHashMap::new));
    }

    private static List<Deck> readFromCSVAndConvert(final File file, final Format format) {
        return CsvSerializer.readObjectsFromCsv(file)
            .stream()
            .map(m -> new Deck(
                    m.get("Name"),
                    YearMonth.of(
                        Integer.parseInt((m.get("Banlist")).split("-")[0]),
                        Integer.parseInt(( m.get("Banlist")).split("-")[1])
                    ),
                    format,
                    StringUtils.isNotBlank(m.get("Implemented Date")) ?  LocalDate.parse(m.get("Implemented Date")) : null,
                    Boolean.parseBoolean(m.get("Ported")),
                    Boolean.parseBoolean(m.get("Active")),
                    m.get("Shared"),
                    Boolean.parseBoolean(m.get("OCG")),
                    Boolean.parseBoolean(m.get("Custom"))
                )
            )
            .toList();
    }

}
