package main;

import dialog.OptionsDialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MFileChooser extends JPanel implements ActionListener {

    private final JPopupMenu textFieldPopup;
    private final JMenuItem clearTextItem;
    private final JTextField textField;
    private final JButton browseButton;
    private final JFileChooser fileChooser;

    public MFileChooser(boolean isDirectory) {
        super();
        textField = new JTextField(15);
        textField.setPreferredSize(new Dimension(200, 25));
        browseButton = new JButton();
        fileChooser = new JFileChooser();

        textField.setEditable(false);
        browseButton.setText("Browse");
        //browseButton.setPreferredSize(new Dimension(70,20));
        browseButton.addActionListener(this);
        if (isDirectory) {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        fileChooser.setApproveButtonText("Select");
        setSize(new Dimension(300, 25));
        setLayout(new GridBagLayout());

        textFieldPopup = new JPopupMenu();
        clearTextItem = new JMenuItem("Clear Selected File");
        clearTextItem.addActionListener(this);
        textFieldPopup.add(clearTextItem);
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                if (SwingUtilities.isRightMouseButton(e)) {
                    textFieldPopup.show(textField, e.getX(), e.getY());
                }

            }
        });

        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx = gc.gridy = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        add(textField, gc);

        gc.gridx++;
        gc.anchor = GridBagConstraints.WEST;
        add(browseButton, gc);

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if(source == browseButton){
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
        }
        else if(source == clearTextItem){
            textField.setText("");
        }
    }

    public String getSelectedFile() {
        String filePath = textField.getText();
        if (filePath == null || filePath.isEmpty() || filePath.trim().isEmpty()) {
            return null;
        } else {
            return filePath;//.replace("\\", "\\\\");
            // if(OSType.isWindows()) return "\""+filePath+"\"";
            // else return filePath.replace(" ", "\\ ");
        }
    }

    @Override
    public void setEnabled(boolean val) {
        browseButton.setEnabled(val);
    }

    public String getFileName() {
        String filePath = getSelectedFile();
        if (filePath == null) {
            return null;
        }

        String fileName = "";
        for (int i = filePath.length() - 2; i >= 0; i--) {
            if (filePath.charAt(i) == '\\' || filePath.charAt(i) == '/') {
                break;
            } else {
                fileName = filePath.charAt(i) + fileName;
            }
        }
        return fileName;
    }
}
