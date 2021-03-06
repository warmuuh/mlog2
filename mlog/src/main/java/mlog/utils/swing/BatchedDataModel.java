package mlog.utils.swing;

import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import mlog.ctrl.rt.MessageBuffer;
import mlog.ctrl.rt.Message;

public class BatchedDataModel extends AbstractTableModel{

  private final MessageBuffer buffer;
  private final List<String> columnNames;

  public BatchedDataModel(MessageBuffer buffer, List<String> columns) {
    this.buffer = buffer;
    this.columnNames = new LinkedList<>();
    columnNames.add("origin");
    columnNames.addAll(columns);
    buffer.onRowsAdded.add(this::onRowsAdded);

    buffer.onClear.add(this::onClearBuffer);
  }


  @Override
  public int getRowCount() {
    return buffer.getBuffer().size();
  }

  @Override
  public int getColumnCount() {
    return columnNames.size();
  }


  @Override
  public String getColumnName(int column) {
    return columnNames.get(column);
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    Message message = buffer.getBuffer().get(rowIndex);
    if (columnIndex == 0){
      return message.getChannel();
    }

    return message.getFields().get(columnNames.get(columnIndex));
  }

  public Message getRowValue(int rowIndex){
    return buffer.getBuffer().get(rowIndex);
  }

  private synchronized void onRowsAdded(List<Message> rows, Integer overflow) {
    if (overflow > 0) {
      fireTableRowsDeleted(0, overflow);
    }
    fireTableRowsInserted(getRowCount() - 1 - rows.size(), getRowCount() - 1);
  }

  private synchronized void onClearBuffer(Integer rowcount) {
    if (rowcount == 0){
      return;
    }
    fireTableRowsDeleted(0, rowcount -1);
  }
}
