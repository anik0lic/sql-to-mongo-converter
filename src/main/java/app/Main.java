package app;

import gui.MainFrame;
import validator.Validator;
import validator.ValidatorImplementation;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args){
        Validator validator = new ValidatorImplementation();
        AppCore appCore = new AppCore(validator);
        MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.setAppCore(appCore);

        validator.addSubscriber(mainFrame);
    }
}