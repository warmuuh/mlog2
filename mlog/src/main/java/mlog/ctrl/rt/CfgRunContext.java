package mlog.ctrl.rt;

import java.util.List;
import lombok.Value;

@Value
public class CfgRunContext {
  MessageQueue queue;
  MessageBuffer buffer;
  List<Channel> channels;
}
