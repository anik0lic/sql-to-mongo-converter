package adapter;

import org.bson.Document;
import parser.composite.Query;

import java.util.List;

public interface Adapter {
    List<String> getMongoQuery(Query query);

}
