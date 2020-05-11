package mlog.ui.commands;

import lombok.Value;
import mlog.domain.Configuration;

public interface AppCommand {

  @Value
  class ExecConfiguration implements AppCommand {
    Configuration configuration;
  }


  @Value
  class StopCurrentConfiguration implements AppCommand {}

  @Value
  class ShowEditConfigurationDialog implements  AppCommand {}

  @Value
  class ClearBuffer implements  AppCommand {}

  @Value
  class SetAutoscroll implements  AppCommand {
    boolean autoscroll;
  }


}
