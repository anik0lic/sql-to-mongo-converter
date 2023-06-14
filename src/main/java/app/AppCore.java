package app;

import adapter.Adapter;
import adapter.AdapterImplementation;
import database.Database;
import database.MongoDatabaseExec;
import database.settings.Settings;
import database.settings.SettingsImplementation;
import gui.table.TableModel;
import lombok.Getter;
import lombok.Setter;
import parser.Parser;
import parser.ParserImplementation;
import parser.composite.Query;
import validator.Validator;

import java.util.List;

@Getter
@Setter
public class AppCore {
    private Database database;
    private Settings settings;
    private TableModel tableModel;
    private Parser parser;
    private Validator validator;
    private Adapter adapter;

    public AppCore(Validator validator) {
        this.settings = initSettings();
        this.database = new MongoDatabaseExec(this.settings);
        this.tableModel = new TableModel();
        this.parser = new ParserImplementation();
        this.validator = validator;
        this.adapter = new AdapterImplementation();
    }

    private Settings initSettings() {
        Settings settingsImplementation = new SettingsImplementation();
        settingsImplementation.addParameter("mongodb_ip", "134.209.239.154");
        settingsImplementation.addParameter("mongodb_database", "bp_tim12");
        settingsImplementation.addParameter("mongodb_username", "writer");
        settingsImplementation.addParameter("mongodb_password", "wMRJwt79Mh8pnnt5");

        return settingsImplementation;
    }

    public void startParsing(String text) {
        Query query = parser.parsing(text);
        if (validator.validate(query)) {
            readDataFromTable(adapter.getMongoQuery(query));
        }
    }

    public void readDataFromTable(List<String> parameters) {
        tableModel.setRows(this.database.getDataFromTable(parameters));
    }
}
