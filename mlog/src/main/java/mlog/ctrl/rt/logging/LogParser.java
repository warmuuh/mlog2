package mlog.ctrl.rt.logging;

import mlog.ctrl.rt.Message;

public interface LogParser {
  Message parse(byte[] logline);
  Message parse(byte[] logline, Message prevMessage);
}
