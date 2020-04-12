package mlog.plugin.dummy;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.Message;
import mlog.ctrl.rt.StatefulLogParser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

@RequiredArgsConstructor
public class DummyChannel implements Channel {

  private final String message;
  private final StatefulLogParser parser;
  @Override
  public String getName() {
    return "dummy";
  }

  @Override
  public Flux<Message> getMessages(){
    Consumer<SynchronousSink<String>> gen = s -> s.next("08:59:33.047 [threadname] INFO mlog.plugin.dummy.DummyChannel " + message);
    return Flux.generate(gen)
        .map(parser::parse)
        .delayElements(Duration.of(250, ChronoUnit.MILLIS));
  }
}
