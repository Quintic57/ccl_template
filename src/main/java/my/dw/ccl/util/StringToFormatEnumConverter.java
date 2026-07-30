package my.dw.ccl.util;

import my.dw.ccl.domain.Format;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToFormatEnumConverter implements Converter<String, Format> {

  @Override
  public Format convert(final String name) {
    return Format.fromName(name);
  }

}
