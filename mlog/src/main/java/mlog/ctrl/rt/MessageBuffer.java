package mlog.ctrl.rt;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import mlog.utils.Counter;
import mlog.utils.Event;
import mlog.utils.Event2;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import reactor.core.publisher.Flux;

public class MessageBuffer {

  protected CircularFifoQueue<Message> buffer = new CircularFifoQueue<Message>(50_000);

  public Event2<List<Message>, Integer> onRowsAdded = new Event2<>();

  private Counter msgPerSec = new Counter(1000);

  public MessageBuffer(Flux<List<Message>> incomingBatches){
    incomingBatches.subscribe(msgBatch -> addMessages(msgBatch));
  }

  protected void addMessages(List<Message> msgBatch) {
    try {
      int overflow = buffer.size() + msgBatch.size() - buffer.maxSize();
      buffer.addAll(msgBatch);
      msgPerSec.count(msgBatch.size());
      onRowsAdded.invoke(msgBatch, overflow);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public long getMsgPerSec() {
    return msgPerSec.getRate();
  }

  public CircularFifoQueue<Message> getBuffer() {
    return buffer;
  }
}
