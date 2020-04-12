package mlog.persistence;

import java.io.ObjectInputFilter.Config;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.inject.Singleton;
import lombok.SneakyThrows;
import mlog.domain.Configuration;
import mlog.domain.LoggerConf;
import mlog.domain.LoggerFormat;

@Singleton
public class PersistenceManager {

  List<Configuration> configurations = new LinkedList<>();

  public PersistenceManager() {
    this.configurations.add(createKube("k8s-msgbox"));
    this.configurations.add(createDummyConfig("dummy1"));
  }

  public List<Configuration> findAllConfigurations(){
    return configurations;
  }

  @SneakyThrows
  private Configuration createDummyConfig(String name) {
    return new Configuration(UUID.randomUUID().toString(), name, new LinkedList<>(List.of(new LoggerConf(new URI("dummy://test"+name)))),
        new LoggerFormat("(?<date>\\d{1,2}:\\d{2}:\\d{2}.\\d{3})[ ]{1,2}(?<thread>\\[.*?\\])[ ](?<prio>INFO|DEBUG|WARN|ERROR)[ ](?<logger>.*?)[ ](?<message>.*)"));
  }

  @SneakyThrows
  private Configuration createKube(String name) {
    return new Configuration(UUID.randomUUID().toString(), name, new LinkedList<>(List.of(new LoggerConf(new URI("k8s://messagebox-service")))),
        new LoggerFormat("(?<date>\\d{4}\\-\\d{2}\\-\\d{1,2}[ ]{1,2}\\d{1,2}:\\d{2}:\\d{2},\\d{3})[ ]{1,2}(?<thread>\\[.*?\\])[ ](?<logger>.*?)[ ](?<prio>INFO|DEBUG|WARN|ERROR)[ ]{1,2}(?<message>.*)"));
  }

}
