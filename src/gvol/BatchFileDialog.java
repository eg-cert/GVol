/**
 * @author Mohamad Shawkey
 */
package gvol;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

public class BatchFileDialog extends JDialog implements ActionListener {

    private final JPanel batchPanel;
    private final JPanel commandsPanel;
    private final JTable batchTable;
    private final AbstractTableModel batchModel;
    private final JTable commandsTable;
    private final AbstractTableModel commandsModel;
    private final JButton doneButton;
    private final BatchFileDialog dialog;
    private int selectedFileID;

    public BatchFileDialog(JFrame parent) {
        super(parent, true);
        setSize(new Dimension(620, 480));
        setLayout(null);
        setLocationRelativeTo(parent);

        batchPanel = new JPanel();
        commandsPanel = new JPanel();

        batchModel = new BatchModel();
        commandsModel = new CommandsModel();
        batchTable = new JTable(batchModel);
        commandsTable = new JTable(commandsModel);
        doneButton = new JButton();
        dialog=this;
        selectedFileID = 0;
        doneButton.setText("Done");
        doneButton.addActionListener(this);

        Insets insets = this.getInsets();
        doneButton.setBounds(insets.left + 350, insets.top + 390, 100, 30);
        add(doneButton);

        initBatchPanel();
        initCommandsPanel();
    }

    private void initBatchPanel() {
        batchTable.getColumn(batchTable.getColumnName(1)).setCellRenderer(new ButtonRenderer());
        batchPanel.setLayout(new BorderLayout());
        batchPanel.setBorder(BorderFactory.createTitledBorder("Batch Files"));
        batchPanel.add(new JScrollPane(batchTable));
        Insets insets = this.getInsets();
        batchPanel.setBounds(insets.left + 5, insets.top + 5, 290, 380);
        add(batchPanel);
    }

    private void initCommandsPanel() {
        commandsPanel.setLayout(new BorderLayout());
        commandsPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
        commandsPanel.add(new JScrollPane(commandsTable));
        Insets insets = this.getInsets();
        commandsPanel.setBounds(insets.left + 305, insets.top + 5, 290, 380);
        add(commandsPanel);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Component s = (Component) ae.getSource();

        if (s == doneButton) {
            this.setVisible(false);
        }
        else {
            this.setVisible(false);
        }
    }

    private class BatchModel extends AbstractTableModel {

        private BatchFile[] batchFiles;

        public BatchModel() {
            batchFiles = DatabaseConn.getBatchFiles();
        }

        @Override
        public int getRowCount() {
            return DatabaseConn.batchFileCount();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int x, int y) {

            if (y == 0) {
                return batchFiles[x].Name;
            } else if (y == 1) {
                JButton button = new JButton("Details");
                button.setName(("det_" + ((Integer) batchFiles[x].ID).toString()));
                button.addActionListener(dialog);
                return button;
            } else {
                JButton button = new JButton("Delete");
                button.setName(("del_" + ((Integer) batchFiles[x].ID).toString()));
                return button;
            }
        }

        @Override
        public String getColumnName(int x) {
            if (x == 0) {
                return "Name";
            } else {
                return "";
            }
        }
    }

    private class CommandsModel extends AbstractTableModel {

        public CommandsModel() {
        }

        @Override
        public int getRowCount() {
            return DatabaseConn.commandsCount(selectedFileID);
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int x, int y) {

            return null;
        }

        @Override
        public String getColumnName(int x) {
            if (x == 0) {
                return "Comand";
            } else {
                return "";
            }
        }
    }

    class ButtonRenderer  implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return (Component)value;
           
        }
    }
}
