/**
 * @author Mohamad Shawkey
 */
package gvol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.border.TitledBorder;

public class BatchFileDialog extends JDialog implements ActionListener {

    private final JPanel batchPanel;
    private final JPanel commandsPanel;
    private final JPanel addBatchPanel;
    private final JLabel nameLabel;
    private final JTextField nameTextField;
    private final JButton addBatchButton;

    private final JButton doneButton;
    private int selectedFileID;

    public BatchFileDialog(JFrame parent) {
        super(parent, true);
        setSize(new Dimension(620, 580));
        setLayout(null);
        setLocationRelativeTo(parent);

        batchPanel = new JPanel();
        commandsPanel = new JPanel();
        addBatchPanel = new JPanel();
        nameLabel = new JLabel("Name: ");
        nameTextField = new JTextField(15);
        addBatchButton = new JButton("Add");
        selectedFileID = 0;

        initBatchPanel();
        initAddBatchPanel();
        initCommandsPanel();

        doneButton = new JButton("Done");
        doneButton.addActionListener(this);
        Insets insets = this.getInsets();
        doneButton.setBounds(insets.left + 500, insets.top + 490, 80, 25);
        add(doneButton);

    }

    private void initBatchPanel() {
        JScrollPane scrollPane = new JScrollPane(batchPanel);
        Insets insets = this.getInsets();
        scrollPane.setBorder(new TitledBorder("Batch Files"));
        scrollPane.setBounds(insets.left + 5, insets.top + 5, 590, 200);
        add(scrollPane);
        selectedFileID = -1;
        updateBatches();
    }
     
    private void initAddBatchPanel() {
        Insets insets = this.getInsets();
        addBatchPanel.setBounds(insets.left+5,insets.top+210,590,60);
        addBatchPanel.setLayout(new FlowLayout());
        addBatchPanel.setBorder(BorderFactory.createTitledBorder("Add new batch file"));
        add(addBatchPanel);
        
        addBatchButton.addActionListener(this);
        
        addBatchPanel.add(nameLabel);
        addBatchPanel.add(nameTextField);
        addBatchPanel.add(addBatchButton);
    }
    
    private void initCommandsPanel() {
        JScrollPane scrollPane = new JScrollPane(commandsPanel);
        Insets insets = this.getInsets();
        
        scrollPane.setBorder(new TitledBorder("Selected batch file commands"));
        
        scrollPane.setBounds(insets.left + 5, insets.top + 275, 590, 200);
        add(scrollPane);
        
        updateCommands();
        
    }

    private void updateBatches() {
        BatchFile[] batches = DatabaseConn.getBatchFiles();
        batchPanel.removeAll();
        batchPanel.setLayout(new GridLayout(Math.max(batches.length,9),3,1,1));
        
        for (BatchFile batchFile : batches) {
            JLabel label = new JLabel(batchFile.getName());
            if (batchFile.getID() == selectedFileID) {
                label.setOpaque(true);
                label.setBackground(Color.BLUE);
                label.setForeground(Color.WHITE);
            }
            batchPanel.add(label);
            JButton selectButton = new JButton("Select");
            selectButton.setActionCommand("select:" + batchFile.getID());
            selectButton.addActionListener(this);
            batchPanel.add(selectButton);
            JButton deleteButton = new JButton("Delete");
            deleteButton.setActionCommand("delete:" + batchFile.getID());
            deleteButton.addActionListener(this);
            batchPanel.add(deleteButton); 
        }
        for(int i=0; i<(9-batches.length)*3;i++){
            batchPanel.add(new JLabel());
            
        }
        revalidate();
        repaint();
        
    }

    private void updateCommands() {
        Command [] commands = DatabaseConn.getCommands(selectedFileID);
        commandsPanel.removeAll();
        commandsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.WEST;
        gc.weighty = 1;
        gc.gridy = 0 ;
        for(Command command:commands){
            JLabel label = new JLabel(command.getCmd());
            gc.weightx = 4;
            gc.gridx = 0;
            commandsPanel.add(label,gc);
            
            JButton button = new JButton("Remove");
            button.addActionListener(this);
            button.setActionCommand("remove:"+command.getID());
            gc.weightx = 1;
            gc.gridx = 1;
            commandsPanel.add(button,gc);
            
            gc.gridy++;
            
        }
        
        revalidate();
        repaint();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        Component source = (Component) ae.getSource();

        if (source == doneButton) {
            doneButtonAction();
        } else if(source == addBatchButton) {
            addBatchButtonAction();
        }else{
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

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(visible){
            selectedFileID = -1;
            nameTextField.setText("");
            updateBatches();
            updateCommands();
        }
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

    private void selectButtonAction(int batchFileID) {
        selectedFileID = batchFileID;
        updateBatches();
        updateCommands();
    }

    private void deleteButtonAction(int batchFileID) {
        DatabaseConn.deleteBatchFile(batchFileID);
        selectedFileID =-1;
        updateBatches();
        updateCommands();
    }

    private void removeButtonAction(int commandID) {
        DatabaseConn.deleteCommand(commandID);
        updateBatches();
        updateCommands();
    }

}
