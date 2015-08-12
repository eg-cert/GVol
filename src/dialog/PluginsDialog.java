
package dialog;

import database.*;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import main.ComboBoxItem;

public class PluginsDialog extends JDialog implements ActionListener{
    
    
    final private JPanel addPluginPanel;
    private JTable pluginTable;
    private JPopupMenu pluginPopup;
    private JMenuItem deletePluginItem;
    
    final private JLabel pluginNameLabel;
    final private JTextField pluginNameTextField;
    final private JLabel descLabel;
    final private JTextField descTextField;
    final private JButton addPluginButton;
    
    private JTable optionTable;
    private JPopupMenu optionPopup;
    private JMenuItem removeOptionItem;
    
    final private JPanel addOptionPanel;
    final private JLabel addOptionLabel;
    final private JComboBox optionsComboBox;
    final private JButton addOptionButton;
    final private JButton doneButton;
    
    private int selectedPluginID=0;
    
    public PluginsDialog(JFrame parent){
        super(parent, true);
        setSize(new Dimension(620, 580));
        setLayout(null);
        setLocationRelativeTo(parent);

        addPluginPanel = new JPanel();
        pluginNameLabel=new JLabel("Plugin Name:");
        descLabel = new JLabel("Description:");
        pluginNameTextField= new JTextField(12);
        descTextField = new JTextField(15);
        
        addPluginButton= new JButton();
        
        addOptionPanel = new JPanel();
        addOptionLabel = new JLabel("Select Option");
        optionsComboBox = new JComboBox();
        addOptionButton = new JButton("Add");
        doneButton = new JButton();
        
        
        initPluginsTable();
        initAddPluginPanel();
        initOptionsTable();
        initAddOptionPanel();
        
        doneButton.setText("Done");
        doneButton.addActionListener(this);
        doneButton.setBounds(getInsets().left + 500, getInsets().top + 520, 80, 25);
        add(doneButton);
    }

    private void initPluginsTable() {
        Insets insets = this.getInsets();
        pluginTable = new JTable(){
            @Override
            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return false;
            }
            
            @Override
            public String getToolTipText(MouseEvent e) {
                try {
                    String tip = null;
                    java.awt.Point p = e.getPoint();
                    int row = rowAtPoint(p);
                    ComboBoxItem cbi = (ComboBoxItem) pluginTable.getModel().getValueAt(row, 0);
                    if (cbi == null) {
                        return null;
                    }
                    Plugin plugin = DatabaseConn.getPlugin(cbi.getID());
                    tip = "<html>" + plugin.getName() + "<br>" + ((plugin.getDesc()==null)?"":plugin.getDesc()) + "</html>";
                    return tip;
                } catch (Exception ex) {
                    return null;
                }
            }

        };
        pluginTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pluginTable.getSelectionModel().addListSelectionListener(new TableSelectionListener());
        
        pluginPopup = new JPopupMenu();
        deletePluginItem = new JMenuItem("Delete Plugin");
        deletePluginItem.addActionListener(this);
        pluginPopup.add(deletePluginItem);
        pluginTable.addMouseListener(new TableMouseListener(pluginTable,pluginPopup));
    
        JScrollPane scrollPane = new JScrollPane(pluginTable);
        scrollPane.setBounds(insets.left + 5, insets.top + 5, 590, 175);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Plugins"));
        
        add(scrollPane);

        updatePlugins();
    }

    private void initAddPluginPanel() {
        Insets insets = this.getInsets();
        addPluginPanel.setBounds(insets.left+5,insets.top+185,590,80);
        addPluginPanel.setLayout(new FlowLayout());
        addPluginPanel.setBorder(BorderFactory.createTitledBorder("Add new plugin"));
        add(addPluginPanel);
        
        addPluginButton.setText("Add Plugin");
        addPluginButton.addActionListener(this);
        
        addPluginPanel.add(pluginNameLabel);
        addPluginPanel.add(pluginNameTextField);
        addPluginPanel.add(descLabel);
        addPluginPanel.add(descTextField);
        addPluginPanel.add(addPluginButton);     
    }
     
    private void initOptionsTable() {
        Insets insets = this.getInsets();
        optionTable = new JTable(){
            @Override
            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return false;
            }
        };
        optionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        optionPopup = new JPopupMenu();
        removeOptionItem = new JMenuItem("Remove Option");
        removeOptionItem.addActionListener(this);
        optionPopup.add(removeOptionItem);
        optionTable.addMouseListener(new TableMouseListener(optionTable,optionPopup));
    
        
        JScrollPane scrollPane = new JScrollPane(optionTable);
        scrollPane.setBounds(insets.left + 5, insets.top + 270, 590, 175);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Options"));
        add(scrollPane);

        updateOptions();
    }

    private void initAddOptionPanel() {
        Insets insets = this.getInsets();
        addOptionPanel.setBounds(insets.left+5,insets.top+455,590,60);
        addOptionPanel.setLayout(new FlowLayout());
        addOptionPanel.setBorder(BorderFactory.createTitledBorder("Add Option to The Selected Plugin"));
        
        addOptionButton.addActionListener(this);
        add(addOptionPanel);
        updateComboBox();
        addOptionPanel.add(addOptionLabel);
        addOptionPanel.add(optionsComboBox);
        addOptionPanel.add(addOptionButton);
        
    }

    @Override
    public void actionPerformed(ActionEvent ae)  {
        Object source = ae.getSource();
        
        if(source == doneButton){
            doneButtonAction();
        }
        else if(source == addPluginButton){
            addPluginButtonAction();
        }
        else if(source == addOptionButton){
            addOptionButtonAction();
        }
        else if(source == deletePluginItem){
            int row = pluginTable.getSelectedRow();
            ComboBoxItem cbi = (ComboBoxItem) pluginTable.getModel().getValueAt(row, 0);
            deleteButtonAction(cbi.getID());
        }
        else if (source == removeOptionItem){
            int row = optionTable.getSelectedRow();
            ComboBoxItem cbi = (ComboBoxItem) optionTable.getModel().getValueAt(row, 0);
            removeButtonAction(cbi.getID());
        }
    }

    private void updatePlugins() {
        pluginTable.removeAll();
        String [] columnNames = {"Plugin Name"};
        Plugin [] plugins = DatabaseConn.getPlugins();
        ComboBoxItem [][] data = new ComboBoxItem[plugins.length][1];
        for(int i=0;i<plugins.length;i++) {
            data[i][0] = new ComboBoxItem(plugins[i].getID(), plugins[i].getName());
        }
        pluginTable.setModel(new DefaultTableModel(data,columnNames));
    }

    private void updateOptions() {
        optionTable.removeAll();
        String [] columnNames = {"Option","Type","Description"};
        Option [] options = DatabaseConn.getPluginOptions(selectedPluginID);
        if(options==null) return;
        ComboBoxItem [][] data = new ComboBoxItem[options.length][3];
        for(int i=0;i<options.length;i++) {
            data[i][0] = new ComboBoxItem(options[i].getID(), options[i].getCmd());
            data[i][1] = new ComboBoxItem(0,options[i].getValueType().toString());
            data[i][2] = new ComboBoxItem(0,options[i].getDesc());
        }
        optionTable.setModel(new DefaultTableModel(data,columnNames));
    }

    private void updateComboBox(){
        Option [] options = DatabaseConn.getOptions();
        if(options==null) return;
        optionsComboBox.removeAllItems();
        
        for(Option op :options){
            optionsComboBox.addItem(new ComboBoxItem(op.getID(),op.getCmd()+", "+op.getDesc()+", "+op.getValueType().toString()));
        }
    }
    
    @Override
    public void setVisible(boolean visible){
        super.setVisible(visible);
        
        if(visible){
           // updatePlugins();
            pluginNameTextField.setText("");
            updateComboBox();
            updateOptions();
        }
    }
    
    private void doneButtonAction() {
        setVisible(false);
    }

    private void addPluginButtonAction() {
        String pluginName = pluginNameTextField.getText();
        if(pluginName == null || pluginName.isEmpty() || pluginName.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Enter a value for plugin name");
            return;
        }
        boolean isUpdated = false;
        
        if(DatabaseConn.pluginExists(pluginName)){
            String msg = "This plugin exists. Do you want to update description?";
            if(0 == JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION)){
                DatabaseConn.updatePluginDesc(pluginName,descTextField.getText());
                isUpdated = true;
            }
            
        }
        else{
            String desc = descTextField.getText();
            if(desc == null || desc.isEmpty() || desc.trim().isEmpty()) desc = null;
            Plugin p = new Plugin(0, pluginName, desc);
            DatabaseConn.addPlugin(p);
            isUpdated = true;
        }
        
        if(isUpdated){
        updatePlugins();
        pluginNameTextField.setText("");
        descTextField.setText("");
        }
    }

    private void addOptionButtonAction() {
        ComboBoxItem cbi = (ComboBoxItem) optionsComboBox.getSelectedItem();
        if(cbi==null) return;
        if(selectedPluginID ==0 ){
            JOptionPane.showMessageDialog(this, "Select a plugin first");
            return;
        }
        
        if(DatabaseConn.pluginOptionExists(selectedPluginID,cbi.getID())){
            JOptionPane.showMessageDialog(this, "This option already exists");
            return;
        }
        
        DatabaseConn.addPluginOption(selectedPluginID,cbi.getID());
        
        updateOptions();
    }

    private void deleteButtonAction(int pluginID) {
        try {

            if (DatabaseConn.pluginUsed(pluginID)) {
                JOptionPane.showMessageDialog(this, "Error: this plugin is used in a batch file.");
            } else {
                DatabaseConn.deletePlugin(pluginID);
                selectedPluginID = 0;
                updatePlugins();
                updateOptions();
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

    }

    private void removeButtonAction(int optionID) {
        DatabaseConn.deletePluginOption(selectedPluginID,optionID);
        updateOptions();
    }

    private class TableSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            
            if (!e.getValueIsAdjusting()) {
                int row = pluginTable.getSelectedRow();
               // System.out.println("current row = " + row);
                if(row>=0 && row<pluginTable.getRowCount()){
                    ComboBoxItem cbi = (ComboBoxItem) pluginTable.getModel().getValueAt(row, 0);
                    selectedPluginID = cbi.getID();
                }
                else{
                    selectedPluginID = 0;
                }
                
                //System.out.println("current first = " + first + " last = "+last +" selected id = "+ selectedPluginID);
                updateOptions();
            }
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
