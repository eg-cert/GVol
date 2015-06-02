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

    private JComponent[] components;
    private JCheckBox[] checkBoxes;
    private Option [] options;
    
    public OptionsPanel() {
        super();

        setBorder(BorderFactory.createTitledBorder("Options"));
        setLayout(new GridBagLayout());

    }

    public void updateVisibleOptions(int pluginID) {
        removeAll();
        GridBagConstraints gc = new GridBagConstraints();
        options = DatabaseConn.getPluginOptions(pluginID);
        checkBoxes = new JCheckBox[options.length];
        components = new JComponent[options.length];
        gc.gridy = -1;
        for (int i = 0; i < options.length; i++) {
            gc.gridx = 0;
            gc.gridy = gc.gridy++;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.WEST;
            gc.weightx = gc.weighty = 1;

            initComponents(i);
            add(checkBoxes[i], gc);
            gc.gridx = 1;
            gc.anchor = GridBagConstraints.WEST;
            components[i].setEnabled(false);
            add(components[i], gc);

        }
        //add code here 
        revalidate();
    }

    private void initComponents(int ind) {

        GridBagConstraints gc = new GridBagConstraints();
        JTextField jField;
        MFileChooser fileChooser;

        checkBoxes[ind] = new JCheckBox(options[ind].getDesc());
        checkBoxes[ind].addActionListener(this);

        switch (options[ind].getValueType()) {
            case STRING:
                jField = new JTextField(14);
                components[ind] = jField;
                break;
            case NUMBER:
                jField = new JTextField(14);
                jField.addKeyListener(new NumberValidator());
                jField.setPreferredSize(new Dimension(250, 25));
                components[ind] = jField;
                break;
            case FILE:
                fileChooser = new MFileChooser(false);
                components[ind] = fileChooser;
                break;
            case DIRECTORY:
                fileChooser = new MFileChooser(true);
                components[ind] = fileChooser;
                break;
            case NOVALUE:
                components[ind] = new JLabel();
                break;

        }
        components[ind].setEnabled(false);

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        JCheckBox checkBox = (JCheckBox) ae.getSource();
        for (int i = 0; i < checkBoxes.length; i++) {
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

    public String getCommand() {
        String cmd = "";
        for (int i = 0; i < checkBoxes.length; i++) {
            if (checkBoxes[i].isSelected()) {
                switch (options[i].getValueType()) {
                    case STRING:
                    case NUMBER:
                        JTextField jField = (JTextField) components[i];
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

    public boolean hasValidValues() {

        for (int i = 0; i < options.length; i++) {
            if (checkBoxes[i].isSelected()) {
                switch (options[i].getValueType()) {
                    case STRING:
                    case NUMBER:
                        JTextField jField = (JTextField) components[i];
                        if (isNullOrWhiteSpace(jField.getText())) {
                            return false;
                        }
                        break;
                    case FILE:
                    case DIRECTORY:
                        MFileChooser fileChooser = (MFileChooser) components[i];
                        if (isNullOrWhiteSpace(fileChooser.getSelectedFile())) {
                            return false;
                        }
                        break;

                }
            }
        }
        return true;
    }

    private boolean isNullOrWhiteSpace(String str) {
        return (str == null || str.isEmpty() || str.trim().isEmpty());
    }
}
