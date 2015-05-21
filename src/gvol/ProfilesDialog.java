/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gvol;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ProfilesDialog extends JDialog implements ActionListener{
    
    final private JPanel cmdPanel;
    final private JLabel volLabel;
    final private JTextField volTextField;
    final private JButton updateCmdButton;
    
    final private JPanel profilesPanel;
    
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
        
        
        //profiles panel and components
        profilesPanel = new JPanel();
        
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
        initProfilesPanel();
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
    
    private void initProfilesPanel() {
        Insets insets = this.getInsets();
        JScrollPane scrollPane= new JScrollPane(profilesPanel);
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
        profilesPanel.removeAll();
       
        Profile [] profiles = DatabaseConn.getProfiles();
        
         profilesPanel.setLayout(new GridLayout(Math.max(7,profiles.length),3,1,1));
         
        
        for(int i=0;i<profiles.length;i++){
            
            profilesPanel.add(new JLabel(profiles[i].getName()));
            
            profilesPanel.add(new JLabel(profiles[i].getDescription()));
            
            JButton button = new JButton("Delete");
            button.addActionListener(this);
            button.setActionCommand(((Integer)profiles[i].getID()).toString());
            profilesPanel.add(button);
        }
        
        for(int i=0;i<(7-profiles.length)*3;i++)
        {
            profilesPanel.add(new JLabel(""));
        }
        revalidate();
        repaint();
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
        else {
            deleteButtonAction(source);
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

    private void deleteButtonAction(Object source) {
        try {
            JButton but = (JButton) source;
            int ID = Integer.parseInt(but.getActionCommand());
            DatabaseConn.deleteProfile(ID);
            updateProfiles();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    
}
