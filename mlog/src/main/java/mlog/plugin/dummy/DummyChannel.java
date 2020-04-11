package mlog.plugin.dummy;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import mlog.ctrl.rt.Channel;
import mlog.domain.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

@RequiredArgsConstructor
public class DummyChannel implements Channel {

  private final String message;

  @Override
  public String getName() {
    return "dummy";
  }

  @Override
  public Flux<Message> getMessages(){
    Consumer<SynchronousSink<Message>> gen = s -> s.next(new Message("dummyChan", Map.of("message",message)));
    return Flux.generate(gen)
        .delayElements(Duration.of(250, ChronoUnit.MILLIS));
  }
}
