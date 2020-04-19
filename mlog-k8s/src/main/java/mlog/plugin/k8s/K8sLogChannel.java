package mlog.plugin.k8s;

import static mlog.utils.UrlUtils.splitQuery;

import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.StatefulLogParser;
import mlog.domain.LoggerConf;

@Slf4j
public class K8sLogChannel extends PodLoggingChannel {

  private Map<String, List<String>> options;

  public K8sLogChannel(String podname, Map<String, List<String>> options,
      StatefulLogParser parser) {
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
