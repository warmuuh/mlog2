package mlog.ctrl.rt.logging.json;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mlog.ctrl.rt.logging.LoggerFormat;


@NoArgsConstructor
@Getter
public class JsonLoggerFormat implements LoggerFormat {

  private List<String> fields;

  private List<JsonPropertySupplier> suppliers;

  public JsonLoggerFormat(String fields) {
    this.fields = Arrays.stream(fields.split(","))
        .map(String::trim)
        .collect(Collectors.toList());
  }

}
