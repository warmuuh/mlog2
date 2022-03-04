package mlog.plugin.kafka;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.logging.LogParser;
import mlog.domain.LoggerConf;
import mlog.plugin.LoggerPlugin;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import reactor.kafka.receiver.ReceiverOptions;

@Slf4j
public class KafkaLoggerPlugin implements LoggerPlugin {

  @Override
  public List<String> getSupportedProtocols() {
    return List.of("kafka");
  }

  @Override
  public List<Channel> createChannels(LoggerConf loggerConfig, Function<String, LogParser> parserFactory) {
    String bootstrapHost = loggerConfig.getUri().getHost();
    int port = loggerConfig.getUri().getPort() > 0 ? loggerConfig.getUri().getPort() : 9092;
    String topic = loggerConfig.getUri().getPath();
    if (topic.startsWith("/")) {
      topic = topic.substring(1);
    }

    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapHost + ":" + port);
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "sample-group");
    consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);

    ReceiverOptions<byte[], byte[]> receiverOptions =
        ReceiverOptions.<byte[], byte[]>create(consumerProps)
            .subscription(Collections.singleton(topic));

    return List.of(new KafkaTopicChannel(receiverOptions, parserFactory.apply(topic)));
  }

}
