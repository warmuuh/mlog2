package mlog.logback;

import ch.qos.logback.core.read.CyclicBufferAppender;
import java.util.function.Consumer;
import mlog.utils.CheckedConsumer;
import mlog.utils.Event;

public class LogbackInmemAppender<T> extends CyclicBufferAppender<T> {

  private Event<T> onAppedEvent = new Event<>();

  @Override
  protected void append(T eventObject) {
    super.append(eventObject);
    onAppedEvent.invoke(eventObject);
  }

  public void OnAppend(CheckedConsumer<T> consumer) {
    onAppedEvent.add(consumer);
  }
}
