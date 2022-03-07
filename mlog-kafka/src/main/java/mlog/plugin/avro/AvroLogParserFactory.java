package mlog.plugin.avro;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import mlog.ctrl.rt.logging.LogParser;
import mlog.plugin.LogParserFactory;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;

public class AvroLogParserFactory implements LogParserFactory {

  @Override
  public String getLogFormatName() {
    return "Avro";
  }

  @Override
  @SneakyThrows
  public LogParser create(String channelName, String loggingFormatConfig, Map<String, String> additionalConfig) {
    String schema = additionalConfig.get("schema");
    if (schema == null) {
      throw new IllegalArgumentException("'schema' attribute is missing from avro logger additional config");
    }
    int offset = 0;
    if (additionalConfig.containsKey("offset")) {
      offset = Integer.parseInt(additionalConfig.get("offset"));
    }

    return new AvroLogParser(channelName, Schema.parse(new File(schema)), offset);
  }

  @Override
  @SneakyThrows
  public List<String> getDefinedFields(String loggingFormatConfig, Map<String, String> additionalConfig) {
    String schema = additionalConfig.get("schema");
    if (schema == null) {
      throw new IllegalArgumentException("'schema' attribute is missing from avro logger additional config");
    }

    List<String> specifiedFields = Arrays.stream(loggingFormatConfig.split(","))
        .map(String::trim)
        .collect(Collectors.toList());

    List<String> definedFields = Schema.parse(new File(schema)).getFields().stream()
        .map(Field::name)
        .collect(Collectors.toList());

    if (specifiedFields.isEmpty()) {
      return definedFields;
    }

    return definedFields.stream().filter(specifiedFields::contains).collect(Collectors.toList());
  }
}
