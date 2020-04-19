package mlog.ui;

import java.util.List;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.CfgRunContext;
import mlog.ui.components.FlatSVGIcon;

@Singleton
@Slf4j
public class StatusView extends JPanel {


  private final JButton status;
  private final JProgressBar queueBar;
  private final JProgressBar bufferSize;
  private final JLabel msgPerSec;

  public StatusView() {
    status = new JButton(new FlatSVGIcon("icons/status_inactive.svg"));
    add(status);

    add(new JLabel("Buffer Size:"));
    bufferSize = new JProgressBar(0, 100);
    add(bufferSize);


    add(new JLabel("Msg Per Sec:"));
    msgPerSec = new JLabel("0");
    add(msgPerSec);


    queueBar = new JProgressBar(0, 100);
    add(new JLabel("Queue Fill State:"));
    add(queueBar);
  }

  SwingWorker<String, String> monitoringThread;

  @RequiredArgsConstructor
  private class MonitoringThread extends SwingWorker<String, String> {
    private final CfgRunContext context;

    @Override
    protected String doInBackground() throws Exception {
      while(true) {
        publish("");
        Thread.sleep(1000);
      }
    }

    @Override
    protected void process(List<String> chunks) {
      double perc = (double) context.getQueue().getQueueSize() / context.getQueue().getBufferSize();
      queueBar.setValue((int)(perc * 100.0));
      queueBar.invalidate();

      double buffPerc = (double) context.getBuffer().getBuffer().size() / context.getBuffer().getBuffer().maxSize();
      bufferSize.setValue((int)(buffPerc * 100.0));
      bufferSize.invalidate();

      msgPerSec.setText("" + context.getBuffer().getMsgPerSec());
      msgPerSec.invalidate();

      status.setIcon(new FlatSVGIcon(context.getQueue().isAlive() ? "icons/status_active.svg" : "icons/status_inactive.svg"));

    }
  };

  public void startMonitor(CfgRunContext currentRunContext){

    if (monitoringThread != null) {
      monitoringThread.cancel(true);
    }

    monitoringThread = new MonitoringThread(currentRunContext);
    monitoringThread.execute();
  }


  public void stopMonitor(){
    if (monitoringThread != null) {
      monitoringThread.cancel(true);
    }
    status.setIcon(new FlatSVGIcon("icons/status_inactive.svg"));

  }
}
