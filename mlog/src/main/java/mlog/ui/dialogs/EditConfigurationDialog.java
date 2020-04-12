package mlog.ui.dialogs;

import static mlog.utils.swing.SwingDsl.*;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.ui.FlatOptionPaneUI;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import lombok.extern.java.Log;
import mlog.domain.Configuration;
import mlog.domain.LoggerConf;
import mlog.ui.components.FlatSVGIcon;
import mlog.utils.swing.Bindings;
import mlog.utils.swing.GridBagConstraintHelper;
import mlog.utils.swing.SwingDsl;

public class EditConfigurationDialog extends JDialog {

  private JList<Configuration> configurationList;
  private final JPanel optionPane;
  private final Bindings bindings = new Bindings();
  private List<Configuration> configurations;

  public EditConfigurationDialog(List<Configuration> configurations, Frame owner) {
    super(owner, "Edit Configurations...", true);
    this.configurations = configurations;
    FlatDarkLaf.install();

    getContentPane().setLayout(new BorderLayout(5, 5));
    JPanel leftSide = createConfigListView(configurations);

    optionPane = new JPanel();
    optionPane.setLayout(new GridBagLayout());
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide, optionPane);
    split.setDividerLocation(200);
    getContentPane().add(split);
    pack();
    setSize(new Dimension(1000, 500));
  }

  private JPanel createConfigListView(List<Configuration> configurations) {
    JPanel leftSide = new JPanel();
    leftSide.setLayout(new BorderLayout());
    JToolBar toolBar = new JToolBar();
    toolBar.add(new JButton(new FlatSVGIcon("icons/add.svg")));
    toolBar.add(new JButton(new FlatSVGIcon("icons/remove.svg")));
    leftSide.add(toolBar, BorderLayout.PAGE_START);
    configurationList = new JList<>();
    configurationList.setListData(configurations.toArray(new Configuration[]{}));
    configurationList.getSelectionModel().addListSelectionListener(evt -> {
      showOptionsFor(configurationList.getSelectedValue());

    });
    configurationList.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
          boolean isSelected, boolean cellHasFocus) {
        if (value instanceof Configuration) {
          value = ((Configuration) value).getName();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
    });

    leftSide.add(configurationList, BorderLayout.CENTER);
    return leftSide;
  }

  public void showOptionsFor(Configuration configuration) {
    optionPane.removeAll();
    bindings.clear();
    if (configuration == null){
      return;
    }

    GridBagConstraintHelper c = new GridBagConstraintHelper(2);



    optionPane.add(label("Name"), c.next());
    optionPane.add(text(configuration.getName(), configuration::setName, bindings), c.next());


    JList<LoggerConf> loggerList = new JList<>();

    JPanel listBtns = new JPanel();
    listBtns.setLayout(new BoxLayout(listBtns, BoxLayout.LINE_AXIS));
    listBtns.add(iconBtn("icons/add.svg", () -> {
      String loggerUri = JOptionPane.showInputDialog(this, "Enter Logger Uri");
      try {
        URI uri = new URI(loggerUri);
        configuration.getLogger().add(new LoggerConf(uri));
        loggerList.setListData(configuration.getLogger().toArray(new LoggerConf[]{}));
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }));

    listBtns.add(iconBtn("icons/remove.svg", () -> {
      if (loggerList.getSelectedValue() != null) {
        configuration.getLogger().remove(loggerList.getSelectedValue());
        loggerList.setListData(configuration.getLogger().toArray(new LoggerConf[]{}));
      }
    }));
    optionPane.add(listBtns, c.next(1, 2));


    loggerList.setCellRenderer(new DefaultListCellRenderer(){
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
          boolean isSelected, boolean cellHasFocus) {
        if (value instanceof LoggerConf){
          value = ((LoggerConf) value).getUri().toString();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
    });

    loggerList.setListData(configuration.getLogger().toArray(new LoggerConf[]{}));

    optionPane.add(loggerList, c.next(8, 2, true));


    optionPane.add(label("Format"), c.next());
    optionPane.add(text(configuration.getFormat().getRegex(), configuration.getFormat()::setRegex, bindings));

    setupActionBtns(c);
    optionPane.doLayout();
    optionPane.revalidate();
  }

  private void setupActionBtns(GridBagConstraintHelper c) {
    optionPane.add(new JPanel(), c.next());
    JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    btns.add(button("Cancel", () -> {
      setVisible(false);
      dispose();
    }));

    btns.add(button("Apply", () -> {
      bindings.apply();
      configurationList.setListData(configurations.toArray(new Configuration[]{}));
    }));

    btns.add(button("Ok", () -> {
      bindings.apply();
      configurationList.setListData(configurations.toArray(new Configuration[]{}));
      this.setVisible(false);
      dispose();
    }));

    optionPane.add(btns, c.next());
  }


}
