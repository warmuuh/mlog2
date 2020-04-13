package mlog.plugin.dummy;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.Message;
import mlog.ctrl.rt.StatefulLogParser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RequiredArgsConstructor
public class DummyChannel implements Channel {

  private final String message;
  private final StatefulLogParser parser;
  private final int msgPerSec;

  @Override
  public String getName() {
    return "dummy";
  }

  @Override
  public Flux<Message> getMessages(){
    Flux<String> flux = Flux.create(sink -> {
      int msgCounter = 0;
      AtomicBoolean goOn = new AtomicBoolean(true);
      sink.onCancel(() -> {
        log.info("Cancelling dummy process: " + message);
        goOn.set(false);
      });

      sink.onDispose(() -> {
        log.info("Cancelling dummy process: " + message);
        goOn.set(false);
      });

      while(goOn.get()){
        sink.next("08:59:33.047 [threadname] INFO mlog.plugin.dummy.DummyChannel " + message + " count: " + msgCounter);
        msgCounter++;
        try {
          Thread.sleep(1000 / (long)msgPerSec);
        } catch (InterruptedException e) {
          break;
        }
      }

    });

    return flux
        .subscribeOn(Schedulers.boundedElastic())
        .map(parser::parse);
  }
}
