package mlog.ctrl.rt.logging.regex;

import java.util.List;
import mlog.ctrl.rt.logging.LogParser;
import mlog.plugin.LogParserFactory;

public class RegexLogParserFactory implements LogParserFactory {

  @Override
  public String getLogFormatName() {
    return "Regex";
  }

  @Override
  public LogParser create(String channelName, String loggingFormatConfig) {
    return new StatefulLogParser(channelName, new RegexLoggerFormat(loggingFormatConfig));
  }

  @Override
  public List<String> getDefinedFields(String loggingFormatConfig) {
    return new RegexLoggerFormat(loggingFormatConfig).getFields();
  }
}
