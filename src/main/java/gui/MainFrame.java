package gui;

import app.AppCore;
import lombok.Data;
import observer.Subscriber;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Data
public class MainFrame extends JFrame implements Subscriber {
    private static MainFrame instance = null;
    private AppCore appCore;
    private JTable jTable;
    private TextArea textArea;

    private MainFrame(){}

    public static MainFrame getInstance(){
        if (instance==null){
            instance=new MainFrame();
            instance.initialise();
        }
        return instance;
    }

    private void initialise() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new TextArea();
        Font font = new Font(Font.DIALOG, Font.BOLD, 16);
        textArea.setFont(font);

        Button button = new Button("Run");
        button.setFont(new Font(Font.DIALOG, Font.BOLD, 16) );
        button.setBackground(Color.LIGHT_GRAY);
        button.setPreferredSize(new Dimension(150, 40));
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.add(button);

        this.add(textArea, BorderLayout.NORTH);
        this.add(panel, BorderLayout.CENTER);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!textArea.getText().isBlank()){
                    appCore.startParsing(textArea.getText());
                }else{
                    JOptionPane.showMessageDialog(MainFrame.getInstance(), "Text Area is empty", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jTable = new JTable();
        jTable.setPreferredScrollableViewportSize(new Dimension(1000, 400));
        jTable.setFillsViewportHeight(true);
        this.add(new JScrollPane(jTable), BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    public void setAppCore(AppCore appCore) {
        this.appCore = appCore;
        this.jTable.setModel(appCore.getTableModel());
    }
    @Override
    public void update(Object object) {
        JOptionPane.showMessageDialog(this, object, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
