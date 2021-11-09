package mlog.ctrl.rt.logging.json;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

public class JsonPropertySupplier {

  private final List<String> fields;
  @Getter
  private final String fieldName;

  public JsonPropertySupplier(String fieldName) {
    this.fieldName = fieldName;
    this.fields = Arrays.asList(fieldName.split("\\."));
  }

  public String getValue(JsonNode cur) {
    for (String field : fields) {
      if (cur == null) {
        break;
      }
      cur = cur.get(field);
    }

    if (cur != null) {
      return cur.asText();
    }
    return "";
  }

}
