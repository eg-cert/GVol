package gvol;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class OptionsDialog extends JDialog implements ActionListener {

    final private JPanel optionsPanel;

    final private JPanel addOptionPanel;
    final private JLabel nameLabel;
    final private JTextField nameTextField;
    final private JLabel typeLabel;
    final private JComboBox typeComboBox;
    final private JLabel descLabel;
    final private JTextField descTextField;
    final private JButton addButton;

    final private JButton doneButton;

    public OptionsDialog(JFrame parent) {
        super(parent, true);
        setSize(new Dimension(620, 480));
        setLayout(null);
        setLocationRelativeTo(parent);
        setTitle("Volatility Options");

        optionsPanel = new JPanel();
        addOptionPanel = new JPanel();
        nameLabel = new JLabel();
        nameTextField = new JTextField(13);
        typeLabel = new JLabel();
        typeComboBox = new JComboBox();
        descLabel = new JLabel();
        descTextField = new JTextField(13);
        addButton = new JButton();

        initOptionsPanel();
        initAddOptionPanel();

        doneButton = new JButton("Done");
        doneButton.addActionListener(this);
        doneButton.setBounds(getInsets().left + 500, getInsets().top + 385, 80, 25);
        add(doneButton);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if (source == doneButton) {
            doneButtonAction();
        } else if (source == addButton) {
            addButtonAction();
        } else {
            deleteButtonAction(source);
        }
    }

    private void initOptionsPanel() {
        Insets insets = this.getInsets();
        JScrollPane scrollPane = new JScrollPane(optionsPanel);
        scrollPane.setBounds(insets.left + 5, insets.top + 5, 590, 270);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Options"));
        add(scrollPane);

        updateOptions();
    }

    private void initAddOptionPanel() {
        addOptionPanel.setLayout(new FlowLayout());
        addOptionPanel.setBorder(BorderFactory.createTitledBorder("Add new option"));
        Insets insets = this.getInsets();
        addOptionPanel.setBounds(insets.left + 5, insets.top + 280, 590, 90);
        add(addOptionPanel);

        nameLabel.setText("Option: ");
        typeLabel.setText("Type: ");
        descLabel.setText("Description: ");

        for (OptionValueType ovt : OptionValueType.values()) {
            typeComboBox.addItem(ovt.name());
        }

        addButton.setText("Add");
        addButton.addActionListener(this);

        addOptionPanel.add(nameLabel);
        addOptionPanel.add(nameTextField);
        addOptionPanel.add(typeLabel);
        addOptionPanel.add(typeComboBox);
        addOptionPanel.add(descLabel);
        addOptionPanel.add(descTextField);
        addOptionPanel.add(addButton);
    }

    private void updateOptions() {
        optionsPanel.removeAll();

        Option[] options = DatabaseConn.getOptions();

        optionsPanel.setLayout(new GridLayout(Math.max(9, options.length), 4, 1, 1));

        for (int i = 0; i < options.length; i++) {

            optionsPanel.add(new JLabel(options[i].getCmd()));
            optionsPanel.add(new JLabel(options[i].getValueType().toString()));
            optionsPanel.add(new JLabel(options[i].getDesc()));
            JButton button = new JButton("Delete");
            button.addActionListener(this);
            button.setActionCommand(((Integer) options[i].getID()).toString());
            optionsPanel.add(button);
        }

        for (int i = 0; i < (9 - options.length) * 4; i++) {
            optionsPanel.add(new JLabel(""));
        }
        revalidate();
        repaint();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            nameTextField.setText("");
            descTextField.setText("");
            typeComboBox.setSelectedIndex(0);
        }
    }

    private void doneButtonAction() {
        setVisible(false);
    }

    private void addButtonAction() {
        
        String name = nameTextField.getText();
        String desc = descTextField.getText();

        if (name == null || name.isEmpty() || name.trim().isEmpty()
                || desc == null || desc.isEmpty() || desc.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Values for both option and description.");
            return;
        }

        Option op = new Option(OptionValueType.valueOf((String) typeComboBox.getSelectedItem()), name, desc);
        DatabaseConn.addOption(op);
        updateOptions();
    }

    private void deleteButtonAction(Object source) {
        try {
            JButton but = (JButton) source;
            int ID = Integer.parseInt(but.getActionCommand());
            DatabaseConn.deleteOption(ID);
            updateOptions();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}
