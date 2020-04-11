/*
 * Created by JFormDesigner on Fri Apr 10 17:07:47 CEST 2020
 */

package mlog.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.io.ObjectInputFilter.Config;
import java.util.List;
import javax.inject.Singleton;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import mlog.domain.Configuration;
import mlog.ui.commands.AppCommand;
import mlog.ui.commands.AppCommand.ExecConfiguration;
import mlog.ui.commands.AppCommand.StopCurrentConfiguration;
import mlog.ui.components.FlatSVGIcon;
import mlog.utils.Event;


@Singleton
public class Toolbar extends JToolBar {


  public Event<AppCommand> OnCommand = new Event<>();
  private final JComboBox<Configuration> configSelect;

  public Toolbar() {

    configSelect = new JComboBox<>();
    configSelect.setRenderer(new DefaultListCellRenderer(){
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
          boolean isSelected, boolean cellHasFocus) {
        if (value instanceof Configuration) {
          value = ((Configuration) value).getName();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
    });
    configSelect.setMaximumSize(new Dimension(150, Integer.MAX_VALUE));
    add(configSelect);

    JButton execute = new JButton(new FlatSVGIcon("icons/execute.svg"));
    execute.addActionListener(e -> OnCommand.invoke(new ExecConfiguration(
        (Configuration) configSelect.getSelectedItem())));
    add(execute);

    JButton stop = new JButton(new FlatSVGIcon("icons/suspend.svg"));
    stop.addActionListener(e -> OnCommand.invoke(new StopCurrentConfiguration()));
    add(stop);
  }

  public void setConfigurations(List<Configuration> allConfigurations) {
    allConfigurations.forEach(configSelect::addItem);
    if (allConfigurations.size() > 0)
      configSelect.setSelectedIndex(0);
  }
}
