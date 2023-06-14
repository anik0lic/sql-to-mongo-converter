package database;

import data.Row;
import org.bson.Document;

import java.util.List;

public interface Database {
    List<Row> getDataFromTable(List<String> parameters);
}
