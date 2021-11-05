package mlog.ctrl.rt.logging;

import mlog.ctrl.rt.Message;

public interface LogParser {
  Message parse(String logline);
  Message parse(String logline, Message prevMessage);
}
