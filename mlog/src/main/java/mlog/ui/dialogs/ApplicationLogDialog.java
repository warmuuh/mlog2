package mlog.ui.dialogs;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import mlog.logback.LogbackConfiguration;
import mlog.logback.LogbackInmemAppender;

public class ApplicationLogDialog extends JDialog {

  private JTextArea loggingArea;


  public ApplicationLogDialog(Frame owner) {
    super(owner, "Application Log", true);
    FlatDarkLaf.install();

    loggingArea = new JTextArea();
    loggingArea.setEditable(false);
    getContentPane().add(new JScrollPane(loggingArea));
    pack();
    setSize(new Dimension(1000, 500));
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowOpened(WindowEvent e) {
        LogbackInmemAppender<ILoggingEvent> logAppender = LogbackConfiguration.getInmemoryAppender();
        for(int i = 0; i < logAppender.getLength(); ++i) {
          ILoggingEvent evt = logAppender.get(i);
          loggingArea.append(evt.getLevel() + ":\t" + evt.getFormattedMessage());
          loggingArea.append("\n");
        }
        super.windowOpened(e);
      }

      @Override
      public void windowClosed(WindowEvent e) {
        super.windowClosed(e);
      }
    });
  }




}
