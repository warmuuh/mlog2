package mlog.plugin.k8s;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.Message;
import mlog.ctrl.rt.logging.LogParser;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Slf4j
public abstract class PodLoggingChannel implements Channel {

  private final String podname;
  private final LogParser parser;


  private Process proc;

  @Override
  public String getName() {
    return podname;
  }

  @Override
  public Flux<Message> getMessages() {
    Flux<Message> flux = Flux.create(sink -> {
      sink.onCancel(() -> {
        log.info("Cancelling process: " + podname);
        if (proc != null)
          proc.destroy();
        proc = null;
      });

      sink.onDispose(() -> {
        log.info("Disposing process: " + podname);
        if (proc != null)
          proc.destroy();
        proc = null;
      });

      if (proc == null) {
        proc = initProc(podname);
      }

      BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));

//      log.info("poll");
      while (proc.isAlive()) {
//        log.info("poll msg");
        try {
          Message prevMessage = null;
          while (input.ready()) {
            Message curMsg = parser.parse(input.readLine().getBytes(StandardCharsets.UTF_8), prevMessage);
            if (curMsg != null){
              if (prevMessage != null){
                sink.next(prevMessage);
              }
              prevMessage = curMsg;
            }
          }
          if (prevMessage != null){
            sink.next(prevMessage);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }

        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
         /* */
        }
      }
      log.info("finishing log pipe");

    });


    return flux
        .subscribeOn(Schedulers.boundedElastic());
//        .map(parser::parse);
  }


  protected abstract Process initProc(String podname);
}
