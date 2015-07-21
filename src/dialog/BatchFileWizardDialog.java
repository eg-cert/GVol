package dialog;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import main.*;
import database.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class BatchFileWizardDialog extends JDialog implements ActionListener {

    final private OptionsPanel optionsPanel;
    final private JButton nextButton;
    final private JButton cancelButton;
    final private JLabel stepLabel;
    final private JLabel pluginLabel;
    final private JPanel infoPanel;
    final private String [] cmd;
    final private Plugin [] plugins;
    private boolean isReady;
    private int iterator;
    
    public BatchFileWizardDialog(JFrame parent, int batchFileID){
        super(parent, true);
        setSize(new Dimension(620, 580));
        setLayout(null);
        setLocationRelativeTo(parent);
        setTitle(DatabaseConn.getBatchFile(batchFileID).getName());
        optionsPanel = new OptionsPanel();
        nextButton = new JButton();
        cancelButton = new JButton();
        stepLabel = new JLabel();
        
        pluginLabel = new JLabel();
        infoPanel = new JPanel();
        cmd = new String[DatabaseConn.batchFilePluginCount(batchFileID)];
        plugins = DatabaseConn.getBatchFilePlugins(batchFileID);
        iterator = 0;
        isReady = false;
        initComponents();
        changePlugin(iterator);
    }
    
    public String [] getPluginNames(){
        String [] res = new String [plugins.length];
        for(int i=0;i<plugins.length;i++){
            res[i] = plugins[i].getName();
        }
        return res;
    }
    
    public String [] getCommands(){
        return cmd;
    }
    
    public boolean isReady(){
        return isReady;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Component source = (Component) e.getSource();
        if (source == nextButton) {
            nextButtonAction();
        }
        else if(source == cancelButton){
            cancelButtonAction();
        }
    }

    private void initComponents() {
        infoPanel.setLayout(new GridLayout(2,1));
        infoPanel.add(stepLabel);
        infoPanel.add(pluginLabel);
        Insets insets = getInsets();
        infoPanel.setBounds(insets.left + 5, insets.top + 5, 590, 100);
        JScrollPane scrollPane = new JScrollPane(optionsPanel);
        scrollPane.setBounds(insets.left + 5, insets.top + 110, 590, 380);
        
        nextButton.setBounds(insets.left + 410, insets.top + 500, 80, 25);
        nextButton.addActionListener(this);
        cancelButton.setBounds(insets.left + 500, insets.top + 500, 80, 25);
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(this);
        add(infoPanel);
        add(scrollPane);
        add(nextButton);
        add(cancelButton);
    }

    private void nextButtonAction() {
        if(!optionsPanel.hasValidValues()){
            JOptionPane.showMessageDialog(this, "you must enter values for all selected options.");
            return;
        }
        cmd[iterator] = plugins[iterator].getName() + " " +optionsPanel.getCommand();
        iterator = iterator + 1;
        if(iterator == plugins.length){
            isReady = true;
            setVisible(false);
        }
        else changePlugin(iterator);
    }

    private void cancelButtonAction() {
        setVisible(false);
    }
    
    private void changePlugin(int index){
        if(index<plugins.length){
            stepLabel.setText("Step "+(index+1)+" of "+plugins.length);
            pluginLabel.setText("Plugin: "+plugins[index].getName());
            pluginLabel.setToolTipText(plugins[index].getTooltip());
            optionsPanel.updateVisibleOptions(plugins[index].getID());
            if(index==plugins.length-1){
                nextButton.setText("Finish");
            }
            else nextButton.setText("Next");
        }
    }
    
    
}
