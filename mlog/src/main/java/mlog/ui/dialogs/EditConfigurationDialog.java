package mlog.ui.dialogs;

import static mlog.utils.swing.SwingDsl.*;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import lombok.SneakyThrows;
import mlog.domain.Configuration;
import mlog.domain.LoggerConf;
import mlog.plugin.LogParserFactory;
import mlog.utils.ObjectUtils;
import mlog.utils.swing.Bindings;
import mlog.utils.swing.GridBagConstraintHelper;
import org.apache.commons.lang3.StringUtils;

public class EditConfigurationDialog extends JDialog {

  private JList<Configuration> configurationList;
  private final JPanel optionPane;
  private final Bindings bindings = new Bindings();
  private List<Configuration> configurations;

  public EditConfigurationDialog(List<Configuration> configurations, List<LogParserFactory> logparsers, Frame owner) {
    super(owner, "Edit Configurations...", true);
    this.configurations = configurations;
    FlatDarkLaf.install();

    getContentPane().setLayout(new BorderLayout(5, 5));
    JPanel leftSide = createConfigListView(configurations, logparsers);

    optionPane = new JPanel();
    optionPane.setLayout(new GridBagLayout());
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide, optionPane);
    split.setDividerLocation(200);
    getContentPane().add(split);
    pack();
    setSize(new Dimension(1000, 500));
  }


  private JPanel createConfigListView(List<Configuration> configurations,
      List<LogParserFactory> logparsers) {
    JPanel leftSide = new JPanel();
    leftSide.setLayout(new BorderLayout());
    JToolBar toolBar = new JToolBar();
    toolBar.add(iconBtn("icons/add.svg", () -> {
      String name = JOptionPane.showInputDialog(this, "Add new Configuration...");
      if(StringUtils.isBlank(name))
        return;
      Configuration configuration = new Configuration(UUID.randomUUID().toString(), name,
          new LinkedList<>(), "Regex", "(?<message>.*)", "");
      configurations.add(configuration);
      configurationList.setListData(configurations.toArray(new Configuration[]{}));
      configurationList.setSelectedValue(configuration, true);
      showOptionsFor(configuration, logparsers);
    }));

    toolBar.add(iconBtn("icons/copy.svg", () -> {
      var selectedConfig = configurationList.getSelectedValue();
      if (selectedConfig == null) {
        return;
      }
      var newCopy = ObjectUtils.deepClone(selectedConfig);
      newCopy.setId(UUID.randomUUID().toString());
      newCopy.setName(newCopy.getName() + " (copy)");
      configurations.add(newCopy);
      configurationList.setListData(configurations.toArray(new Configuration[]{}));
      configurationList.setSelectedValue(newCopy, true);
      showOptionsFor(newCopy, logparsers);
    }));


    toolBar.add(iconBtn("icons/remove.svg", () -> {
      if (configurationList.getSelectedValue() != null){
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Remove configuration?")){
          configurations.remove(configurationList.getSelectedValue());
          configurationList.setSelectedValue(null, true);
          configurationList.setListData(configurations.toArray(new Configuration[]{}));
        }
      }
    }));

    leftSide.add(toolBar, BorderLayout.PAGE_START);
    configurationList = new JList<>();
    configurationList.setListData(configurations.toArray(new Configuration[]{}));
    configurationList.getSelectionModel().addListSelectionListener(evt -> {
      showOptionsFor(configurationList.getSelectedValue(), logparsers);

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

  public void selectConfiguration(Configuration configuration) {
    configurationList.setSelectedValue(configuration, true);
  }

  private void showOptionsFor(Configuration configuration,
      List<LogParserFactory> logparsers) {
    optionPane.removeAll();
    bindings.clear();
    if (configuration == null){
      optionPane.doLayout();
      optionPane.revalidate();
      optionPane.repaint();
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
      if(StringUtils.isBlank(loggerUri))
        return;

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

    loggerList.addMouseListener(new MouseAdapter() {
      @SneakyThrows
      public void mouseClicked(MouseEvent evt) {
        JList list = (JList)evt.getSource();
        if (evt.getClickCount() == 2) {
          // Double-click detected
          int index = list.locationToIndex(evt.getPoint());
          if (index >= 0){
            LoggerConf val = configuration.getLogger().get(index);
            String loggerUri = JOptionPane.showInputDialog(EditConfigurationDialog.this, "Enter Logger Uri", val.getUri().toString());
            if(StringUtils.isBlank(loggerUri))
              return;

            val.setUri(new URI(loggerUri));

          }
        }
      }
    });



    optionPane.add(loggerList, c.next(8, 2, true));

    optionPane.add(label("LogType"), c.next());
    var values = logparsers.stream().map(LogParserFactory::getLogFormatName).collect(Collectors.toList());
    optionPane.add(select(configuration.getLogType(), configuration::setLogType, values.toArray(new String[]{}), bindings), c.next());

    optionPane.add(label("LogConfiguration"), c.next());
    optionPane.add(text(configuration.getLogTypeConfig(), configuration::setLogTypeConfig, bindings), c.next());

    optionPane.add(label("Additional config"), c.next());
    optionPane.add(text(configuration.getLogTypeAdditionalConfig(), configuration::setLogTypeAdditionalConfig, bindings), c.next());

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
      var selectedValue = configurationList.getSelectedValue();
      configurationList.setListData(configurations.toArray(new Configuration[]{}));
      configurationList.setSelectedValue(selectedValue, true);
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
