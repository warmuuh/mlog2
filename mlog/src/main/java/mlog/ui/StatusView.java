package mlog.ui;

import java.util.List;
import javax.inject.Singleton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mlog.ctrl.rt.CfgRunContext;

@Singleton
@Slf4j
public class StatusView extends JPanel {


  private final JProgressBar progressBar;
  private final JLabel bufferSize;

  public StatusView() {
    bufferSize = new JLabel("0");
    add(new JLabel("Buffer Size:"));
    add(bufferSize);

    progressBar = new JProgressBar(0, 100);
    add(new JLabel("Queue Fill State:"));
    add(progressBar);
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
      progressBar.setValue((int)(perc * 100.0));
      bufferSize.setText(""+context.getBuffer().getBuffer().size());
      bufferSize.invalidate();
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
    if (monitoringThread != null)
      monitoringThread.cancel(true);
  }
}
