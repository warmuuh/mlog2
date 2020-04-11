package mlog.ctrl.rt;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.inject.Singleton;
import mlog.domain.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;


public class MessageQueue {

  private UnicastProcessor<Message> processor = UnicastProcessor.create();

  public void process(Message message){
    processor.sink().next(message);
  }

  public Flux<List<Message>> getMessageBatches() {
    return processor.bufferTimeout(10, Duration.of(1, ChronoUnit.SECONDS));
  }

  public int getQueueSize(){
    return processor.size();
  }
  public int getBufferSize(){
    return processor.getBufferSize();
  }

  public void stop(){
    processor.cancel();
  }
}
