package mlog.ctrl.rt;

import java.util.List;
import lombok.Value;
import reactor.core.Disposable;

@Value
public class CfgRunContext {
  MessageQueue queue;
  MessageBuffer buffer;
  List<Channel> channels;
  List<Disposable> disposables;

  public void stop(){
    queue.stop();
    disposables.forEach(Disposable::dispose);
  }
}
