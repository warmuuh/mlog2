package mlog.domain;

import java.util.Map;
import lombok.Value;

@Value
public class Message {
  String channel;
  Map<String, String> fields;
}