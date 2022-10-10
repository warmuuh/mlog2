package mlog.ui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.Taskbar;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import lombok.RequiredArgsConstructor;

@Singleton

public class MainWindow extends JFrame {


  public MainWindow(Toolbar toolbar,
      ConnectionView connectionView,
      LogView logView,
      LogDetailView logDetailView,
      StatusView statusView,
      LogPanelView logPanelView) throws HeadlessException {
    super("Mlog");

    setIcon();

    setLayout(new BorderLayout(5, 5));
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    getContentPane().add(toolbar, BorderLayout.PAGE_START);



    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPane.add(logView);
    splitPane.add(logDetailView);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(0.2);

    splitPane.setResizeWeight(0.8);
    splitPane.setContinuousLayout(true);

    JSplitPane verSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    verSplit.add(connectionView);
    verSplit.add(splitPane);
    verSplit.setOneTouchExpandable(true);
    verSplit.setDividerLocation(150);
    verSplit.setContinuousLayout(true);


    getContentPane().add(verSplit, BorderLayout.CENTER);
    JPanel bottomPannel = new JPanel();
    bottomPannel.setLayout(new BoxLayout(bottomPannel,BoxLayout.Y_AXIS));
    bottomPannel.add(statusView);
    bottomPannel.add(logPanelView);
    getContentPane().add(bottomPannel, BorderLayout.PAGE_END);



    pack();
    setSize(1200, 800);
  }

  private void setIcon() {
    setIconImage(new ImageIcon(getClass().getResource("/mlog-icon.png")).getImage());
    try {
      //doing it via AWT to support icon on macOs:
      var awtIcon = java.awt.Toolkit.getDefaultToolkit().getImage(getClass().getResource("/mlog-icon.png"));
      Taskbar awtTaskbar = Taskbar.getTaskbar();
      awtTaskbar.setIconImage(awtIcon);
    } catch (final Throwable e) {
      /* silently fail */
    }
  }

}
