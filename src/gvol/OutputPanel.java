package gvol;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

class OutputPanel extends JPanel implements ActionListener {

    private final ArrayList<JTextArea> consoleTextArea;
    private final JTabbedPane tabsPane;
    private final JButton closeButton;
    private final ComLayerWithOutputPanel comLayer;
    
    public OutputPanel(ComLayerWithOutputPanel comLayer) {

        super();
        this.comLayer = comLayer;
        setBorder(BorderFactory.createTitledBorder("Console Output"));
        setLayout(new BorderLayout());
        tabsPane = new JTabbedPane();
        consoleTextArea = new ArrayList<>();
        //add(new JScrollPane(consoleTextArea),BorderLayout.CENTER);
        add(tabsPane, BorderLayout.CENTER);
        closeButton = new JButton();
        closeButton.setText("Close Current Tab");
        closeButton.addActionListener(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(closeButton);
        JLabel lb = new JLabel();
        lb.setPreferredSize(new Dimension(600,25));
        panel.add(lb);
        //closeButton.setPreferredSize(new Dimension(200, 25));
        add(panel, BorderLayout.PAGE_END);
        
        
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        return textArea;
    }

    public void appendText(String txt, int id) {
        consoleTextArea.get(id).append(txt);
        
        
    }

    public void clearText(int id) {
        consoleTextArea.get(id).setText("");
        
    }

    public void addNewTextArea() {
        JTextArea textArea = createTextArea();
        consoleTextArea.add(textArea);
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        tabsPane.addTab("Tab " + consoleTextArea.size(), new JScrollPane(textArea));
        tabsPane.setSelectedIndex(tabsPane.getTabCount()- 1);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        int ind = tabsPane.getSelectedIndex();
        if(ind >=0)
        {
            String str = tabsPane.getTitleAt(ind);
            str = str.substring(4);
            int id = Integer.parseInt(str)-1;
            if (comLayer.tabClosed(id)) {
                tabsPane.remove(ind);
            }
            
        }
        
    }

}
