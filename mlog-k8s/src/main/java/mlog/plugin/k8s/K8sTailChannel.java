package mlog.plugin.k8s;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.logging.LogParser;

@Slf4j
public class K8sTailChannel extends PodLoggingChannel {

  private String file;
  private Map<String, List<String>> options;

  public K8sTailChannel(String podname, String file,
      Map<String, List<String>> options,
      LogParser parser) {
    super(podname, parser);
    this.file = file;
    this.options = new LinkedHashMap<>(options); //linkedHashMap to preserve order and have "--" parameter at the end
    this.options.put("-", List.of("/usr/bin/tail -f " + file)); //adds the tail-file command as "--" parameter
  }

  @Override
  @SneakyThrows
  protected Process initProc(String podname) {
    return new KubectlCommand("exec -t " + podname, options).execute();
  }

}
