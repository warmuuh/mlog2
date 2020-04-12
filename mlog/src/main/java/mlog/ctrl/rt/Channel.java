package mlog.ctrl.rt;

import reactor.core.publisher.Flux;


public interface Channel {

  String getName();
  public Flux<Message> getMessages();
}
