package database.settings;

import java.util.HashMap;
import java.util.Map;

public class SettingsImplementation implements Settings{
    private Map<String, String> parameters = new HashMap();

    public String getParameter(String parameter) {
        return  this.parameters.get(parameter);
    }

    public void addParameter(String parameter, String value) {
        this.parameters.put(parameter, value);
    }

}
