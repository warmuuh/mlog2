package mlog.plugin;

import java.util.List;
import mlog.ctrl.rt.logging.LogParser;

public interface LogParserFactory {

  String getLogFormatName();

  LogParser create(String channelName, String loggingFormatConfig);

  List<String> getDefinedFields(String loggingFormatConfig);
}
