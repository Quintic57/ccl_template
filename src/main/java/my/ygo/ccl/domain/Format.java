package my.ygo.ccl.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Format {

    CROSS_BANLIST("Cross-Banlist"),
    EDISON("Edison"),
    GOAT("GOAT"),
    MODERN("Modern"),
    UNLISTED("Unlisted");

    private String name;

    Format(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<Format> getListedFormats() {
        return Arrays.stream(Format.values())
            .filter(format -> !format.equals(Format.UNLISTED))
            .collect(Collectors.toList());
    }

}
