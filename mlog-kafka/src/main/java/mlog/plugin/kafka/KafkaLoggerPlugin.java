package mlog.plugin.kafka;

import static mlog.utils.UrlUtils.splitQuery;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.logging.LogParser;
import mlog.domain.LoggerConf;
import mlog.plugin.LoggerPlugin;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import reactor.kafka.receiver.ReceiverOptions;

@Slf4j
public class KafkaLoggerPlugin implements LoggerPlugin {

  @Override
  public List<String> getSupportedProtocols() {
    return List.of("kafka");
  }

  @Override
  @SneakyThrows
  public List<Channel> createChannels(LoggerConf loggerConfig, Function<String, LogParser> parserFactory) {
    String bootstrapHost = loggerConfig.getUri().getHost();
    int port = loggerConfig.getUri().getPort() > 0 ? loggerConfig.getUri().getPort() : 9092;
    String topic = loggerConfig.getUri().getPath();

    if (topic.startsWith("/")) {
      topic = topic.substring(1);
    }

    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapHost + ":" + port);


    Map<String, List<String>> options = splitQuery(loggerConfig.getUri().getQuery());
    if (options.containsKey("props")) {
      Properties properties = new Properties();
      properties.load(new FileInputStream(options.get("props").get(0)));
      properties.forEach((k,v) -> consumerProps.put(k.toString(),v.toString()));
    }

    if (options.containsKey("group")) {
      consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, options.get("group").get(0));
    } else {
      consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "sample-group");
    }
    consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);


//    consumerProps.put(SslConfigs.SSL_ENGINE_FACTORY_CLASS_CONFIG, MySslEngineFactory.class);
//    consumerProps.put(SslConfigs.SSL_PROTOCOL_CONFIG, "TLSv1.2");

    ReceiverOptions<byte[], byte[]> receiverOptions =
        ReceiverOptions.<byte[], byte[]>create(consumerProps)
            .subscription(Collections.singleton(topic));

    return List.of(new KafkaTopicChannel(receiverOptions, parserFactory.apply(topic)));
  }

}
