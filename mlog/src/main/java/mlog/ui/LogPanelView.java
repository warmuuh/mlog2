package mlog.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.CfgRunContext;
import mlog.logback.LogbackConfiguration;
import mlog.ui.commands.AppCommand;
import mlog.ui.commands.AppCommand.ShowApplicationLog;
import mlog.ui.components.FlatSVGIcon;
import mlog.utils.Event;

@Singleton
@Slf4j
public class LogPanelView extends JPanel {

  public Event<AppCommand> OnCommand = new Event<>();
  private final JLabel lastLogMessage;

  public LogPanelView() {
    lastLogMessage = new JLabel("some log message");
    add(lastLogMessage);
    lastLogMessage.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        OnCommand.invoke(new ShowApplicationLog());
      }
    });
  }

  @PostConstruct
  public void setup() {
    LogbackConfiguration.getInmemoryAppender()
        .OnAppend(evt ->
        lastLogMessage.setText(evt.getFormattedMessage())
    );
  }

}
