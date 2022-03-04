package mlog.ctrl.rt.logging.json;

import java.util.List;
import java.util.Map;
import mlog.ctrl.rt.logging.LogParser;
import mlog.plugin.LogParserFactory;

public class JsonLogParserFactory implements LogParserFactory {

  @Override
  public String getLogFormatName() {
    return "Json";
  }

  @Override
  public LogParser create(String channelName, String loggingFormatConfig, Map<String, String> additionalConfig) {
    return new JsonLogParser(channelName, new JsonLoggerFormat(loggingFormatConfig));
  }

  @Override
  public List<String> getDefinedFields(String loggingFormatConfig, Map<String, String> additionalConfig) {
    return new JsonLoggerFormat(loggingFormatConfig).getFields();
  }
}
