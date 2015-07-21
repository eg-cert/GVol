package dialog;

import database.Option;
import database.DatabaseConn;
import database.OptionValueType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import main.ComboBoxItem;

public class OptionsDialog extends JDialog implements ActionListener {

    private JTable optionTable;
    private JPopupMenu optionPopup;
    private JMenuItem deleteOptionItem;
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
        } else if(source==deleteOptionItem) {
            int row = optionTable.getSelectedRow();
            ComboBoxItem cbi = (ComboBoxItem) optionTable.getModel().getValueAt(row, 0);
            deleteOptionAction(cbi.getID());
        }
    }

    private void initOptionsPanel() {
        Insets insets = this.getInsets();
        optionTable = new JTable(){
            @Override
            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return false;
            }
        };;
        optionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        optionPopup = new JPopupMenu();
        deleteOptionItem = new JMenuItem("Delete Option");
        deleteOptionItem.addActionListener(this);
        optionPopup.add(deleteOptionItem);
        optionTable.addMouseListener(new TableMouseListener(optionTable,optionPopup));
    
        JScrollPane scrollPane = new JScrollPane(optionTable);
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
        optionTable.removeAll();
        String [] columnNames = {"Option","Type","Description"};
        Option [] options = DatabaseConn.getOptions();
        if(options==null || options.length==0) return;
        ComboBoxItem [][] data = new ComboBoxItem[options.length][3];
        for(int i=0;i<options.length;i++) {
            data[i][0] = new ComboBoxItem(options[i].getID(), options[i].getCmd());
            data[i][1] = new ComboBoxItem(0,options[i].getValueType().toString());
            data[i][2] = new ComboBoxItem(0,options[i].getDesc());
        }
        optionTable.setModel(new DefaultTableModel(data,columnNames));
        
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

        Option op = new Option(0,OptionValueType.valueOf((String) typeComboBox.getSelectedItem()), name, desc);
        DatabaseConn.addOption(op);
        updateOptions();
    }

    private void deleteOptionAction(int ID) {
        try {
           
            if(DatabaseConn.optionUsed(ID)){
                JOptionPane.showMessageDialog(this, "Error: this option is used by a plugin.");
            }
            else {
                DatabaseConn.deleteOption(ID);
                updateOptions();
            }
            
        }
        catch(Exception ex){
            System.err.println(ex.getMessage());
        }
    }
    
    private class TableMouseListener extends MouseAdapter{
        JTable table;
        JPopupMenu popup;
        public TableMouseListener(JTable table,JPopupMenu popup){
            this.table = table;
            this.popup = popup;
        }
                
        @Override 
        public void mouseReleased(MouseEvent e) {
            int r = table.rowAtPoint(e.getPoint());
            if (r >= 0 && r < table.getRowCount()) {
                table.setRowSelectionInterval(r, r);
                if(SwingUtilities.isRightMouseButton( e )){
                    popup.show(table, e.getX(), e.getY());
                }
            } 
        }
        
    }
}
