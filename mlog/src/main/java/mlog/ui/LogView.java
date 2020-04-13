package mlog.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import mlog.ctrl.rt.MessageBuffer;
import mlog.domain.LoggerFormat;
import mlog.ctrl.rt.Message;
import mlog.ui.components.FlatSVGIcon;
import mlog.utils.swing.BatchedDataModel;

@Singleton
public class LogView extends JPanel {



  private final JTable table;
  private final JTextField filterExpr;
  private LogDetailView logDetailView;
  private BatchedDataModel dataModel;
  private TableRowSorter<BatchedDataModel> sorter;

  public LogView(LogDetailView logDetailView) {
    this.logDetailView = logDetailView;

    setLayout(new BorderLayout(5,5));
    JToolBar toolBar = new JToolBar();
    toolBar.add(new JLabel("Filter:"));
    filterExpr = new JTextField();
    toolBar.add(filterExpr);
    JButton findBtn = new JButton(new FlatSVGIcon("icons/find.svg"));
    findBtn.addActionListener(e -> toggleFilter());
    toolBar.add(findBtn);
    add(toolBar, BorderLayout.PAGE_START);

    table = new JTable();

    table.getSelectionModel().addListSelectionListener(l -> {
      if (table.getSelectedRow() < 0)
        return;

      Message selectedMessage = dataModel.getRowValue(table.getSelectedRow());
      logDetailView.showDetails(selectedMessage);
    });


    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
      {
        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String prio = dataModel.getRowValue(row).getFields().getOrDefault("prio", "INFO");
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
    table.setModel(dataModel);
    sorter = new TableRowSorter<>(dataModel);
    table.setRowSorter(sorter);
  }

  public void toggleFilter(){
    if (filterExpr.getText().isEmpty()){
      sorter.setRowFilter(null);
    } else {
      sorter.setRowFilter(RowFilter.regexFilter(filterExpr.getText()));
    }
  }

}
