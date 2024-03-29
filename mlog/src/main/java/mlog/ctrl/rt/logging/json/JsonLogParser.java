package mlog.ctrl.rt.logging.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import mlog.ctrl.rt.Message;
import mlog.ctrl.rt.logging.LogParser;
import mlog.plugin.LogParserFactory;

public class JsonLogParser implements LogParser {

  private final String channelName;
  private final JsonLoggerFormat format;
  private final ObjectMapper objectMapper;


  public JsonLogParser(String channelName, JsonLoggerFormat format) {
    this.channelName = channelName;
    this.format = format;
    this.objectMapper = new ObjectMapper();
  }

  public Message parse(byte[] logline) {
    HashMap<String, String> entries = new HashMap<>();
    try {
      var node = objectMapper.readTree(logline);
      format.getSuppliers().forEach(s -> {
        entries.put(s.getFieldName(), s.getValue(node));
      });
    } catch (Exception e) {
      /* ignore line */
    }
    return new Message(channelName, entries);
  }

  @Override
  public Message parse(byte[] logline, Message prevMessage) {
    return parse(logline);
  }

}
