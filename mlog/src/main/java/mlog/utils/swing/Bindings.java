package mlog.utils.swing;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.JTextField;

public class Bindings {

  private Map<JTextField, Consumer<String>> setters = new HashMap<>();

  public void add(JTextField field, Consumer<String> setter){
    setters.put(field, setter);
  }
  public void clear(){
    setters.clear();
  }

  public void apply(){
    setters.forEach((field, setter) -> setter.accept(field.getText()));
  }


}
