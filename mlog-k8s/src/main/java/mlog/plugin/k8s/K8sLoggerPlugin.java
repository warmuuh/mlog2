package mlog.plugin.k8s;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.StatefulLogParser;
import mlog.domain.LoggerConf;
import mlog.plugin.LoggerPlugin;

@Slf4j
public class K8sLoggerPlugin implements LoggerPlugin {

  @Override
  public List<String> getSupportedProtocols() {
    return List.of("k8s", "k8s+tail");
  }

  @Override
  public List<Channel> createChannels(LoggerConf loggerConfig, Function<String, StatefulLogParser> parserFactory) {
    List<String> pods = getPods(loggerConfig.getUri());
    switch (loggerConfig.getUri().getScheme()){
      case "k8s":  return pods.stream().map(p -> new K8sLogChannel(p, loggerConfig, parserFactory.apply(p)))
          .collect(Collectors.toList());

      case "k8s+tail": return pods.stream().map(p -> new K8sTailChannel(p, loggerConfig.getUri().getPath(), loggerConfig, parserFactory.apply(p)))
          .collect(Collectors.toList());
    }
    throw new UnsupportedOperationException("Unsupported schema: " + loggerConfig.getUri().getScheme());
  }


  @SneakyThrows
  public List<String> getPods(URI kubeUri){
    String command = "kubectl get pods -n ebayk --selector=app=" + kubeUri.getHost() + " -o name";
    log.info("Exec: {}", command);
    Process listPodProc = Runtime.getRuntime()
        .exec(command);

    List<String> pods = new LinkedList<>();
    try(BufferedReader input = new BufferedReader(new InputStreamReader(listPodProc.getInputStream()))) {
      String line;
      while ((line = input.readLine()) != null) {
        pods.add(line);
      }
    }

    log.info("Found pods: {}", pods);
    return pods;
  }
}
