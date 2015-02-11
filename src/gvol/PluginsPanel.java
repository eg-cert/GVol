package gvol;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


class PluginsPanel extends JPanel implements ActionListener {

    private final JLabel imageLabel;
    private final JLabel profileLabel;
    private final JLabel pluginLabel;
    private final JButton executeButton;
    private final JComboBox profilesList;
    private final JComboBox pluginsList;
    private final Plugin[] plugins;
    private final Profile[] profiles;
    private final MFileChooser fileChooser;
    private final ComLayerWithPluginPanel comLayer;
    
    public PluginsPanel(Plugin[] plugins, Profile[] profiles, ComLayerWithPluginPanel comLayer) {
        super();
        setBorder(BorderFactory.createTitledBorder("Plugins"));

        this.plugins = plugins;
        this.profiles = profiles;
        this.comLayer = comLayer;
        imageLabel = new JLabel();
        profileLabel = new JLabel();
        pluginLabel = new JLabel();
        fileChooser = new MFileChooser(false);
        executeButton = new JButton();
        profilesList = new JComboBox();
        pluginsList = new JComboBox();
        
        initComponents();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(pluginsList == e.getSource()){
            comLayer.listIndexChanged(pluginsList.getSelectedIndex()+1);
        }
        else {
            comLayer.buttonClicked();
        }
    }

    private void initComponents() {
        
        imageLabel.setText("Memory Image: ");
        profileLabel.setText("Profile: ");
        pluginLabel.setText("Plugin: ");
        
        for (Profile profile : profiles) {
            profilesList.addItem(profile.getDescription());
        }
        
        for(Plugin plugin : plugins){
            pluginsList.addItem(plugin.getName());
        }
        
        pluginsList.addActionListener(this);
        pluginsList.setSelectedIndex(0);
        comLayer.listIndexChanged(1);
        
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
        
        gc.gridy=0;
        gc.gridx=1;
        
        add(fileChooser,gc);
        gc.gridy++;
        add(profilesList,gc);
        gc.gridy++;
        add(pluginsList,gc);
        
        gc.gridy++;
        gc.weighty = 3;
        gc.anchor = GridBagConstraints.NORTH;
        add(executeButton,gc);
    }

    public boolean hasValidValues(){
        String txt = fileChooser.getSelectedFile();
        return (!(txt==null || txt.isEmpty() || txt.trim().isEmpty()));
    }
    
    public String getComand(){
        String cmd;
        //input image 
        cmd = "-f "+fileChooser.getSelectedFile()+" ";
        //profile
        cmd = cmd + "--profile=" + profiles[profilesList.getSelectedIndex()].getName()+" ";
        //plugin 
        cmd = cmd + plugins[pluginsList.getSelectedIndex()].getName()+ " ";
        
        return cmd;
    }
    
    public void setButtonText(String txt){
        executeButton.setText(txt);
    }
}
