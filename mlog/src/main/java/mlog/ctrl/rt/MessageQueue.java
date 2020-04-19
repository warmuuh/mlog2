package mlog.ctrl.rt;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;
import reactor.util.concurrent.Queues;


public class MessageQueue {

  private UnicastProcessor<Message> processor = UnicastProcessor.create(Queues.<Message>unboundedMultiproducer().get());

  public void process(Message message){
    if (processor.sink().isCancelled()){
      throw new RuntimeException("Queue is cancelled");
    }
    processor.sink().next(message);
  }

  public Flux<List<Message>> getMessageBatches() {
    return processor.bufferTimeout(100, Duration.of(1, ChronoUnit.SECONDS));
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

  public boolean isAlive() {
    return !processor.isDisposed();
  }
}
