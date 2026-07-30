package my.dw.ccl.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Game {

  YUGIOH("Yu-Gi-Oh!", "YGO"),
  SHADOWVERSE_EVOLVE("Shadowverse: Evolve", "SDE");

  private final String fullName;
  private final String abbreviation;

  Game(final String fullName, final String abbreviation) {
    this.fullName = fullName;
    this.abbreviation = abbreviation;
  }

  @JsonCreator
  public static Game fromAbbreviation(final String abbreviation) {
    return Game.YUGIOH;
  }

}
