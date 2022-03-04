package mlog.plugin.k8s;

import static mlog.utils.UrlUtils.splitQuery;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.logging.LogParser;
import mlog.domain.LoggerConf;
import mlog.plugin.LoggerPlugin;
import mlog.utils.swing.SwingDsl;

@Slf4j
public class K8sLoggerPlugin implements LoggerPlugin {

  @Override
  public List<String> getSupportedProtocols() {
    return List.of("k8s", "k8s+tail");
  }

  @Override
  public List<Channel> createChannels(LoggerConf loggerConfig, Function<String, LogParser> parserFactory) {
    Map<String, List<String>> options = splitQuery(loggerConfig.getUri().getQuery());
    List<String> pods = getPods(loggerConfig.getUri(), options);

    if (pods.size() > 1){
      var opts = new LinkedList<String>();
      opts.add("All Pods");
      opts.addAll(pods);
      var choice = SwingDsl.selectDialog("Which pod to show?", opts.toArray(new String[0]));
      if (choice == null) {
        return  Collections.emptyList();
      }
      if (!choice.equals("All Pods")) {
        pods = List.of(choice);
      }
    }

    switch (loggerConfig.getUri().getScheme()){
      case "k8s":  return pods.stream().map(p -> new K8sLogChannel(p, options, parserFactory.apply(p)))
          .collect(Collectors.toList());

      case "k8s+tail": return pods.stream().map(p -> new K8sTailChannel(p, loggerConfig.getUri().getPath(), options, parserFactory.apply(p)))
          .collect(Collectors.toList());
    }
    throw new UnsupportedOperationException("Unsupported schema: " + loggerConfig.getUri().getScheme());
  }


  @SneakyThrows
  public List<String> getPods(URI kubeUri, Map<String, List<String>> options){
    Process listPodProc = new KubectlCommand("get pods  -o name --selector=app=" + kubeUri.getHost(), options).execute(
        List.of("n", "namespace"));

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
