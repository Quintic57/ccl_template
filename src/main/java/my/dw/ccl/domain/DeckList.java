package my.dw.ccl.domain;

import io.micrometer.common.util.StringUtils;
import my.dw.ccl.util.CsvSerializer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.sql.Array;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeckList {

    public static final List<Deck> DECK_LIST;

    static {
        //TODO: Move to config
        final Map<Format, String> formatFileNames = Map.of(
            Format.CROSS_BANLIST, "decklist_cb.csv",
            Format.MODERN, "decklist_md.csv",
            Format.EDISON, "decklist_ed.csv",
            Format.GOAT, "decklist_gt.csv"
        );

        DECK_LIST = Arrays.stream(Format.values())
            .map(format -> {
                final List<Deck> decks =
                    Files.exists(Paths.get("src/main/resources/decklist/" + formatFileNames.get(format)), LinkOption.NOFOLLOW_LINKS)
                        ? readFromCSVAndConvert(
                            new File("src/main/resources/decklist/" + formatFileNames.get(format)),
                            format)
                        : new ArrayList<>();
                decks.add(new Deck("[Staples]", YearMonth.of(2002, Month.MARCH), format));
                decks.add(new Deck("[Unlisted]", YearMonth.of(2002, Month.MARCH), format));
                return decks;
            })
            .flatMap(List::stream)
            .toList();
    }

    public static Map<String, Deck> getDeckStringToObjectMapForFormat(final Format format) {
        return DECK_LIST.stream()
            .filter(Deck::isActive)
            .filter(deck -> format == deck.getFormat())
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
            .collect(Collectors.toList());
    }

}
