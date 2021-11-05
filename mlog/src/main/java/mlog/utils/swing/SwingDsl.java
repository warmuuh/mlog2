package mlog.utils.swing;

import java.util.List;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import lombok.experimental.UtilityClass;
import mlog.ui.components.FlatSVGIcon;

@UtilityClass
public class SwingDsl {

  private JFrame owner;

  public void setOwner(JFrame newOwner){
    owner = newOwner;
  }
  public int optionsDialog(String text, String... options){
    return JOptionPane.showOptionDialog(owner, text, "confirmation",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]);
  }

  public JLabel label(String value){
    return new JLabel(value);
  }

  public JTextField text(String value, Consumer<String> setter, Bindings bindings){
      JTextField txt = new JTextField(value);
      bindings.add(txt, setter);
      return txt;
  }

  public JComboBox<String> select(String value, Consumer<String> setter, String[] values, Bindings bindings){
    JComboBox<String> combo = new JComboBox<>(values);
    combo.setSelectedItem(value);
    combo.addActionListener(l -> {
      setter.accept((String)combo.getSelectedItem());
    });
    return combo;
  }

  public JButton iconBtn(String icon, Runnable onAction){
    JButton jButton = new JButton(new FlatSVGIcon(icon));
    jButton.addActionListener(evt -> onAction.run());
    return jButton;
  }

  public JButton button(String text, Runnable onAction){
    JButton jButton = new JButton(text);
    jButton.addActionListener(evt -> onAction.run());
    return jButton;
  }

}
