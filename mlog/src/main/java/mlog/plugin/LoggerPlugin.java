package mlog.plugin;

import java.util.List;
import java.util.function.Function;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.logging.LogParser;
import mlog.ctrl.rt.logging.regex.StatefulLogParser;
import mlog.domain.LoggerConf;

public interface LoggerPlugin {

  List<String> getSupportedProtocols();

  List<Channel> createChannels(LoggerConf loggerConfig, Function<String, LogParser> parserFactory);

}
