package my.dw.ccl.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum Format {

    // YGO
    YGO_CROSS_BANLIST(Game.YUGIOH, "Cross-Banlist"),
    YGO_MODERN(Game.YUGIOH, "Modern"),
    YGO_EDISON(Game.YUGIOH, "Edison", YearMonth.of(2010, 4)),
    YGO_GOAT(Game.YUGIOH, "GOAT", YearMonth.of(2005, 4)),

    // SDE
    SDE_CLASSIC(Game.SHADOWVERSE_EVOLVE, "Classic");

    private final Game game;

    private final String name;

    private final YearMonth dateRange;

    Format(final Game game, final String name) {
        this(game, name, null);
    }

    Format(final Game game, final String name, final YearMonth dateRange) {
        this.game = game;
        this.name = name;
        this.dateRange = dateRange;
    }

    @Override
    public String toString() {
        return name + (dateRange != null ? " (" + dateRange.format(DateTimeFormatter.ofPattern("yyyy-MM")) + ")" : "");
    }

    @JsonCreator
    public static Format fromGameAndName(final Game game, final String name) {
        return Arrays.stream(Format.values())
            .filter(format -> format.name.equalsIgnoreCase(name) && format.game == game)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Invalid format: %s or game: %s", name, game)));
    }

    public static Map<String, Format> getFormatStringToObjectMap() {
        return Arrays.stream(Format.values())
            .collect(Collectors.toMap(Format::toString, format -> format, (o1, o2) -> o1, LinkedHashMap::new));
    }

}
