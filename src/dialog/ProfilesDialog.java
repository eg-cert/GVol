package dialog;

import database.DatabaseConn;
import database.Profile;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
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

public class ProfilesDialog extends JDialog implements ActionListener{
    
    final private JPanel cmdPanel;
    final private JLabel volLabel;
    final private JTextField volTextField;
    final private JButton updateCmdButton;
    
    private JTable profileTable;
    private JPopupMenu profilePopup;
    private JMenuItem deleteProfileItem;
    
    final private JPanel addProfilePanel;
    final private JLabel nameLabel;
    final private JLabel descLabel;
    final private JTextField nameTextField;
    final private JTextField descTextField;
    final private JButton addButton;
    
    final private JButton doneButton;
   
    
    public ProfilesDialog(JFrame parent){
        super(parent, true);
        setSize(new Dimension(620, 480));
        setLayout(null);
        setLocationRelativeTo(parent);
        setTitle("Volatility Command and Profiles");
        
        
        //vol command panel and components
        cmdPanel = new JPanel();
        volLabel = new JLabel();
        volTextField = new JTextField(15);
        updateCmdButton = new JButton();
        
        //add new profile panel and components
        addProfilePanel = new JPanel();
        nameLabel = new JLabel();
        descLabel = new JLabel();
        nameTextField = new JTextField(15);
        descTextField = new JTextField(15);
        addButton = new JButton();
        
        initCmdPanel();
        initProfilesTable();
        initAddProfilePanel();
        
        doneButton = new JButton("Done");
        doneButton.addActionListener(this);  
        doneButton.setBounds(getInsets().left+500, getInsets().top+385, 80, 25);
        add(doneButton);
    }

    private void initCmdPanel() {
        cmdPanel.setLayout(new FlowLayout());
        cmdPanel.setBorder(BorderFactory.createTitledBorder("Volatility Command"));
        Insets insets = this.getInsets();
        cmdPanel.setBounds(insets.left + 5, insets.top + 5, 590, 60);
        add(cmdPanel);
        
        volLabel.setText("Volatility Command: ");
        volTextField.setText(DatabaseConn.getVolCommand());
        updateCmdButton.setText("Apply Changes");
        updateCmdButton.addActionListener(this);
        cmdPanel.add(volLabel);
        cmdPanel.add(volTextField);
        cmdPanel.add(updateCmdButton);
    }
    
    private void initProfilesTable() {
        Insets insets = this.getInsets();
        profileTable = new JTable(){
            @Override
            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return false;
            }
        };;
        profileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        profilePopup = new JPopupMenu();
        deleteProfileItem = new JMenuItem("Remove Profile");
        deleteProfileItem.addActionListener(this);
        profilePopup.add(deleteProfileItem);
        profileTable.addMouseListener(new TableMouseListener(profileTable,profilePopup));
    
        JScrollPane scrollPane= new JScrollPane(profileTable);
        scrollPane.setBounds(insets.left+5,insets.top+65,590,250);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Profiles"));
        add(scrollPane);
        
        updateProfiles();
    }
    
    private void initAddProfilePanel() {
        addProfilePanel.setLayout(new FlowLayout());
        addProfilePanel.setBorder(BorderFactory.createTitledBorder("Add new profile"));
        Insets insets = this.getInsets();
        addProfilePanel.setBounds(insets.left + 5, insets.top + 320, 590, 60);
        add(addProfilePanel);
        
        nameLabel.setText("Profile: ");
        descLabel.setText("Description: ");
        addButton.setText("Add Profile");
        addProfilePanel.add(nameLabel);
        addProfilePanel.add(nameTextField);
        addProfilePanel.add(descLabel);
        addProfilePanel.add(descTextField);
        
        addProfilePanel.add(addButton);
        addButton.addActionListener(this);
        
    }
    
    private void updateProfiles() {
        
        profileTable.removeAll();
        String [] columnNames = {"Profile","Description"};
        Profile [] profiles = DatabaseConn.getProfiles();
        if(profiles.length==0) return;
        ComboBoxItem [][] data = new ComboBoxItem[profiles.length][2];
        for(int i=0;i<profiles.length;i++) {
            data[i][0] = new ComboBoxItem(profiles[i].getID(), profiles[i].getName());
            data[i][1] = new ComboBoxItem(0,profiles[i].getDescription());
        }
        profileTable.setModel(new DefaultTableModel(data,columnNames));
        
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if(source == updateCmdButton){
            updateCmdButtonAction();
        }
        else if(source==doneButton){
            doneButtonAction();
        }
        else if(source == addButton){
            addButtonAction();
        }
        else if(source == deleteProfileItem) {
            int row = profileTable.getSelectedRow();
            ComboBoxItem cbi = (ComboBoxItem) profileTable.getModel().getValueAt(row, 0);
            deleteButtonAction(cbi.getID());
        }
    }
    
    @Override
    public void setVisible(boolean visible){
        super.setVisible(visible);
        if(visible){
            volTextField.setText(DatabaseConn.getVolCommand());
            nameTextField.setText("");
            descTextField.setText("");
        }
    }

    private void updateCmdButtonAction() {
        String newCmd = volTextField.getText();
        if(newCmd == null || newCmd.isEmpty() || newCmd.trim().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Invalid Command");
        }
        else DatabaseConn.setVolCommand(newCmd);
    }

    private void doneButtonAction() {
        setVisible(false);
    }

    private void addButtonAction() {
        String name = nameTextField.getText();
        String desc = descTextField.getText();
        
        if(name == null || name.isEmpty() || name.trim().isEmpty()
            || desc == null || desc.isEmpty() || desc.trim().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Enter Values for both profile and description.");
            return;
        }
        if(DatabaseConn.profileExists(name)){
            JOptionPane.showMessageDialog(this, "Profile already exists.");
            return;
        }
        Profile p = new Profile(name.trim(),desc.trim());
        DatabaseConn.addProfile(p);
        nameTextField.setText("");
        descTextField.setText("");
        updateProfiles();
        
    }

    private void deleteButtonAction(int ID) {
        try {
           
            DatabaseConn.deleteProfile(ID);
            updateProfiles();
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
