package gvol;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


class OutputPanel extends JPanel {
    
    private final JTextArea consoleTextArea;
    
    public OutputPanel() {
        
        super();
        setBorder(BorderFactory.createTitledBorder("Console Output"));
        
        consoleTextArea = new JTextArea();
        setLayout(new BorderLayout());
        consoleTextArea.setLineWrap(true);
        consoleTextArea.setEditable(false);
        
        add(new JScrollPane(consoleTextArea),BorderLayout.CENTER);
        
    }
    
    public void appendText(String txt){
        consoleTextArea.append(txt);
    }
    
    public void clearText(){
        consoleTextArea.setText("");
    }
    
}
