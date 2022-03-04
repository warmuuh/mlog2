package mlog.plugin.kafka;

import lombok.RequiredArgsConstructor;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.Message;
import mlog.ctrl.rt.logging.LogParser;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

@RequiredArgsConstructor
public class KafkaTopicChannel implements Channel {

  private final ReceiverOptions<byte[], byte[]> receiverOptions;
  private final LogParser parser;

  @Override
  public String getName() {
    return String.join(", ", receiverOptions.subscriptionTopics());
  }

  @Override
  public Flux<Message> getMessages() {
    return KafkaReceiver.create(receiverOptions)
        .receive()
        .map(kafkaMessage -> parser.parse(kafkaMessage.value()));
  }
}
