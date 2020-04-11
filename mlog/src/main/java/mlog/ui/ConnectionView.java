package mlog.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import javax.inject.Singleton;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import mlog.ctrl.rt.Channel;

@Singleton
public class ConnectionView extends JPanel {

  private final JList<Channel> channelList;

  public ConnectionView() {
    setLayout(new BorderLayout());
    channelList = new JList<>();
    channelList.setCellRenderer(new DefaultListCellRenderer(){
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
          boolean isSelected, boolean cellHasFocus) {
        if (value instanceof Channel){
          value = ((Channel) value).getName();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
    });
    add(channelList, BorderLayout.CENTER);
  }


  public void showActiveChannels(List<Channel> channel){
    channelList.setListData(channel.toArray(new Channel[]{}));
  }

}
