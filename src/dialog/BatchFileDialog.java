
package dialog;

import database.*;
import java.awt.Component;
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

public class BatchFileDialog extends JDialog implements ActionListener {

    private JTable batchTable;
    private JPopupMenu batchPopup;
    private JMenuItem deleteBatchItem;
    
    private JTable pluginTable;
    private JPopupMenu pluginPopup;
    private JMenuItem removePluginItem;
    
    final private JPanel addBatchPanel;
    final private JLabel nameLabel;
    final private JTextField nameTextField;
    final private JButton addBatchButton;

    final private JPanel addPluginPanel;
    final private JLabel addPluginLabel;
    final private JComboBox pluginsComboBox;
    final private JButton addPluginButton;
    
    final private JButton doneButton;
    private int selectedFileID = 0;

    public BatchFileDialog(JFrame parent) {
        super(parent, true);
        setSize(new Dimension(620, 580));
        setLayout(null);
        setLocationRelativeTo(parent);

       
        addBatchPanel = new JPanel();
        nameLabel = new JLabel("Name: ");
        nameTextField = new JTextField(15);
        addBatchButton = new JButton("Add");
        selectedFileID = 0;

        
        addPluginPanel = new JPanel();
        addPluginLabel = new JLabel("Select Plugin");
        pluginsComboBox = new JComboBox();
        addPluginButton = new JButton("Add");
        
        initBatchTable();
        initAddBatchPanel();
        initPluginsTable();
        initAddPluginPanel();
        doneButton = new JButton("Done");
        doneButton.addActionListener(this);
        Insets insets = this.getInsets();
        doneButton.setBounds(insets.left + 500, insets.top + 500, 80, 25);
        add(doneButton);

    }

    private void initBatchTable() {
        Insets insets = this.getInsets();
        batchTable = new JTable(){
            @Override
            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return false;
            }
        };
        batchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        batchTable.getSelectionModel().addListSelectionListener(new TableSelectionListener());
        
        batchPopup = new JPopupMenu();
        deleteBatchItem = new JMenuItem("Delete Batch File");
        deleteBatchItem.addActionListener(this);
        batchPopup.add(deleteBatchItem);
        batchTable.addMouseListener(new TableMouseListener(batchTable,batchPopup));
    
        JScrollPane scrollPane = new JScrollPane(batchTable);
        scrollPane.setBounds(insets.left + 5, insets.top + 5, 590, 175);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Batch Files"));
        
        add(scrollPane);

        selectedFileID = 0;
        updateBatches();
    }
     
    private void initAddBatchPanel() {
        Insets insets = this.getInsets();
        addBatchPanel.setBounds(insets.left+5,insets.top+185,590,60);
        addBatchPanel.setLayout(new FlowLayout());
        addBatchPanel.setBorder(BorderFactory.createTitledBorder("Add new batch file"));
        add(addBatchPanel);
        
        addBatchButton.addActionListener(this);
        
        addBatchPanel.add(nameLabel);
        addBatchPanel.add(nameTextField);
        addBatchPanel.add(addBatchButton);
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
                java.awt.Point p = e.getPoint();
                int row = rowAtPoint(p);
                ComboBoxItem cbi = (ComboBoxItem) pluginTable.getModel().getValueAt(row, 0);
                int id = cbi.getID();
                Plugin plugin = DatabaseConn.getPlugin(id); 
                return plugin.getTooltip();
            }

        };
        pluginTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        pluginPopup = new JPopupMenu();
        removePluginItem = new JMenuItem("Remove Plugin");
        removePluginItem.addActionListener(this);
        pluginPopup.add(removePluginItem);
        pluginTable.addMouseListener(new TableMouseListener(pluginTable,pluginPopup));
    
        
        JScrollPane scrollPane = new JScrollPane(pluginTable);
        scrollPane.setBounds(insets.left + 5, insets.top + 250, 590, 175);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Selected batch file plugins"));
        add(scrollPane);

        updatePlugins();
        
    }
    
    private void initAddPluginPanel() {
        Insets insets = this.getInsets();
        addPluginPanel.setBounds(insets.left+5,insets.top+435,590,60);
        addPluginPanel.setLayout(new FlowLayout());
        addPluginPanel.setBorder(BorderFactory.createTitledBorder("Add Plugin to The Selected Batch File"));
        
        addPluginButton.addActionListener(this);
        add(addPluginPanel);
        updateComboBox();
        addPluginPanel.add(addPluginLabel);
        addPluginPanel.add(pluginsComboBox);
        addPluginPanel.add(addPluginButton);
    }
    
    private void updateBatches() {
        batchTable.removeAll();
        String [] columnNames = {"Batch File Name"};
        BatchFile[] batches = DatabaseConn.getBatchFiles();
        if(batches == null ) return;
        ComboBoxItem [][] data = new ComboBoxItem[batches.length][1];
        for(int i=0;i<batches.length;i++) {
            data[i][0] = new ComboBoxItem(batches[i].getID(), batches[i].getName());
        }
        batchTable.setModel(new DefaultTableModel(data,columnNames));
         
    }

    private void updatePlugins() {
        pluginTable.removeAll();
       // System.err.println("updating plugins for "+selectedFileID);
        String [] columnNames = {"Plugin Name"};
        Plugin [] plugins = DatabaseConn.getBatchFilePlugins(selectedFileID);
        if(plugins==null) return;
        ComboBoxItem [][] data = new ComboBoxItem[plugins.length][1];
        for(int i=0;i<plugins.length;i++) {
            data[i][0] = new ComboBoxItem(plugins[i].getID(), plugins[i].getName());
        }
        pluginTable.setModel(new DefaultTableModel(data,columnNames));
    }
    
    private void updateComboBox() {
        pluginsComboBox.removeAllItems();
        Plugin [] plugins = DatabaseConn.getPlugins();
        if(plugins==null) return;
        
        for(Plugin pl :plugins){
            pluginsComboBox.addItem(new ComboBoxItem(pl.getID(),pl.getName()));
        }
        addPluginPanel.revalidate();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        Component source = (Component) ae.getSource();
        if (source == doneButton) {
            doneButtonAction();
        } else if(source == addBatchButton) {
            addBatchButtonAction();
        }else if(source == deleteBatchItem){
            int row = batchTable.getSelectedRow();
            ComboBoxItem cbi = (ComboBoxItem) batchTable.getModel().getValueAt(row, 0);
            deleteButtonAction(cbi.getID());
        }
        else if(source == addPluginButton){
            addPluginButtonAction();
        }
        else if(source == removePluginItem){
            int row = pluginTable.getSelectedRow();
            ComboBoxItem cbi = (ComboBoxItem) pluginTable.getModel().getValueAt(row, 0);
            removeButtonAction(cbi.getID());
        }
    }

    @Override
    public void setVisible(boolean visible) {
        
        if(visible){
            selectedFileID = 0;
            nameTextField.setText("");
            updateBatches();
            updatePlugins();
            updateComboBox();
        }
        super.setVisible(visible);
    }

    private void doneButtonAction() {
        setVisible(false);
    }

    private void addBatchButtonAction() {
        String name = nameTextField.getText();
        if(name==null || name.isEmpty() || name.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Enter a value for the batch file name");
            return;
        }
        
        if(DatabaseConn.batchFileExists(name)){
            JOptionPane.showMessageDialog(this, "A batch file with the same name already exists.");
            return;
        }
        
        BatchFile batchFile = new BatchFile(0, name);
        
        DatabaseConn.addBatchFile(batchFile);
        updateBatches();
        
        nameTextField.setText("");
    }

    private void deleteButtonAction(int batchFileID) {
        DatabaseConn.deleteBatchFile(batchFileID);
        selectedFileID =0;
        updateBatches();
        updatePlugins();
    }

    private void removeButtonAction(int pluginID) {
        DatabaseConn.deleteBatchFilePlugin(selectedFileID,pluginID);
        updatePlugins();
    }

    private void addPluginButtonAction() {
       ComboBoxItem cbi = (ComboBoxItem) pluginsComboBox.getSelectedItem();
        if(cbi==null) return;
        if(selectedFileID == 0){
            JOptionPane.showMessageDialog(this, "Select a batch file first");
            return;
        }
        
        if(DatabaseConn.batchFilePluginExists(selectedFileID,cbi.getID())){
            JOptionPane.showMessageDialog(this, "This plugin already exists");
            return;
        }
        
        DatabaseConn.addBatchFilePlugin(selectedFileID,cbi.getID());
        
        updatePlugins();
    }

    private class TableSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            
            if (!e.getValueIsAdjusting()) {
                int row = batchTable.getSelectedRow();
                //System.err.println("current row = " + row);
                if(row >= 0 && row < batchTable.getRowCount()){
                    ComboBoxItem cbi = (ComboBoxItem) batchTable.getModel().getValueAt(row, 0);
                    selectedFileID = cbi.getID();
                }
                else{
                    selectedFileID = 0;
                }
                
                //System.err.println("current first = " + first + " last = "+last +" selected id = "+ selectedPluginID);
                updatePlugins();
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
