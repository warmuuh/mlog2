package mlog.plugin;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.StatefulLogParser;
import mlog.domain.LoggerConf;

public interface LoggerPlugin {

  List<String> getSupportedProtocols();

  List<Channel> createChannels(LoggerConf loggerConfig, Function<String, StatefulLogParser> parserFactory);

}
