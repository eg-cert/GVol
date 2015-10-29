package main;

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
import iface.*;
import dialog.*;
import database.*;
import javax.swing.ToolTipManager;

public class MainFrame extends JFrame implements ActionListener {

    private String [] volCommand;
    private final ArrayList<CommandExecuter> commandExecuter;
    private final ArrayList<OutputStreamWriter[]> outputFiles;
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
    private final JMenuItem batchMenuItem;
    private final JMenuItem exitMenuItem;
    private final JMenu configMenu;
    private final JMenuItem cmdAndProfilesMenuItem;
    private final JMenuItem optionsMenuItem;
    private final JMenuItem pluginsMenuItem;
    private final SubWindowAdapter windowAdapter;

    public MainFrame() {
        super("GVol - A GUI for Volatility Memory Forensics Framework");
        this.volCommand = updateVolCommand();//DatabaseConn.getVolCommand();

        this.ids = 0;
        this.commandExecuter = new ArrayList<CommandExecuter>();
        this.outputFiles = new ArrayList<OutputStreamWriter[]>();
        setSize(920, 720);
        this.setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        frame = this;
        windowAdapter = new SubWindowAdapter();
        addWindowListener(windowAdapter);

        mainPanel = new JPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(null);

        menuBar = new JMenuBar();
        fileMenu = new JMenu();
        configMenu = new JMenu();

        batchMenuItem = new JMenuItem();
        exitMenuItem = new JMenuItem();
        cmdAndProfilesMenuItem = new JMenuItem();
        optionsMenuItem = new JMenuItem();
        pluginsMenuItem = new JMenuItem();

        batchFileDialog = new BatchFileDialog(this);
        optionsDialog = new OptionsDialog(this);
        pluginsDialog = new PluginsDialog(this);
        profilesDialog = new ProfilesDialog(this);

        ToolTipManager.sharedInstance().setDismissDelay(600000);

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
        pluginsPanel.setBounds(insets.left + 5, insets.top + 5, 450, 450);
        mainPanel.add(pluginsPanel);
    }

    private void initOptionsPanel() {
        optionsPanel = new OptionsPanel();
        Insets insets = mainPanel.getInsets();
        optionsPanel.setBounds(insets.left + 460, insets.top + 5, 450, 450);
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
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(this);
        fileMenu.add(batchMenuItem);
        fileMenu.add(exitMenuItem);

        configMenu.setText("Configuration");

        cmdAndProfilesMenuItem.setText("Command & Profiles...");
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
        if (source == exitMenuItem) {
            windowAdapter.windowClosing(null);
            return;
        } else if (source == batchMenuItem) {
            batchFileDialog.setVisible(true);
        } else if (source == cmdAndProfilesMenuItem) {
            profilesDialog.setVisible(true);
        } else if (source == optionsMenuItem) {
            optionsDialog.setVisible(true);
        } else if (source == pluginsMenuItem) {
            pluginsDialog.setVisible(true);
        }
        pluginsPanel.updateComponents();
        volCommand = updateVolCommand();
    }

    private String[] updateVolCommand() {
        String [] newCmd = null;
        String cmd = DatabaseConn.getVolCommand();
        if(cmd.length()>7){
            String st  = cmd.substring(0, 7);
            st = st.toLowerCase();
            File f = new File(cmd);
            
            if(st.compareTo("python ")==0 && !f.exists()){
                newCmd = new String[2];
                newCmd[0] = "python";
                newCmd[1] = cmd.substring(7);
            }
            else{
                newCmd = new String[1];
                newCmd[0] = cmd;
            }
        }
        else{
            newCmd = new String[1];
            newCmd[0] = cmd;
        }
        return newCmd;
    }

    class PluginPanelCom implements ComLayerWithPluginPanel {

        @Override
        public void buttonClicked() {
            if (!optionsPanel.hasValidValues()) {
                showMessage("you must enter values for all selected options.");
                return;
            } else if (!pluginsPanel.hasValidValues()) {
                String msg = "you must specify an input image file or output directory.\n";
                msg += "If you selected a second memory image, you have to choose an output directory.";
                showMessage(msg);
                return;
            } else if (pluginsPanel.runBatchFile() > 0 && DatabaseConn.batchFilePluginCount(pluginsPanel.runBatchFile()) < 1) {
                showMessage("this batch file has no commands.");
                return;
            }

            if (pluginsPanel.runBatchFile() > 0) {
                if(!runBatchFile()) return;
            } else {
                runCommand();
            }

            Thread t = new Thread(commandExecuter.get(commandExecuter.size() - 1));
            t.start();
        }

        @Override
        public void listIndexChanged(int pluginID) {

            optionsPanel.updateVisibleOptions(pluginID);
            revalidate();
            repaint();

        }

        private String getNewFileName(String dir, String name){
            try {
                String fPath = dir + File.separator + name;
                Integer i = 0;
                while (Files.exists(Paths.get(fPath + i.toString() + ".txt"))) {
                    i++;
                }
                fPath = fPath + i.toString() + ".txt";
                return fPath;
            } catch (Exception ex) {
                return null;
            }
        }
        
        private OutputStreamWriter getOutputStream(String filePath) {
            try {
                OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filePath));
                return out;
            } catch (Exception ex) {
                System.err.println("failed to getOutputStream fpath = "+filePath);
                return null;
            }
        }

        private void runCommand() {
           
            OutputStreamWriter[] out = null;
            
            int count;
            String [][] diff = null;
            if(pluginsPanel.shouldWriteToFile()){
                out = new OutputStreamWriter[1];
            }
            if(pluginsPanel.shouldRunDiff()){
                count = 2;
                diff = new String[1][2];
                out = new OutputStreamWriter[2];
            }
            else count=1;
            
            String [][] cmd = new String[count][];//[1+volCommand.length+pluginCommands.length+optionCommands.length];
            
            for(int j=0;j<count;j++){
                String [] pluginCommands = pluginsPanel.getCommand(j);
                String [] optionCommands = optionsPanel.getCommand();
                cmd[j] = new String[1+volCommand.length+pluginCommands.length+optionCommands.length];
                int prev =0;
                for(int i = 0;i<volCommand.length;i++) cmd[j][i] = volCommand[i];
                prev += volCommand.length;
                for(int i = 0;i<pluginCommands.length;i++) cmd[j][i + prev] = pluginCommands[i];
                prev += pluginCommands.length;
                cmd[j][prev++] = pluginsPanel.getPluginName();
                for(int i=0;i<optionCommands.length;i++) cmd[j][i+prev] = optionCommands[i];

                if (pluginsPanel.shouldWriteToFile()) {
                    String filePath = getNewFileName(pluginsPanel.getOutputDir(), pluginsPanel.getFileName(j) + "-" + pluginsPanel.getPluginName() + "-output");
                    out[j] = getOutputStream(filePath);
                    if(pluginsPanel.shouldRunDiff()){
                        diff[0][j] = filePath;
                    }
                }
            }
            outputPanel.addNewTextArea();
            outputFiles.add(out);
            commandExecuter.add(new CommandExecuter(cmd, new CommandExecuterCom(), ids++,diff));

        }

        private boolean runBatchFile() {
            String[][] cmd = null;
            String[][] diff = null;
            OutputStreamWriter[] out = null;

            int batchFileID = pluginsPanel.runBatchFile();
            BatchFileWizardDialog batchFileWizard = new BatchFileWizardDialog(frame, batchFileID);
            batchFileWizard.setVisible(true);
            while (batchFileWizard.isVisible()) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                }
            }
            if (!batchFileWizard.isReady()) {
                return false;
            }
            
            int count;
            String [][] str = batchFileWizard.getCommands();
            cmd = new String[str.length][];
            if(pluginsPanel.shouldWriteToFile())
                out = new OutputStreamWriter[str.length];
            
            if(pluginsPanel.shouldRunDiff()){
                count = 2;
                diff = new String[str.length][2];
                cmd = new String[str.length*2][];
                
                out = new OutputStreamWriter[str.length*2];
            }
            else count=1;
            
            
            for(int k=0;k<count;k++){
                for (int i = 0; i < str.length; i++) {
                    String [] pluginCommands = pluginsPanel.getCommand(k);
                    String [] optionCommands = str[i];

                    cmd[i+(k*str.length)] = new String[volCommand.length + pluginCommands.length+optionCommands.length];
                    int prev = 0;
                    for(int j=0;j<volCommand.length;j++) cmd[i+(k*str.length)][j] = volCommand[j];
                    prev+=volCommand.length;
                    for(int j = 0;j<pluginCommands.length;j++) cmd[i+(k*str.length)][j+prev] = pluginCommands[j];
                    prev+=pluginCommands.length;
                    for(int j=0;j<optionCommands.length;j++) cmd[i+(k*str.length)][j+prev] = optionCommands[j];
                

                    if (pluginsPanel.shouldWriteToFile()) {
                        BatchFile batchFile = DatabaseConn.getBatchFile(batchFileID);
                        String[] plugins = batchFileWizard.getPluginNames();
                        String filePath = getNewFileName(pluginsPanel.getOutputDir(), pluginsPanel.getFileName(k) + "-" + batchFile.getName()
                                + "-" + plugins[i] + "-output");
                        out[i+(k*str.length)] = getOutputStream(filePath);
                        if(pluginsPanel.shouldRunDiff()){
                            diff[i][k] = filePath;
                        }
                    }
                }
            }
            outputPanel.addNewTextArea();
            outputFiles.add(out);
            commandExecuter.add(new CommandExecuter(cmd, new CommandExecuterCom(), ids++,diff));
            
            return true;
        }
    }

    class CommandExecuterCom implements ComLayerWithThread {

        @Override
        public void addToConsole(String line, int id, int ind) {

            outputPanel.appendText(line + "\n", id);

            if (outputFiles.get(id) != null) {
                try {
                    outputFiles.get(id)[ind].write(line + "\r\n");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }

        @Override
        public void threadClosed(int id) {
            try {
                OutputStreamWriter[] osw = outputFiles.get(id);
                for (OutputStreamWriter o : osw) {
                    o.flush();
                    o.close();
                }
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
                    //System.err.print("YES");
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
