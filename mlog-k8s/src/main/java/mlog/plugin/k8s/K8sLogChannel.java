package mlog.plugin.k8s;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.StatefulLogParser;
import mlog.domain.LoggerConf;

@Slf4j
public class K8sLogChannel extends PodLoggingChannel {

  public K8sLogChannel(String podname, LoggerConf config,
      StatefulLogParser parser) {
    super(podname, config, parser);
  }

  @Override
  @SneakyThrows
  protected Process initProc(String podname) {
    log.info("Open channel to " + podname);
    return Runtime.getRuntime()
        .exec("kubectl logs -f " + podname + "  -n ebayk -c logger");
  }

}
