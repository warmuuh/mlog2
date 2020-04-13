package mlog.plugin.k8s;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.StatefulLogParser;
import mlog.domain.LoggerConf;

@Slf4j
public class K8sTailChannel extends PodLoggingChannel {

  private String file;

  public K8sTailChannel(String podname, String file, LoggerConf config,
      StatefulLogParser parser) {
    super(podname, config, parser);
    this.file = file;
  }

  @Override
  @SneakyThrows
  protected Process initProc(String podname) {
    String command = "kubectl exec -t -n ebayk " + podname + "  -- /usr/bin/tail -f " + file;
    log.info("Exec: {} ", command);
    return Runtime.getRuntime()
        .exec(command);
  }

}
