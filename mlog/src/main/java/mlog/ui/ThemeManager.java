package mlog.ui;


import com.formdev.flatlaf.FlatLightLaf;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Singleton
public class ThemeManager {

  @PostConstruct
  public void setup(){
    FlatLightLaf.install();
  }

}
