package my.dw.ccl.util;

import my.dw.ccl.domain.Game;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToGameEnumConverter implements Converter<String, Game> {

  @Override
  public Game convert(final String abbreviation) {
    return Game.fromAbbreviation(abbreviation);
  }

}
