package database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCursor;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import data.Row;
import database.settings.Settings;
import lombok.Data;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class MongoDatabaseExec implements Database{
    private Settings settings;
    private MongoClient connection;
    private static String ip;
    private static String database;
    private static String username;
    private static String password;

    public MongoDatabaseExec(Settings settings){
        this.settings = settings;
        getParameters();
    }

    private void getParameters(){
        ip = settings.getParameter("mongodb_ip");
        database = settings.getParameter("mongodb_database");
        username = settings.getParameter("mongodb_username");
        password = settings.getParameter("mongodb_password");
    }

    public MongoClient getConnection(){
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
        connection = new MongoClient(new ServerAddress(ip, 27017), Arrays.asList(credential));

        return connection;
    }

    private void closeConnection(){
        try{
            connection.close();
        } finally {
            connection = null;
        }
    }

    @Override
    public List<Row> getDataFromTable(List<String> parameters) {
        List<Row> rows = new ArrayList<>();
        String tip = parameters.get(parameters.size()-1);
        parameters.remove(parameters.size()-1);

        try{
            this.getConnection();

            MongoDatabase database = getConnection().getDatabase("bp_tim12");
            MongoCursor<Document> cursor = null;

            String collection = parameters.get(0);
            parameters.remove(0);
            List<Document> bson = new ArrayList<>();

            for(String s : parameters){
                bson.add(Document.parse(s));
            }

            if(tip.equals("1")){
                cursor = database.getCollection(collection).find(Document.parse(parameters.get(0))).projection(Document.parse(parameters.get(1))).sort(Document.parse(parameters.get(2))).iterator();
            }
            else{
                cursor = database.getCollection(collection).aggregate(bson).iterator();
            }

            List<Document> documents = new ArrayList<>();

            while (cursor.hasNext()){
                Document d = cursor.next();
                if(tip.equals("3"))
                    d = d.get("1", Document.class);
                documents.add(d);
                System.out.println(d.toJson());
            }

            for(Document d : documents){
                Row row = new Row();
                row.setName(collection);

                DocumentResultSetMetaData resultSetMetaData = new DocumentResultSetMetaData(d);

                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++){
                    row.addField(resultSetMetaData.getColumnLabel(i), resultSetMetaData.getString(i));
                }

                rows.add(row);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            this.closeConnection();
        }

        return rows;
    }
}
