/*
 * Created by JFormDesigner on Fri Apr 10 17:07:47 CEST 2020
 */

package mlog.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import javax.inject.Singleton;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import lombok.Value;
import mlog.domain.Configuration;
import mlog.ui.commands.AppCommand;
import mlog.ui.commands.AppCommand.ClearBuffer;
import mlog.ui.commands.AppCommand.ExecConfiguration;
import mlog.ui.commands.AppCommand.ShowEditConfigurationDialog;
import mlog.ui.commands.AppCommand.StopCurrentConfiguration;
import mlog.ui.components.FlatSVGIcon;
import mlog.utils.Event;
import mlog.utils.Event0;


@Singleton
public class Toolbar extends JToolBar {


  public Event<AppCommand> OnCommand = new Event<>();
  private final JComboBox<ConfigComboEntry> configSelect;

  public Toolbar() {

    configSelect = new JComboBox<>();
    configSelect.setRenderer(new DefaultListCellRenderer() {
      JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);

      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
          boolean isSelected, boolean cellHasFocus) {
        if (value instanceof ConfigComboEntry) {
          ConfigComboEntry comboVal = (ConfigComboEntry) value;
          if (comboVal.getName() == null) {
            return separator;
          }
          value = comboVal.getName();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
    });
    configSelect.setMaximumSize(new Dimension(150, Integer.MAX_VALUE));

    configSelect.addActionListener(evt -> {
      if (configSelect.getSelectedItem() instanceof ConfigComboEntry){
        ConfigComboEntry e = (ConfigComboEntry) configSelect.getSelectedItem();
        e.OnSelect.invoke();
        if  (e.getConfig() == null){
          configSelect.setSelectedItem(null);
        }
      }
    });


    add(configSelect);

    JButton execute = new JButton(new FlatSVGIcon("icons/execute.svg"));
    execute.addActionListener(e -> {
      if (configSelect.getSelectedItem() instanceof ConfigComboEntry && ((ConfigComboEntry) configSelect.getSelectedItem()).getConfig() != null){
        OnCommand.invoke(new ExecConfiguration(((ConfigComboEntry) configSelect.getSelectedItem()).getConfig()));
      }

    });
    add(execute);

    JButton stop = new JButton(new FlatSVGIcon("icons/suspend.svg"));
    stop.addActionListener(e -> OnCommand.invoke(new StopCurrentConfiguration()));
    add(stop);

    JButton clear = new JButton(new FlatSVGIcon("icons/clear.svg"));
    clear.addActionListener(e -> OnCommand.invoke(new ClearBuffer()));
    add(clear);
  }

  public void setConfigurations(List<Configuration> allConfigurations) {
    configSelect.removeAllItems();

    ConfigComboEntry item = new ConfigComboEntry("Edit Configurations...", null);

    configSelect.addItem(item);
    configSelect.addItem(new ConfigComboEntry(null, null)); //separator
    allConfigurations.forEach(c -> configSelect.addItem(new ConfigComboEntry(c.getName(), c)));


    if (allConfigurations.size() > 0) {
      configSelect.setSelectedIndex(2);
    }
    // we have to do it out of order bc otherwise, addItem selects the item directly and opens the dialog
    item.OnSelect.add(() -> OnCommand.invoke(new ShowEditConfigurationDialog()));
  }

  @Value
  public static class ConfigComboEntry {

    String name;
    Configuration config;

    Event0 OnSelect = new Event0();
  }
}
