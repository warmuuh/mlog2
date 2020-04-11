package mlog.ui;

import java.awt.BorderLayout;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import mlog.ctrl.rt.MessageBuffer;
import mlog.domain.LoggerFormat;
import mlog.domain.Message;
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
      Message selectedMessage = dataModel.getRowValue(table.getSelectedRow());
      logDetailView.showDetails(selectedMessage);
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
