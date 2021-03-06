package mlog.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import mlog.ctrl.rt.MessageBuffer;
import mlog.domain.LoggerFormat;
import mlog.ctrl.rt.Message;
import mlog.ui.components.FlatSVGIcon;
import mlog.utils.swing.BatchedDataModel;
import mlog.utils.swing.SqlRowFilter;

@Singleton
public class LogView extends JPanel {



  private final JTable table;
  private final JTextField filterExpr;
  private LogDetailView logDetailView;
  private BatchedDataModel dataModel;
  private TableRowSorter<BatchedDataModel> sorter;
  private boolean scrollToBottom = false;

  public LogView(LogDetailView logDetailView) {
    this.logDetailView = logDetailView;

    setLayout(new BorderLayout(5,5));
    JToolBar toolBar = new JToolBar();
    toolBar.add(new JLabel("Filter:"));
    filterExpr = new JTextField();
    filterExpr.addActionListener(e -> toggleFilter());
    toolBar.add(filterExpr);




    JButton findBtn = new JButton(new FlatSVGIcon("icons/find.svg"));
    findBtn.addActionListener(e -> toggleFilter());
    toolBar.add(findBtn);

    JButton clearFilter = new JButton(new FlatSVGIcon("icons/clear.svg"));
    clearFilter.addActionListener(e -> clearFilter());
    toolBar.add(clearFilter);

    add(toolBar, BorderLayout.PAGE_START);

    table = new JTable();

    table.getSelectionModel().addListSelectionListener(l -> {
      if (table.getSelectedRow() < 0)
        return;

      Message selectedMessage = dataModel.getRowValue(table.convertRowIndexToModel(table.getSelectedRow()));
      logDetailView.showDetails(selectedMessage);
    });


    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
      {
        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String prio = dataModel.getRowValue(sorter.convertRowIndexToModel(row)).getFields().getOrDefault("prio", "INFO");
        if ("WARN".equals(prio)) {
          c.setForeground(Color.ORANGE);
        } else if ("ERROR".equals(prio)) {
          c.setForeground(Color.RED);
        } else {
          c.setForeground(Color.WHITE);
        }

        return c;
      }
    });

    add(new JScrollPane(table), BorderLayout.CENTER);
  }



  public void startShowing(MessageBuffer buffer, LoggerFormat format) {
    dataModel = new BatchedDataModel(buffer, format.getFields());
    dataModel.addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
        if (scrollToBottom){
          table.scrollRectToVisible(new Rectangle(0, table.getHeight() - 1, table.getWidth(), 1));
        }
      }
    });

    table.setModel(dataModel);
    sorter = new TableRowSorter<>(dataModel);
    table.setRowSorter(sorter);
  }


  private void clearFilter() {
    sorter.setRowFilter(null);
  }

  public void toggleFilter(){
    if (filterExpr.getText().isEmpty()){
      sorter.setRowFilter(null);
    } else {
//      sorter.setRowFilter(RowFilter.regexFilter(filterExpr.getText()));
      sorter.setRowFilter(new SqlRowFilter(table, filterExpr.getText()));
    }
  }


  public void setScrollToBottom(boolean scrollToBottom) {
    this.scrollToBottom = scrollToBottom;
  }

}
