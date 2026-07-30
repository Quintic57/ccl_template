package my.dw.ccl.config;

import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

  @Bean
  public CacheBuilder<Object, Object> deckCacheBuilder() {
    return CacheBuilder.newBuilder()
        .maximumSize(1000)
        .concurrencyLevel(4)
        .expireAfterWrite(Duration.ofMinutes(30L));
  }

}
