package mlog.plugin.dummy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.StatefulLogParser;
import mlog.domain.LoggerConf;
import mlog.plugin.LoggerPlugin;
import mlog.utils.UrlUtils;

public class DummyLogger implements LoggerPlugin {

  @Override
  public List<String> getSupportedProtocols() {
    return List.of("dummy");
  }

  @Override
  public List<Channel> createChannels(LoggerConf loggerConfig, Function<String, StatefulLogParser> parserFactory) {
    Map<String, List<String>> params = UrlUtils.splitQuery(loggerConfig.getUri().getQuery());
    int msgRate = Integer.parseInt(params.getOrDefault("rate", List.of()).stream().findFirst().orElse("4"));
    return List.of(new DummyChannel(loggerConfig.getUri().getHost(), parserFactory.apply(loggerConfig.getUri().getHost()), msgRate));
  }
}
