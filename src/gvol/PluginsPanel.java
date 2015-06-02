package gvol;

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
    private final JCheckBox addToBatchBox;
    
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
        pluginsList = new JComboBox();
        batchFilesList = new JComboBox();
        writeFileBox = new JCheckBox("Write the output to a file.");
        runBatchBox = new JCheckBox("Run batch file.");
        addToBatchBox = new JCheckBox("Add command to batch file.");
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
        addToBatchBox.addActionListener(this);
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
        gc.gridy++; add(addToBatchBox,gc);
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
        if(!addToBatchBox.isSelected()){
            txt = inputFileChooser.getSelectedFile();
            if(txt==null || txt.isEmpty() || txt.trim().isEmpty())
                return false;
        }
        if(writeFileBox.isSelected()){
            txt = outputDirChooser.getSelectedFile();
            if(txt==null || txt.isEmpty() || txt.trim().isEmpty())
                return false;
        }
        return true;
    }
    
    public String [] getCommands(){
        String [] cmd = null;
        if(addToBatchBox.isSelected()){
            cmd = new String[1];
            //plugin 
            ComboBoxItem cbi = (ComboBoxItem) pluginsList.getSelectedItem();
            Plugin pl = DatabaseConn.getPlugin(cbi.getID());
            cmd[0] =  pl.getName()+ " ";
        }
        else if(runBatchBox.isSelected()){
            ComboBoxItem cbi = (ComboBoxItem) batchFilesList.getSelectedItem();
            Command [] commands = DatabaseConn.getCommands(cbi.getID());
            cmd = new String[commands.length];
            cbi = (ComboBoxItem) profilesList.getSelectedItem();
            Profile p = DatabaseConn.getProfile(cbi.getID());
            String file = inputFileChooser.getSelectedFile();
            String profile = p.getName();
            for(int i=0;i<cmd.length;i++){
                cmd[i] = "-f "+file+" ";
                cmd[i] = cmd[i]+"--profile="+profile+" ";
                cmd[i] = cmd[i]+commands[i].getCmd();
            }
        }
        else{
            cmd = new String[1];
            //input image 
            cmd[0] = "-f "+inputFileChooser.getSelectedFile()+" ";
            //profile
            ComboBoxItem cbi = (ComboBoxItem) profilesList.getSelectedItem();
            Profile p = DatabaseConn.getProfile(cbi.getID());
            cmd[0] = cmd[0] + "--profile=" + p.getName()+" ";
            //plugin 
            cbi = (ComboBoxItem) pluginsList.getSelectedItem();
            Plugin pl = DatabaseConn.getPlugin(cbi.getID());
            cmd[0] = cmd[0] + pl.getName()+ " ";
        }
        return cmd;
    }
    
    public boolean shouldWriteToFile(){
        
        return writeFileBox.isSelected();
    }
    
    public boolean shouldAddToBatchFile(){
        return addToBatchBox.isSelected();
    }
    
    public boolean addToBatchFile(String cmd){
        try{
            ComboBoxItem cbi = (ComboBoxItem) batchFilesList.getSelectedItem();
            
            Command command = new Command(0, cmd,cbi.getID() );
            DatabaseConn.addCommand(command);
        }
        catch(Exception ex){
            return false;
        }
        return true;
    }
    
    public String getFileName(){
        ComboBoxItem cbi = (ComboBoxItem) pluginsList.getSelectedItem();
        Plugin pl = DatabaseConn.getPlugin(cbi.getID());
        return inputFileChooser.getFileName()+"-"+pl.getName()+"-output";
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
        else if(addToBatchBox == source){
            addToBatchBoxAction();
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
        addToBatchBox.setEnabled(!selected);
        batchFilesList.setEnabled(selected);
        pluginsList.setEnabled(!selected);
        comLayer.listIndexChanged(-1);
        executeButton.setText(selected?"Run Batch":"Run Command");
    }

    private void addToBatchBoxAction() {
       boolean selected = addToBatchBox.isSelected();
       inputFileChooser.setEnabled(!selected);
       profilesList.setEnabled(!selected);
       runBatchBox.setEnabled(!selected);
       batchFilesList.setEnabled(selected);
       executeButton.setText(selected?"Add to Batch":"Run Command");
       writeFileBox.setSelected(false);
       writeFileBox.setEnabled(!selected);
       outputDirChooser.setEnabled(false);
       
    }
    
}
