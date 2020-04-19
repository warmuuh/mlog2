package mlog.plugin.k8s;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.StatefulLogParser;
import mlog.domain.LoggerConf;

@Slf4j
public class K8sTailChannel extends PodLoggingChannel {

  private String file;
  private Map<String, List<String>> options;

  public K8sTailChannel(String podname, String file,
      Map<String, List<String>> options,
      StatefulLogParser parser) {
    super(podname, parser);
    this.file = file;
    this.options = new HashMap<>(options);
    this.options.put("-", List.of("/usr/bin/tail -f " + file)); //adds the tail-file command as "--" parameter
  }

  @Override
  @SneakyThrows
  protected Process initProc(String podname) {
    return new KubectlCommand("exec -t " + podname, options).execute();
  }

}
