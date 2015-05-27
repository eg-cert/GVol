
package gvol;

import java.awt.Color;
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


public class PluginsDialog extends JDialog implements ActionListener{
    
    
    final private JPanel pluginsPanel;
    
    final private JPanel addPluginPanel;
    final private JLabel pluginNameLabel;
    final private JTextField pluginNameTextField;
    final private JButton addPluginButton;
    
    final private JPanel optionsPanel;
    
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

        pluginsPanel = new JPanel();
        addPluginPanel = new JPanel();
        pluginNameLabel=new JLabel("Plugin Name:");
        pluginNameTextField= new JTextField(15);
        addPluginButton= new JButton();
        
        optionsPanel = new JPanel();
        addOptionPanel = new JPanel();
        addOptionLabel = new JLabel("Select Option");
        optionsComboBox = new JComboBox();
        addOptionButton = new JButton("Add");
        doneButton = new JButton();
        
        
        initPluginsPanel();
        initAddPluginPanel();
        initOptionsPanel();
        initAddOptionPanel();
        
        doneButton.setText("Done");
        doneButton.addActionListener(this);
        doneButton.setBounds(getInsets().left + 500, getInsets().top + 500, 80, 25);
        add(doneButton);
    }

    private void initPluginsPanel() {
        Insets insets = this.getInsets();
        JScrollPane scrollPane = new JScrollPane(pluginsPanel);
        scrollPane.setBounds(insets.left + 5, insets.top + 5, 590, 175);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Plugins"));
        add(scrollPane);

        updatePlugins();
    }

    private void initAddPluginPanel() {
        Insets insets = this.getInsets();
        addPluginPanel.setBounds(insets.left+5,insets.top+185,590,60);
        addPluginPanel.setLayout(new FlowLayout());
        addPluginPanel.setBorder(BorderFactory.createTitledBorder("Add new plugin"));
        add(addPluginPanel);
        
        addPluginButton.setText("Add Plugin");
        addPluginButton.addActionListener(this);
        
        addPluginPanel.add(pluginNameLabel);
        addPluginPanel.add(pluginNameTextField);
        addPluginPanel.add(addPluginButton);
        
        
    }
     
    private void initOptionsPanel() {
        Insets insets = this.getInsets();
        JScrollPane scrollPane = new JScrollPane(optionsPanel);
        scrollPane.setBounds(insets.left + 5, insets.top + 250, 590, 175);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Options"));
        add(scrollPane);

        updateOptions();
    }

    private void initAddOptionPanel() {
        Insets insets = this.getInsets();
        addOptionPanel.setBounds(insets.left+5,insets.top+435,590,60);
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
    public void actionPerformed(ActionEvent ae) {
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
        else{
            try{
                JButton but = (JButton) source;
                String str = but.getActionCommand();
                String [] arr = str.split(":");
                if(arr.length!=2 || arr[0].isEmpty() || arr[1].isEmpty())
                    return;
                if(arr[0].compareTo("select")==0)
                    selectButtonAction(Integer.parseInt(arr[1]));
                else if(arr[0].compareTo("delete")==0)
                    deleteButtonAction(Integer.parseInt(arr[1]));
                else if(arr[0].compareTo("remove")==0)
                    removeButtonAction(Integer.parseInt(arr[1]));
                
            }
            catch(Exception ex){}
        
        }
    }

    private void updatePlugins() {
        pluginsPanel.removeAll();

        Plugin[] plugins = DatabaseConn.getPlugins();

        pluginsPanel.setLayout(new GridLayout(Math.max(6, plugins.length), 3, 1, 1));

        for (int i = 0; i < plugins.length; i++) {
            JLabel nameLabel = new JLabel(plugins[i].getName());
            if(plugins[i].getID() == selectedPluginID){
                nameLabel.setOpaque(true);
                nameLabel.setBackground(Color.BLUE);
                nameLabel.setForeground(Color.WHITE);
            }
            pluginsPanel.add(nameLabel);
            JButton selectButton = new JButton("Select");
            JButton deleteButton = new JButton("Delete");
            
            selectButton.addActionListener(this);
            selectButton.setActionCommand("select:"+((Integer) plugins[i].getID()).toString());
            pluginsPanel.add(selectButton);
            
            deleteButton.addActionListener(this);
            deleteButton.setActionCommand("delete:"+((Integer) plugins[i].getID()).toString());
            pluginsPanel.add(deleteButton);
            
        }

        for (int i = 0; i < (6 - plugins.length) * 3; i++) {
            pluginsPanel.add(new JLabel(""));
        }
        revalidate();
        repaint();
    }

    private void updateOptions() {
        Option [] options = DatabaseConn.getPluginOptions(selectedPluginID);
        if(options==null) return;
        
        optionsPanel.removeAll();

        optionsPanel.setLayout(new GridLayout(Math.max(6, options.length), 4, 1, 1));

        for (int i = 0; i < options.length; i++) {

            optionsPanel.add(new JLabel(options[i].getCmd()));
            optionsPanel.add(new JLabel(options[i].getValueType().toString()));
            optionsPanel.add(new JLabel(options[i].getDesc()));
            JButton button = new JButton("Remove");
            button.addActionListener(this);
            button.setActionCommand("remove:"+((Integer) options[i].getID()).toString());
            optionsPanel.add(button);
        }

        for (int i = 0; i < (6 - options.length) * 4; i++) {
            optionsPanel.add(new JLabel(""));
        }
        revalidate();
        repaint();
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
            selectedPluginID = 0;
            updatePlugins();
            pluginNameTextField.setText("");
            
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
        if(DatabaseConn.pluginExists(pluginName)){
            JOptionPane.showMessageDialog(this, "This plugin already exists");
            return;
        }
        
        Plugin p = new Plugin(0, pluginName, null);
        
        DatabaseConn.addPlugin(p);
        
        updatePlugins();
        pluginNameTextField.setText("");
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

    private void selectButtonAction(int pluginID) {
        selectedPluginID = pluginID;
        updatePlugins();
        updateOptions();
        
    }

    private void deleteButtonAction(int pluginID) {
        DatabaseConn.deletePlugin(pluginID);
        selectedPluginID = 0;
        updatePlugins();
        updateOptions();
        
    }

    private void removeButtonAction(int optionID) {
        DatabaseConn.deletePluginOption(selectedPluginID,optionID);
        updateOptions();
    }
    
    
}
