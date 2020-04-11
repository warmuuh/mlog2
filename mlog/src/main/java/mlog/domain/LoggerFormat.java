package mlog.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
public class LoggerFormat {
  List<String> fields;
}
