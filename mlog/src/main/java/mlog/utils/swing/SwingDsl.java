package mlog.utils.swing;

import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import lombok.experimental.UtilityClass;
import mlog.ui.components.FlatSVGIcon;

@UtilityClass
public class SwingDsl {


  public JLabel label(String value){
    return new JLabel(value);
  }

  public JTextField text(String value, Consumer<String> setter, Bindings bindings){
      JTextField txt = new JTextField(value);
      bindings.add(txt, setter);
      return txt;
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
