package mlog.domain;

public enum LogType {
  Regex,
  Json;

  public static LogType parse(String value){
    try{
      return LogType.valueOf(value);
    } catch (IllegalArgumentException e){
      return null;
    }
  }
}
