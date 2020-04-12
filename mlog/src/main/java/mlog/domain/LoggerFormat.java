package mlog.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;


@NoArgsConstructor
public class LoggerFormat {
  private String regex;
  private List<String> fields;


  public LoggerFormat(String regex) {
    this.regex = regex;
    compile();
  }

  public String getRegex() {
    return regex;
  }

  public List<String> getFields() {
    return fields;
  }

  public void setRegex(String regex) {
    this.regex = regex;
    compile();
  }

  private void compile(){
    Pattern pattern = Pattern.compile(regex, Pattern.DOTALL); // validate
    Matcher matcher = Pattern.compile("\\?<(.*?)>").matcher(regex);
    fields = new LinkedList<>();
    while(matcher.find()){
      fields.add(matcher.group(1));
    }
  }
}
