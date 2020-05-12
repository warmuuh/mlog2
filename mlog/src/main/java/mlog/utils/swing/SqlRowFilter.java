package mlog.utils.swing;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.support.FunctionalSimpleAttribute;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.googlecode.cqengine.query.parser.common.ParseResult;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.RowFilter;

public class SqlRowFilter extends RowFilter<BatchedDataModel, Integer> {

  private final JTable table;
  private final Query<Entry> query;

  public SqlRowFilter(JTable table, String filter) {
    this.table = table;

    Map<String, Attribute<Entry, String>> attributes = new HashMap<>();
    for(int i = 0; i < table.getColumnCount(); ++i){
      String columnName = table.getColumnName(i);
      attributes.put(columnName, getAttributeGetter(columnName));
    }

    SQLParser<Entry> parser = SQLParser.forPojoWithAttributes(Entry.class, attributes);
    ParseResult<Entry> parsedResult = parser.parse("SELECT * from data where " + filter);
    this.query = parsedResult.getQuery();
  }

  @Override
  public boolean include(Entry<? extends BatchedDataModel, ? extends Integer> entry) {
    return query.matches(entry, new QueryOptions());
  }

  private FunctionalSimpleAttribute<Entry, String> getAttributeGetter(
      String attributeName) {
    return new FunctionalSimpleAttribute<Entry, String>(Entry.class, String.class, attributeName,
        obj -> {
          for(int i = 0; i < table.getColumnCount(); ++i){

            if (table.getColumnName(i).equals(attributeName)){
              return obj.getStringValue(table.convertColumnIndexToModel(i));
            }
          }
          return null;
        });
  }
}
