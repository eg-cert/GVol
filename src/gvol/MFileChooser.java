package gvol;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class MFileChooser extends JPanel implements ActionListener{
    
    private final JTextField textField;
    private final JButton browseButton;
    private final JFileChooser fileChooser;
    
    public MFileChooser(boolean isDirectory){
        super();
        textField = new JTextField(15);
        browseButton = new JButton();
        fileChooser = new JFileChooser();
        
        textField.setEditable(false);
        textField.setPreferredSize(new Dimension(240,25));
        browseButton.setText("Browse");
        browseButton.setPreferredSize(new Dimension(80,25));
        browseButton.addActionListener(this);
        if(isDirectory) fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setApproveButtonText("Select");
        setSize(new Dimension(300,25));
        setLayout(new GridBagLayout());
        
        GridBagConstraints gc = new GridBagConstraints();
        
        gc.gridx = gc.gridy = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        add(textField,gc);
        
        gc.gridx++;
        gc.anchor = GridBagConstraints.WEST;
        add(browseButton,gc);
        
        
        
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if(fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
        
    }
    
    public String getSelectedFile(){
        String filePath = textField.getText();
        if(filePath == null || filePath.isEmpty() || filePath.trim().isEmpty()) 
            return null;
        else return "\""+filePath+"\"";
    }
    
    @Override
    public void setEnabled(boolean val){
        browseButton.setEnabled(val);
    }
}
