package mlog.domain;

import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import mlog.ctrl.rt.Channel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggerConf {

  URI uri;

}
