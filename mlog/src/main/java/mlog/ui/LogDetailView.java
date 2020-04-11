package mlog.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.inject.Singleton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import mlog.domain.Message;

@Singleton
public class LogDetailView extends JPanel {

  private final JTextArea detailsText;

  public LogDetailView() {
    setLayout(new BorderLayout());
    setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
    detailsText = new JTextArea();
    add(detailsText, BorderLayout.CENTER);
  }

  public void showDetails(Message message){
    detailsText.setText(message.getFields().toString());
  }

}
