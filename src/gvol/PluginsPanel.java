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
    private final JButton executeButton;
    private final JComboBox profilesList;
    private final JComboBox pluginsList;
    private final Plugin[] plugins;
    private final Profile[] profiles;
    private final MFileChooser fileChooser;
    private final ComLayerWithPluginPanel comLayer;
    private final JCheckBox writeFileBox;
    private final MFileChooser outputDirChooser;
    
    public PluginsPanel(ComLayerWithPluginPanel comLayer) {
        super();
        setBorder(BorderFactory.createTitledBorder("Plugins"));

        this.plugins = DatabaseConn.getPlugins();
        this.profiles = DatabaseConn.getProfiles();
        this.comLayer = comLayer;
        imageLabel = new JLabel();
        profileLabel = new JLabel();
        pluginLabel = new JLabel();
        outputDirLabel = new JLabel();
        fileChooser = new MFileChooser(false);
        executeButton = new JButton();
        profilesList = new JComboBox();
        pluginsList = new JComboBox();
        writeFileBox = new JCheckBox("Write the output to a file.");
        outputDirChooser = new MFileChooser(true);
        
        initComponents();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(pluginsList == e.getSource()){
            ComboBoxItem cbi = (ComboBoxItem) pluginsList.getSelectedItem();
            comLayer.listIndexChanged(cbi.getID());
        }
        else if(executeButton == e.getSource()){
            comLayer.buttonClicked();
        }
        else if(writeFileBox == e.getSource()){
            outputDirChooser.setEnabled(writeFileBox.isSelected());
        }
    }

    private void initComponents() {
        
        imageLabel.setText("Memory Image: ");
        profileLabel.setText("Profile: ");
        pluginLabel.setText("Plugin: ");
        outputDirLabel.setText("Choose output directory: ");
        
        outputDirChooser.setEnabled(false);
        writeFileBox.addActionListener(this);
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
        
        executeButton.addActionListener(this);
        executeButton.setText("Run Command");
        
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx = 0;
        gc.gridy = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.WEST;
        gc.weightx = gc.weighty = 1;
        
        add(imageLabel,gc);
        gc.gridy++;
        add(profileLabel,gc);
        gc.gridy++;
        add(pluginLabel,gc);
        gc.gridy++;
        add(writeFileBox,gc);
        gc.gridy++;
        add(outputDirLabel,gc);
        
        gc.gridy=0;
        gc.gridx=1;
        
        add(fileChooser,gc);
        gc.gridy++;
        add(profilesList,gc);
        gc.gridy++;
        add(pluginsList,gc);
        gc.gridy+=2;
        add(outputDirChooser,gc);
        
        gc.gridy++;
        gc.weighty = 3;
        gc.anchor = GridBagConstraints.NORTH;
        add(executeButton,gc);
    }

    public boolean hasValidValues(){
        String txt = fileChooser.getSelectedFile();
        if(txt==null || txt.isEmpty() || txt.trim().isEmpty())
            return false;
        if(writeFileBox.isSelected()){
            txt = outputDirChooser.getSelectedFile();
            if(txt==null || txt.isEmpty() || txt.trim().isEmpty())
                return false;
        }
        return true;
    }
    
    public String getComand(){
        String cmd;
        //input image 
        cmd = "-f "+fileChooser.getSelectedFile()+" ";
        //profile
        ComboBoxItem cbi = (ComboBoxItem) profilesList.getSelectedItem();
        Profile p = DatabaseConn.getProfile(cbi.getID());
        cmd = cmd + "--profile=" + p.getName()+" ";
        //plugin 
        cbi = (ComboBoxItem) pluginsList.getSelectedItem();
        Plugin pl = DatabaseConn.getPlugin(cbi.getID());
        cmd = cmd + pl.getName()+ " ";
        
        return cmd;
    }
    
    public boolean shouldWriteToFile(){
        
        return writeFileBox.isSelected();
    }
    
    public String getFileName(){
        ComboBoxItem cbi = (ComboBoxItem) pluginsList.getSelectedItem();
        Plugin pl = DatabaseConn.getPlugin(cbi.getID());
        return fileChooser.getFileName()+"-"+pl.getName()+"-output";
    }
    
    public String getOutputDir(){
        String str = outputDirChooser.getSelectedFile();
        if(str!=null){
            str = str.substring(1,str.length()-1);
        }
        return str;
    }
    
}
