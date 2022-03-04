package mlog.ctrl.rt.logging.regex;

import java.util.List;
import java.util.Map;
import mlog.ctrl.rt.logging.LogParser;
import mlog.plugin.LogParserFactory;

public class RegexLogParserFactory implements LogParserFactory {

  @Override
  public String getLogFormatName() {
    return "Regex";
  }

  @Override
  public LogParser create(String channelName, String loggingFormatConfig, Map<String, String> additionalConfig) {
    return new StatefulLogParser(channelName, new RegexLoggerFormat(loggingFormatConfig));
  }

  @Override
  public List<String> getDefinedFields(String loggingFormatConfig, Map<String, String> additionalConfig) {
    return new RegexLoggerFormat(loggingFormatConfig).getFields();
  }
}
