package mlog.utils.swing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import lombok.Getter;


public class GridBagConstraintHelper {

  private final int columns;
  private int curIdx = 0;
  @Getter
  private GridBagConstraints constraints;

  public GridBagConstraintHelper(int columns) {
    this.columns = columns;
    constraints = new GridBagConstraints();
    constraints.ipadx = 30;
    constraints.ipady = 10;
    constraints.insets = new Insets(5,5,5,5);
  }

  public GridBagConstraints next(){
    return next(1,1);
  }


  public GridBagConstraints next(boolean fill){
    return next(1,1, fill);
  }

  public GridBagConstraints next(int rowSpan, int colSpan){
    return next(rowSpan, colSpan, false);
  }
  public GridBagConstraints next(int rowSpan, int colSpan, boolean fill){
    constraints.weightx = (curIdx%2==0) ? 0 : 1;
    constraints.weighty = fill ? 1 : 0;
    constraints.gridx = curIdx % columns;
    constraints.gridy = curIdx / columns;
    constraints.gridwidth = colSpan;
    constraints.gridheight = rowSpan;
    constraints.fill = fill ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
    curIdx+= rowSpan * colSpan;
    return constraints;
  }

}
