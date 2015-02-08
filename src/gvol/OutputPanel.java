/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gvol;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/**
 *
 * @author Shawkey
 */
class OutputPanel extends JPanel {
    
    private JTextArea consoleTextArea;
    
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
