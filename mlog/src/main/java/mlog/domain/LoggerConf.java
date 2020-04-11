package mlog.domain;

import java.net.URI;
import lombok.Value;
import mlog.ctrl.rt.Channel;

@Value
public class LoggerConf {
  URI uri;

}
