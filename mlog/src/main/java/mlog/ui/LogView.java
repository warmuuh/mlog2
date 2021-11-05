package mlog.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import mlog.PlatformUtil;
import mlog.ctrl.rt.MessageBuffer;
import mlog.ctrl.rt.logging.regex.RegexLoggerFormat;
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

  private Map<String, Integer> columnWidths = new HashMap<>();



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

    table.getTableHeader().addMouseListener( new MouseAdapter() {
      public void mouseReleased(MouseEvent arg0)
      {
        for( int i=0;i<table.getColumnModel().getColumnCount();i++ ) {
          var column = table.getColumnModel().getColumn(i);
          columnWidths.put(column.getHeaderValue().toString(), column.getWidth());
        }

      }});

    table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
      @Override
      public void columnAdded(TableColumnModelEvent e) {
        applyColumnWidths();
      }

      @Override
      public void columnRemoved(TableColumnModelEvent e) {
      }

      @Override
      public void columnMoved(TableColumnModelEvent e) {
      }

      @Override
      public void columnMarginChanged(ChangeEvent e) {
      }

      @Override
      public void columnSelectionChanged(ListSelectionEvent e) {
      }
    });


    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
      {
        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        var fields = dataModel.getRowValue(sorter.convertRowIndexToModel(row)).getFields();
        String prio = fields.getOrDefault("prio", fields.getOrDefault("level", "INFO"));
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



  public void startShowing(MessageBuffer buffer, List<String> fields) {
    dataModel = new BatchedDataModel(buffer, fields);
    dataModel.addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
        if (scrollToBottom){
          table.scrollRectToVisible(new Rectangle(0, table.getHeight() - 1, table.getWidth(), 1));
        }
      }
    });

    table.setModel(dataModel);
    applyColumnWidths();
    sorter = new TableRowSorter<>(dataModel);
    table.setRowSorter(sorter);
  }

  private void applyColumnWidths() {
    columnWidths.forEach((cIdx, cWidth) -> {
      for( int i=0;i<table.getColumnModel().getColumnCount();i++ ) {
        var column = table.getColumnModel().getColumn(i);
        if (column.getHeaderValue().toString().equals(cIdx)){
          column.setPreferredWidth(cWidth);
        }
      }
    });
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
