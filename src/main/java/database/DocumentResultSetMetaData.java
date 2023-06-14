package database;

import org.bson.Document;
import java.sql.SQLException;

public class DocumentResultSetMetaData {
    private Document document;

    public DocumentResultSetMetaData(Document document) {
        this.document = document;
    }

    public int getColumnCount() throws SQLException {
        return document.size();
    }

    public String getColumnLabel(int column) throws SQLException {
        String[] fieldNames = document.keySet().toArray(new String[0]);
        if (column < 1 || column > fieldNames.length) {
            throw new SQLException("Invalid column index: " + column);
        }
        return fieldNames[column - 1];
    }

    public String getString(int column) throws SQLException {
        String columnName = getColumnLabel(column);
        Object value = document.get(columnName);
        return (value != null) ? value.toString() : null;
    }
}
