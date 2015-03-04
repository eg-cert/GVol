package gvol;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class OptionsPanel extends JPanel implements ActionListener {

    private final Option[] options;
    private final JComponent[] components;
    private final JCheckBox[] checkBoxes;
    private final boolean [] visible;
    private final int count;
    
    public OptionsPanel(Option[] options) {
        super();
        this.options = options;
        setBorder(BorderFactory.createTitledBorder("Options"));
        count = options.length;
        checkBoxes = new JCheckBox[count];
        components = new JComponent[count];
        visible = new boolean[count];
        initComponents();
    }

    public void updateVisibleOptions(int[] ind) {
        removeAll();
        GridBagConstraints gc = new GridBagConstraints();
        for(int i=0;i<count;i++) visible[i]=false;
        gc.gridy = -1;
        for (int i = 0; i < ind.length; i++) {
            if (ind[i] >= 1 && ind[i] <= count) {
                gc.gridx = 0;
                gc.gridy = gc.gridy++;
                gc.fill = GridBagConstraints.NONE;
                gc.anchor = GridBagConstraints.WEST;
                gc.weightx = gc.weighty = 1;
                
                add(checkBoxes[ind[i]-1], gc);
                visible[ind[i]-1]=true;
                gc.gridx = 1;
                gc.anchor = GridBagConstraints.WEST;
                components[ind[i]-1].setEnabled(checkBoxes[ind[i]-1].isSelected());
                add(components[ind[i]-1], gc);
            }
        }
        //add code here 
        revalidate();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        JTextField jField;
        MFileChooser fileChooser;

        for (int i = 0; i < count; i++) {
            visible[i] = true;
            checkBoxes[i] = new JCheckBox(options[i].getDesc());
            checkBoxes[i].addActionListener(this);
            gc.gridx = 0;
            gc.gridy = i;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.WEST;
            gc.weightx = gc.weighty = 1;
            add(checkBoxes[i], gc);

            switch (options[i].getValueType()) {
                case STRING:
                    jField = new JTextField(14);
                    components[i] = jField;
                    break;
                case NUMBER:
                    jField = new JTextField(14);
                    jField.addKeyListener(new NumberValidator());
                    jField.setPreferredSize(new Dimension(250, 25));
                    components[i] = jField;
                    break;
                case FILE:
                    fileChooser = new MFileChooser(false);
                    components[i] = fileChooser;
                    break;
                case DIRECTORY:
                    fileChooser = new MFileChooser(true);
                    components[i] = fileChooser;
                    break;
                case NOVALUE:
                    components[i] = new JLabel();
                    break;

            }
            gc.gridx = 1;
            gc.anchor = GridBagConstraints.WEST;
            components[i].setEnabled(false);
            add(components[i], gc);

        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        JCheckBox checkBox = (JCheckBox) ae.getSource();
        for (int i = 0; i < count; i++) {
            if (checkBox == checkBoxes[i]) {

                components[i].setEnabled(checkBox.isSelected());
                break;
            }
        }
    }

    class NumberValidator extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            JTextField textField = (JTextField) e.getSource();
            String text = textField.getText();
            String newText = "";
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) <= '9' && text.charAt(i) >= '0') {
                    newText = newText + text.charAt(i);
                }
            }
            textField.setText(newText);
        }

    }
    
    public String getComand(){
        String cmd = "";
        for(int i=0;i<count;i++){
            if(visible[i] && checkBoxes[i].isSelected()){
                switch(options[i].getValueType()){
                    case STRING:
                    case NUMBER:
                        JTextField jField= (JTextField) components[i];
                        cmd = cmd + " " + options[i].getCmd() + " " + jField.getText();
                        break;
                    case FILE:
                    case DIRECTORY:
                        MFileChooser fileChooser = (MFileChooser) components[i];
                        cmd = cmd + " " + options[i].getCmd() + " " + fileChooser.getSelectedFile();
                        break;
                        
                }
            }
        }
        return cmd;
    }
    
    public boolean hasValidValues(){
        
        for(int i=0;i<count;i++){
            if(visible[i] && checkBoxes[i].isSelected()){
                switch(options[i].getValueType()){
                    case STRING:
                    case NUMBER:
                        JTextField jField= (JTextField) components[i];
                        if(isNullOrWhiteSpace(jField.getText()))
                            return false;
                        break;
                    case FILE:
                    case DIRECTORY:
                        MFileChooser fileChooser = (MFileChooser) components[i];
                        if(isNullOrWhiteSpace(fileChooser.getSelectedFile()))
                            return false;
                        break;
                        
                }
            }
        }
        return true;
    }
    
    private boolean isNullOrWhiteSpace(String str){
        return (str==null || str.isEmpty() || str.trim().isEmpty());
    }
}
