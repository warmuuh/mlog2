package mlog.persistence;

import java.net.URI;
import java.util.List;
import javax.inject.Singleton;
import lombok.SneakyThrows;
import mlog.domain.Configuration;
import mlog.domain.LoggerConf;
import mlog.domain.LoggerFormat;

@Singleton
public class PersistenceManager {

  public List<Configuration> findAllConfigurations(){
    return List.of(createDummyConfig("dummy1"), createDummyConfig("dummy2"));
  }

  @SneakyThrows
  private Configuration createDummyConfig(String name) {
    return new Configuration(name, new LoggerConf(new URI("dummy://test"+name)),
        new LoggerFormat(
        List.of("message")));
  }

}
