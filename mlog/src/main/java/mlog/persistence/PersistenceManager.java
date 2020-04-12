package mlog.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jdi.ObjectCollectedException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputFilter.Config;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mlog.PlatformUtil;
import mlog.domain.Configuration;
import mlog.domain.LoggerConf;
import mlog.domain.LoggerFormat;

@Singleton
@Slf4j
public class PersistenceManager {

  List<Configuration> configurations = new LinkedList<>();
  ObjectMapper mapper = new ObjectMapper();
  boolean loadError = false;

  public PersistenceManager() {

  }

  public List<Configuration> findAllConfigurations(){
    return configurations;
  }

  public void storeAll(){
    if (loadError){
      log.warn("Loading error of persistence storage. Wont overwrite file");
      return;
    }

    try {
      File dbFile = getDbFile();
      log.info("Persisting data to " + dbFile);
      mapper.writeValue(dbFile, this.configurations);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @PostConstruct
  public void init(){
    File file = getDbFile();
    if (file.exists()){
      try {
        configurations = mapper.readValue(file, new TypeReference<List<Configuration>>() {});
        log.info("Loaded {} configurations from storage", configurations.size());
      } catch (IOException e) {
        loadError = true;
        e.printStackTrace();
      }
    }
  }

  private File getDbFile() {
    String filename = PlatformUtil.getWritableLocationForFile("configurations.json");
    return new File(filename);
  }

}
