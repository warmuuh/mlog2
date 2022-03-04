package mlog.ctrl.rt.logging.json;

import java.util.List;
import mlog.ctrl.rt.logging.LogParser;
import mlog.plugin.LogParserFactory;

public class JsonLogParserFactory implements LogParserFactory {

  @Override
  public String getLogFormatName() {
    return "Json";
  }

  @Override
  public LogParser create(String channelName, String loggingFormatConfig) {
    return new JsonLogParser(channelName, new JsonLoggerFormat(loggingFormatConfig));
  }

  @Override
  public List<String> getDefinedFields(String loggingFormatConfig) {
    return new JsonLoggerFormat(loggingFormatConfig).getFields();
  }
}
