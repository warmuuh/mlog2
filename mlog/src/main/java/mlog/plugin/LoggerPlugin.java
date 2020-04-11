package mlog.plugin;

import java.util.List;
import mlog.ctrl.rt.Channel;
import mlog.domain.LoggerConf;

public interface LoggerPlugin {

  List<String> getSupportedProtocols();

  List<Channel> createChannels(LoggerConf loggerConfig);

}
