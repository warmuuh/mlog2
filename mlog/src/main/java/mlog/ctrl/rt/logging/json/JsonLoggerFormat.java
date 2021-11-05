package mlog.ctrl.rt.logging.json;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import mlog.ctrl.rt.logging.LoggerFormat;


@NoArgsConstructor
public class JsonLoggerFormat implements LoggerFormat {

  private List<String> fields;

  public JsonLoggerFormat(String fields) {
    this.fields = Arrays.stream(fields.split(","))
        .map(String::trim)
        .collect(Collectors.toList());
  }

  public List<String> getFields() {
    return fields;
  }

}
