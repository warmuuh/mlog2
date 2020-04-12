package mlog.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Data
@AllArgsConstructor
public class Configuration {
    String id;
    String name;
    List<LoggerConf> logger;
    LoggerFormat format;
}
