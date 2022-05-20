package mlog.plugin.proto;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.DynamicMessage;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mlog.ctrl.rt.Message;
import mlog.ctrl.rt.logging.LogParser;

@RequiredArgsConstructor
public class ProtoLogParser implements LogParser {

  private final String channelName;
  private final Descriptor eventType;

  @Override
  @SneakyThrows
  public Message parse(byte[] logline) {
    DynamicMessage message = DynamicMessage.parseFrom(eventType, logline);

    Map<String, String> fieldValues = message.getAllFields().entrySet().stream()
        .collect(Collectors.toMap(
            f -> f.getKey().getName(),
            f -> {
              Object value = f.getValue();
              return value != null ? value.toString() : "";
            }
        ));
    return new Message(channelName, fieldValues);
  }

  @Override
  public Message parse(byte[] logline, Message prevMessage) {
    return parse(logline);
  }
}
