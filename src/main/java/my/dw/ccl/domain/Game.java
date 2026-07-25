package my.dw.ccl.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.Getter;

public enum Game {

  YUGIOH("Yu-Gi-Oh!", "YGO"),
  SHADOWVERSE_EVOLVE("Shadowverse: Evolve", "SDE");

  private final String fullName;
  @Getter
  private final String abbreviation;

  Game(final String fullName, final String abbreviation) {
    this.fullName = fullName;
    this.abbreviation = abbreviation;
  }

  @JsonCreator
  public static Game fromAbbreviation(final String abbreviation) {
      return Arrays.stream(Game.values())
              .filter(game -> game.abbreviation.equalsIgnoreCase(abbreviation))
              .findFirst()
              .orElseThrow(() -> new IllegalArgumentException(
                      String.format("Invalid game: %s", abbreviation)));
  }

}
