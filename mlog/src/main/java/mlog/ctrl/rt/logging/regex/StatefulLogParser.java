package mlog.ctrl.rt.logging.regex;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mlog.ctrl.rt.Message;
import mlog.ctrl.rt.logging.LogParser;


public class StatefulLogParser implements LogParser {

  private final String channelName;
  private final RegexLoggerFormat format;
  private final Pattern pattern;


  public StatefulLogParser(String channelName, RegexLoggerFormat format) {
    this.channelName = channelName;
    this.format = format;
    this.pattern = Pattern.compile(format.getRegex(), Pattern.DOTALL);
  }

  public Message parse(String logline){
    Matcher matcher = pattern.matcher(logline);
    HashMap<String, String> entries = new HashMap<>();
    if (matcher.matches()){
      format.getFields().forEach(fieldName -> entries.put(fieldName, matcher.group(fieldName)));
    } else {
      entries.put("message", logline);
    }
    return new Message(channelName, entries);
  }

  public Message parse(String logline, Message prevMessage){
    Matcher matcher = pattern.matcher(logline);
    HashMap<String, String> entries = new HashMap<>();
    if (matcher.matches()){
      format.getFields().forEach(fieldName -> entries.put(fieldName, matcher.group(fieldName)));
      return new Message(channelName, entries);
    } else {
      if (prevMessage == null){
        prevMessage = new Message(channelName, new HashMap<>());
        prevMessage.getFields().put("message", logline);
        return prevMessage;
      }
      prevMessage.getFields().put("message", prevMessage.getFields().get("message") + "￿\n" + logline);
      return null;
    }
  }

}
