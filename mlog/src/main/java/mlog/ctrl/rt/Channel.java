package mlog.ctrl.rt;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mlog.domain.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;


public interface Channel {

  String getName();
  public Flux<Message> getMessages();
}
