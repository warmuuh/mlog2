package mlog.plugin;

import java.util.List;
import java.util.Map;
import mlog.ctrl.rt.logging.LogParser;

public interface LogParserFactory {

  String getLogFormatName();

  LogParser create(String channelName, String loggingFormatConfig, Map<String, String> additionalConfig);

  List<String> getDefinedFields(String loggingFormatConfig, Map<String, String> additionalConfig);
}
