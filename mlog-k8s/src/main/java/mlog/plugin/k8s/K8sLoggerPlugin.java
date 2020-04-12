package mlog.plugin.k8s;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.StatefulLogParser;
import mlog.domain.LoggerConf;
import mlog.plugin.LoggerPlugin;

public class K8sLoggerPlugin implements LoggerPlugin {

  @Override
  public List<String> getSupportedProtocols() {
    return List.of("k8s");
  }

  @Override
  public List<Channel> createChannels(LoggerConf loggerConfig, Function<String, StatefulLogParser> parserFactory) {
    List<String> pods = getPods(loggerConfig.getUri());
    return pods.stream().map(p -> new PodLoggingChannel(p, loggerConfig, parserFactory.apply(p)))
            .collect(Collectors.toList());
  }


  @SneakyThrows
  public List<String> getPods(URI kubeUri){
    Process listPodProc = Runtime.getRuntime()
        .exec("kubectl get pods -n ebayk --selector=app=" + kubeUri.getHost() + " -o name");

    List<String> pods = new LinkedList<>();
    try(BufferedReader input = new BufferedReader(new InputStreamReader(listPodProc.getInputStream()))) {
      String line;
      while ((line = input.readLine()) != null) {
        pods.add(line);
      }
    }

    return pods;
  }
}
