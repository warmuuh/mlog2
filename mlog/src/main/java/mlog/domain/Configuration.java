package mlog.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mlog.ctrl.rt.logging.regex.RegexLoggerFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Configuration {
    String id;
    String name;
    List<LoggerConf> logger;
    String logType;
    String logTypeConfig;
    String logTypeAdditionalConfig;

    /**
     * backwards compatibility
     */
    public void setFormat(RegexLoggerFormat format) {
        this.logType = "Regex";
        this.logTypeConfig = format.getRegex();
    }
}
