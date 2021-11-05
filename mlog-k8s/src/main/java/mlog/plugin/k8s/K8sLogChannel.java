package mlog.plugin.k8s;

import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.logging.LogParser;

@Slf4j
public class K8sLogChannel extends PodLoggingChannel {

  private Map<String, List<String>> options;

  public K8sLogChannel(String podname, Map<String, List<String>> options,
      LogParser parser) {
    super(podname, parser);
    this.options = options;
  }

  @Override
  @SneakyThrows
  protected Process initProc(String podname) {
    log.info("Open channel to " + podname);
    return new KubectlCommand("logs -f " + podname, options).execute();
  }

}
