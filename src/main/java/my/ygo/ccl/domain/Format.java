package my.ygo.ccl.domain;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public enum Format {

    CROSS_BANLIST("Cross-Banlist"),
    EDISON("Edison", YearMonth.of(2010, 4)),
    GOAT("GOAT", YearMonth.of(2005, 4)),
    MODERN("Modern"),
    UNLISTED("Unlisted");

    private String name;

    private final YearMonth banList;

    Format(final String name) {
        this(name, null);
    }

    Format(final String name, final YearMonth banList) {
        this.name = name;
        this.banList = banList;
    }

    @Override
    public String toString() {
        return name + (banList != null ? " (" + banList.format(DateTimeFormatter.ofPattern("yyyy-MM")) + ")" : "");
    }

    public static Map<String, Format> getFormatStringToObjectMap() {
        return Arrays.stream(Format.values())
            .collect(Collectors.toMap(Format::toString, format -> format, (o1, o2) -> o1, LinkedHashMap::new));
    }

}
