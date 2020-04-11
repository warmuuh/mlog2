package mlog.plugin.dummy;

import java.util.List;
import mlog.ctrl.rt.Channel;
import mlog.domain.LoggerConf;
import mlog.plugin.LoggerPlugin;

public class DummyLogger implements LoggerPlugin {

  @Override
  public List<String> getSupportedProtocols() {
    return List.of("dummy");
  }

  @Override
  public List<Channel> createChannels(LoggerConf loggerConfig) {
    return List.of(new DummyChannel(loggerConfig.getUri().getHost()));
  }
}
