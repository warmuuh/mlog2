package mlog.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.inject.Singleton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import mlog.ctrl.rt.Message;

@Singleton
public class LogDetailView extends JPanel {

  private final JTextArea detailsText;
  private final JScrollPane scrollPane;

  public LogDetailView() {
    setLayout(new BorderLayout());
    setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
    detailsText = new JTextArea();
    detailsText.setLineWrap(true);
    detailsText.setWrapStyleWord(true);
    scrollPane = new JScrollPane(detailsText);
    add(scrollPane, BorderLayout.CENTER);
  }

  public void showDetails(Message message){
    detailsText.setText(message.getFields().toString());
    detailsText.setCaretPosition(0);
  }

}
