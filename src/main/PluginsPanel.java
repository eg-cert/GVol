package main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import database.*;
import iface.*;
import java.awt.event.MouseEvent;

class PluginsPanel extends JPanel implements ActionListener {

    private final JLabel imageLabel;
    private final JLabel profileLabel;
    private final JLabel pluginLabel;
    private final JLabel outputDirLabel;
    private final JLabel batchFileLabel;
    
    private final JButton executeButton;
    
    private final JComboBox profilesList;
    private final JComboBox pluginsList;
    private final JComboBox batchFilesList;
    
    private Plugin[] plugins;
    private Profile[] profiles;
    private BatchFile [] batchFiles;
    
    private final MFileChooser inputFileChooser;
    private final MFileChooser outputDirChooser;
    
    private final JCheckBox writeFileBox;
    private final JCheckBox runBatchBox;
    
    private final ComLayerWithPluginPanel comLayer;
    
    public PluginsPanel(ComLayerWithPluginPanel comLayer) {
        super();
        setBorder(BorderFactory.createTitledBorder("Plugins"));

        
        this.comLayer = comLayer;
        imageLabel = new JLabel();
        profileLabel = new JLabel();
        pluginLabel = new JLabel();
        outputDirLabel = new JLabel();
        batchFileLabel = new JLabel();
        
        inputFileChooser = new MFileChooser(false);
        executeButton = new JButton();
        profilesList = new JComboBox();
        pluginsList = new JComboBox() {
            @Override
            public String getToolTipText(MouseEvent e) {
                try {
                     //System.err.println("called");
                    ComboBoxItem cbi = (ComboBoxItem) this.getSelectedItem();
                    if (cbi == null) {
                        return null;
                    }
                    Plugin plugin = DatabaseConn.getPlugin(cbi.getID());

                   return plugin.getTooltip();
                } catch (Exception ex) {
                   // System.err.println("Failed to get ToolTip");
                }
                return null;

            }
        };
        pluginsList.setToolTipText("select a plugin");
        batchFilesList = new JComboBox();
        writeFileBox = new JCheckBox(String.format("<html><div WIDTH=%d>%s</div><html>", 150,"Write the output to a file."));
        runBatchBox = new JCheckBox("Run batch file.");
        outputDirChooser = new MFileChooser(true);
        
        initComponents();
    }

    private void initComponents() {
        
        imageLabel.setText("Memory image: ");
        profileLabel.setText("Profile: ");
        pluginLabel.setText("Plugin: ");
        outputDirLabel.setText("Choose output directory: ");
        batchFileLabel.setText("Choose batch file: ");
        outputDirChooser.setEnabled(false);
        writeFileBox.addActionListener(this);
        runBatchBox.addActionListener(this);
        
        executeButton.addActionListener(this);
        executeButton.setText("Run Command");
        batchFilesList.setEnabled(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx = 0;
        gc.gridy = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.WEST;
        gc.weightx = gc.weighty = 1;
        
        //labels column
        add(imageLabel,gc);
        gc.gridy++;
        add(profileLabel,gc);
        gc.gridy++;
        add(pluginLabel,gc);
        gc.gridy++;
        add(writeFileBox,gc);
        gc.gridy++;
        add(outputDirLabel,gc);
        gc.gridy++; add(runBatchBox,gc);
        gc.gridy++; add(batchFileLabel,gc);
        
        //second column
        gc.gridy=0;
        gc.gridx=1;
        
        add(inputFileChooser,gc);
        gc.gridy++;
        add(profilesList,gc);
        gc.gridy++;
        add(pluginsList,gc);
        gc.gridy+=2;
        add(outputDirChooser,gc);
        gc.gridy+=3;
        add(batchFilesList,gc);
        gc.gridy++;
        gc.weighty = 3;
        gc.anchor = GridBagConstraints.NORTH;
        add(executeButton,gc);
        
        updateComponents();
    }

    public void updateComponents(){
        plugins = DatabaseConn.getPlugins();
        profiles = DatabaseConn.getProfiles();
        batchFiles = DatabaseConn.getBatchFiles();
        
        pluginsList.removeAllItems();
        profilesList.removeAllItems();
        batchFilesList.removeAllItems();
        
        for (Profile profile : profiles) {
            profilesList.addItem(new ComboBoxItem(profile.getID(), profile.getDescription()));
        }
        int first = -1;
        for(Plugin plugin : plugins){
            if(first == -1) first = plugin.getID();
            
            pluginsList.addItem(new ComboBoxItem(plugin.getID(), plugin.getName()));
        }
        pluginsList.addActionListener(this);
        comLayer.listIndexChanged(first);
        
        for(BatchFile batchFile:batchFiles){
            batchFilesList.addItem(new ComboBoxItem(batchFile.getID(), batchFile.getName()));
        }
        revalidate();
    }
    
    public boolean hasValidValues(){
        String txt;
        
        txt = inputFileChooser.getSelectedFile();
        if(txt==null || txt.isEmpty() || txt.trim().isEmpty())
            return false;

        if(writeFileBox.isSelected()){
            txt = outputDirChooser.getSelectedFile();
            if(txt==null || txt.isEmpty() || txt.trim().isEmpty())
                return false;
        }
        return true;
    }
    
    /**
     * @return the first part of the command which contains the input file
     * and the profile
     */
    
    public String []  getCommand(){
        //input image 
        String [] cmd = new String[4];
        cmd[0] = "-f ";
        cmd[1] = inputFileChooser.getSelectedFile();
        
        //profile
        ComboBoxItem cbi = (ComboBoxItem) profilesList.getSelectedItem();
        Profile p = DatabaseConn.getProfile(cbi.getID());
        cmd[2] = "--profile=";
        cmd[3] = p.getName();    
        return cmd;
    }
    
    public boolean shouldWriteToFile(){
        return writeFileBox.isSelected();
    }
     
    public int runBatchFile(){
        if(runBatchBox.isSelected()){
            ComboBoxItem cbi = (ComboBoxItem)batchFilesList.getSelectedItem();
            if(cbi==null) return -1;
            return cbi.getID();
        }
        else return -1;
    }
    
    public String getPluginName(){
        //plugin 
        ComboBoxItem cbi = (ComboBoxItem) pluginsList.getSelectedItem();
        Plugin pl = DatabaseConn.getPlugin(cbi.getID());
        return pl.getName();
    }
    
    public String getFileName(){
        return inputFileChooser.getFileName();
    }
    
    public String getOutputDir(){
        String str = outputDirChooser.getSelectedFile();
        if(str!=null){
            str = str.substring(1,str.length()-1);
        }
        return str;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(pluginsList == source){
            if(pluginsList.getItemCount()<1) return ;
            ComboBoxItem cbi = (ComboBoxItem) pluginsList.getSelectedItem();
            comLayer.listIndexChanged(cbi.getID());
        }
        else if(executeButton == source){
            executeButtonAction();
        }
        else if(writeFileBox == source){
            writeFileBoxAction();
            
        }
        else if(runBatchBox == source){
            runBatchBoxAction();
        }
       
    }
    
    private void executeButtonAction() {  
        comLayer.buttonClicked();
    }

    private void writeFileBoxAction() {
        outputDirChooser.setEnabled(writeFileBox.isSelected());
    }

    private void runBatchBoxAction() {
        boolean selected = runBatchBox.isSelected();
        batchFilesList.setEnabled(selected);
        pluginsList.setEnabled(!selected);
        comLayer.listIndexChanged(-1);
        executeButton.setText(selected?"Run Batch":"Run Command");
    }

}
