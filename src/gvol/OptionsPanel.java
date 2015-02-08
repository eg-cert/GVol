/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gvol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Shawkey
 */
class OptionsPanel extends JPanel {

    private final Option [] options;
    private final JComponent [] components;
    private final JCheckBox [] checkBoxes;
    private final int count;
    OptionsPanel(Option[] options) {
        super();
        this.options = options;
        setBorder(BorderFactory.createTitledBorder("Options"));
        count = options.length;
        checkBoxes = new JCheckBox[count];
        components = new JComponent[count];
        
        initComponents();
    }
    
    public void updateVisibleOptions(int [] ind){
        
    }

    private void initComponents() {
        for(int i=0;i<count;i++){
            checkBoxes[i] = new JCheckBox(options[i].getDesc());
            switch(options[i].getValueType()){
                case STRING:
                    JTextField jField = new JTextField();
                    
                    components[i] = jField;
                    break;
                case NUMBER:
                    components[i] = new JTextField();
                    break;
                case FILE:
                    components[i] = new JFileChooser();
                    break;
                case DIRECTORY:
                    components[i] = new JFileChooser();
                    break;
                case NOVALUE:
                    components[i] = new JLabel();
                    break;
                    
            }
            
        }
    }
    
    
}
