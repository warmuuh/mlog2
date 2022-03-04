package mlog.ctrl.rt.logging.regex;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mlog.ctrl.rt.Message;
import mlog.ctrl.rt.logging.LogParser;
import mlog.ctrl.rt.logging.json.JsonLogParser;
import mlog.ctrl.rt.logging.json.JsonLoggerFormat;
import mlog.plugin.LogParserFactory;


public class StatefulLogParser implements LogParser {

  private final String channelName;
  private final RegexLoggerFormat format;
  private final Pattern pattern;


  public StatefulLogParser(String channelName, RegexLoggerFormat format) {
    this.channelName = channelName;
    this.format = format;
    this.pattern = Pattern.compile(format.getRegex(), Pattern.DOTALL);
  }

  public Message parse(byte[] logline){
    String input = new String(logline);
    Matcher matcher = pattern.matcher(input);
    HashMap<String, String> entries = new HashMap<>();
    if (matcher.matches()){
      format.getFields().forEach(fieldName -> entries.put(fieldName, matcher.group(fieldName)));
    } else {
      entries.put("message", input);
    }
    return new Message(channelName, entries);
  }

  public Message parse(byte[] logline, Message prevMessage){
    String input = new String(logline);
    Matcher matcher = pattern.matcher(input);
    HashMap<String, String> entries = new HashMap<>();
    if (matcher.matches()){
      format.getFields().forEach(fieldName -> entries.put(fieldName, matcher.group(fieldName)));
      return new Message(channelName, entries);
    } else {
      if (prevMessage == null){
        prevMessage = new Message(channelName, new HashMap<>());
        prevMessage.getFields().put("message", input);
        return prevMessage;
      }
      prevMessage.getFields().put("message", prevMessage.getFields().get("message") + "\n" + logline);
      return null;
    }
  }

}
