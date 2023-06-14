package database.settings;

public interface Settings {
    String getParameter(String parameter);
    void addParameter(String parameter, String value);
}
