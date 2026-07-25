package my.dw.ccl.domain.sde;

import java.util.Arrays;
import my.dw.ccl.domain.Format;

public enum SdeClass {

  ABYSSCRAFT("Abysscraft", "Abyss"),
  DRAGONCRAFT("Dragoncraft", "Dragon"),
  FORESTCRAFT("Forestcraft", "Forest"),
  HAVENCRAFT("Havencraft", "Haven"),
  NEUTRAL("Neutral", "Neutral"),
  RUNECRAFT("Runecraft", "Rune"),
  SWORDCRAFT("Swordcraft", "Sword");

  private final String name;
  private final String abbreviation;

  SdeClass(final String name, final String abbreviation) {
    this.name = name;
    this.abbreviation = abbreviation;
  }

  public static SdeClass fromAbbreviation(final String abbreviation) {
    return Arrays.stream(SdeClass.values())
        .filter(sdeClass -> sdeClass.abbreviation.equalsIgnoreCase(abbreviation))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Invalid class abbreviation: %s", abbreviation)));
  }

}
