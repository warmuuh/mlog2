package mlog.ctrl.rt;

import java.util.ArrayList;
import java.util.List;
import mlog.utils.Event;
import reactor.core.publisher.Flux;

public class MessageBuffer {

  protected ArrayList<Message> buffer = new ArrayList<>(20);

  public Event<List<Message>> onRowsAdded = new Event<>();

  public MessageBuffer(Flux<List<Message>> incomingBatches){
    incomingBatches.subscribe(msgBatch -> addMessages(msgBatch));
  }

  protected void addMessages(List<Message> msgBatch) {
    buffer.addAll(msgBatch);
    onRowsAdded.invoke(msgBatch);
  }


  public ArrayList<Message> getBuffer() {
    return buffer;
  }
}
