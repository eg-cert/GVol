package gvol;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainFrame extends JFrame implements ActionListener {

    private final String volCommand;
    private final ArrayList<CommandExecuter> commandExecuter;
    private final ArrayList<OutputStreamWriter> outputFiles;
    private int ids;
    final JFrame frame;
    private PluginsPanel pluginsPanel;
    private OptionsPanel optionsPanel;
    private OutputPanel outputPanel;
    private final JPanel mainPanel;
    
    private final BatchFileDialog batchFileDialog;
    private final PluginsDialog pluginsDialog;
    private final OptionsDialog optionsDialog;
    private final ProfilesDialog profilesDialog;
    
    private final JMenuBar menuBar;
    private final JMenu fileMenu;
    private final JMenu configMenu;
    private final JMenuItem batchMenuItem;
    private final JMenuItem cmdAndProfilesMenuItem;
    private final JMenuItem optionsMenuItem;
    private final JMenuItem pluginsMenuItem;
    
    
    public MainFrame() {
        super("GVol - A GUI for Volatility memory forensics tool");
        this.volCommand = DatabaseConn.getVolCommand();
       
        this.ids = 0;
        this.commandExecuter = new ArrayList<CommandExecuter>();
        this.outputFiles = new ArrayList<OutputStreamWriter>();
        setSize(920, 720);
        this.setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame = this;
        addWindowListener(new SubWindowAdapter());

        mainPanel = new JPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(null);

        menuBar = new JMenuBar();
        fileMenu = new JMenu();
        configMenu = new JMenu();
        
        batchMenuItem = new JMenuItem();
        cmdAndProfilesMenuItem = new JMenuItem();
        optionsMenuItem = new JMenuItem();
        pluginsMenuItem = new JMenuItem();
        
        batchFileDialog = new BatchFileDialog(this);
        optionsDialog = new OptionsDialog(this);
        pluginsDialog = new PluginsDialog(this);
        profilesDialog = new ProfilesDialog(this);
        
        initOptionsPanel();
        initPluginsPanel();
        initOutputPanel();
        initMenuBar();
        setJMenuBar(menuBar);
        setLocationRelativeTo(null);
    }

    private void initPluginsPanel() {
        pluginsPanel = new PluginsPanel(new PluginPanelCom());
        Insets insets = mainPanel.getInsets();
        pluginsPanel.setBounds(insets.left + 5, insets.top + 5, 500, 450);
        mainPanel.add(pluginsPanel);
    }

    private void initOptionsPanel() {
        optionsPanel = new OptionsPanel();
        Insets insets = mainPanel.getInsets();
        optionsPanel.setBounds(insets.left + 510, insets.top + 5, 400, 450);
        mainPanel.add(optionsPanel);
    }

    private void initOutputPanel() {
        outputPanel = new OutputPanel(new OutputPanelCom());
        Insets insets = mainPanel.getInsets();
        outputPanel.setBounds(insets.left + 5, insets.top + 460, 905, 200);
        mainPanel.add(outputPanel);
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    private void initMenuBar() {
        fileMenu.setText("File");
        batchMenuItem.setText("Manage Batch Files...");
        batchMenuItem.addActionListener(this);
        fileMenu.add(batchMenuItem);
        
        configMenu.setText("Config.");
        
        cmdAndProfilesMenuItem.setText("Cmd & Profiles...");
        cmdAndProfilesMenuItem.addActionListener(this);
        optionsMenuItem.setText("Volatility Options...");
        optionsMenuItem.addActionListener(this);
        pluginsMenuItem.setText("Plugins...");
        pluginsMenuItem.addActionListener(this);
        configMenu.add(cmdAndProfilesMenuItem);
        configMenu.add(optionsMenuItem);
        configMenu.add(pluginsMenuItem);
        
        menuBar.add(fileMenu);
        menuBar.add(configMenu);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if(source == batchMenuItem)
            batchFileDialog.setVisible(true);
        else if(source == cmdAndProfilesMenuItem) 
            profilesDialog.setVisible(true);
        else if(source == optionsMenuItem)
            optionsDialog.setVisible(true);
        else if(source == pluginsMenuItem)
            pluginsDialog.setVisible(true);
               
    }

    class PluginPanelCom implements ComLayerWithPluginPanel {

        @Override
        public void buttonClicked() {
            if (!optionsPanel.hasValidValues()) {
                showMessage("you must enter values for all selected options.");
                return;
            } else if (!pluginsPanel.hasValidValues()) {
                showMessage("you must specify an input image file or output directory.");
                return;
            }

            String cmd = "cmd /c \"" + volCommand + " " + pluginsPanel.getComand() + " " + optionsPanel.getComand() + "\"";
            commandExecuter.add(new CommandExecuter(cmd, new CommandExecuterCom(), ids++));
            outputPanel.addNewTextArea();
            OutputStreamWriter out = null;
            if (pluginsPanel.shouldWriteToFile()) {
                try {
                    String fPath = pluginsPanel.getOutputDir() + File.separator + pluginsPanel.getFileName();
                    Integer i = 0;
                    while (Files.exists(Paths.get(fPath + i.toString() + ".txt"))) {
                        i++;
                    }
                    fPath = fPath + i.toString() + ".txt";
                    out = new OutputStreamWriter(new FileOutputStream(fPath));
                } catch (Exception ex) {
                }
            }
            outputFiles.add(out);
            Thread t = new Thread(commandExecuter.get(commandExecuter.size() - 1));
            t.start();
        }

        @Override
        public void listIndexChanged(int pluginID) {
            
            optionsPanel.updateVisibleOptions(pluginID);
            revalidate();
            repaint();

        }

    }

    class CommandExecuterCom implements ComLayerWithThread {

        @Override
        public void addToConsole(String line, int id) {

            outputPanel.appendText(line + "\n", id);

            if (outputFiles.get(id) != null) {
                try {
                    outputFiles.get(id).write(line + "\r\n");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        @Override
        public void threadClosed(int id) {
            try {

                outputFiles.get(id).close();
            } catch (Exception ex) {
            }

        }

    }

    class OutputPanelCom implements ComLayerWithOutputPanel {

        @Override
        public boolean tabClosed(int id) {
            if (commandExecuter.get(id).isRunning()) {
                if (confirmClose()) {
                    commandExecuter.get(id).setStop();
                    try {
                        Thread.sleep(300);
                    } catch (Exception e) {
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        private boolean confirmClose() {
            String msg = "Are you sure you want to stop the current command?";
            int n = JOptionPane.showConfirmDialog(frame, msg, "Confirm", JOptionPane.YES_NO_OPTION);
            return (n == 0);
        }
    }

    class SubWindowAdapter extends WindowAdapter {

        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            if (commandsRunning()) {
                if (confirmClose()) {
                    System.out.print("YES");
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        }

        private boolean confirmClose() {
            String msg = "Are you sure you want to stop the running command(s) and exit?";
            int n = JOptionPane.showConfirmDialog(frame, msg, "Confirm", JOptionPane.YES_NO_OPTION);
            return (n == 0);
        }

        private boolean commandsRunning() {
            for (CommandExecuter cmdExec : commandExecuter) {
                if (cmdExec.isRunning()) {
                    return true;
                }
            }
            return false;
        }
    }

}
