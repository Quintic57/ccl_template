package my.dw.ccl.domain.sde;

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

}
