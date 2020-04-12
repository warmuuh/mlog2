package mlog.domain;

import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import mlog.ctrl.rt.Channel;

@Data
@AllArgsConstructor
public class LoggerConf {

  URI uri;

}
