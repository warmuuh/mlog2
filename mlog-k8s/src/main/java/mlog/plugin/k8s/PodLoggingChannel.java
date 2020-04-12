package mlog.plugin.k8s;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.Message;
import mlog.ctrl.rt.StatefulLogParser;
import mlog.domain.LoggerConf;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Slf4j
public class PodLoggingChannel implements Channel {

  private final String podname;
  private final LoggerConf config;
  private final StatefulLogParser parser;


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
        initProc();
      }
      BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));

//      log.info("poll");
      while (proc.isAlive()) {
//        log.info("poll msg");
        try {
          Message prevMessage = null;
          while (input.ready()) {
            Message curMsg = parser.parse(input.readLine(), prevMessage);
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

  @SneakyThrows
  private void initProc() {
    log.info("Open channel to " + podname);
    proc = Runtime.getRuntime()
        .exec("kubectl logs -f " + podname + "  -n ebayk -c logger");
  }
}
