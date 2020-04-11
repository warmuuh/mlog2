package mlog.domain;

import lombok.Value;

@Value
public class Configuration {
    String name;
    LoggerConf logger;
    LoggerFormat format;
}
