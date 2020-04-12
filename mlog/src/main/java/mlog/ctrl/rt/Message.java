package mlog.ctrl.rt;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Value
public class Message {
  String channel;
  Map<String, String> fields;
}