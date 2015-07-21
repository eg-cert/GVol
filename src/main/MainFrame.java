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

public class MainFrame extends JFrame implements ActionListener {

    private String volCommand;
    private final ArrayList<CommandExecuter> commandExecuter;
    private final ArrayList<OutputStreamWriter []> outputFiles;
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
        this.volCommand = DatabaseConn.getVolCommand();
       
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
        if(source == exitMenuItem){
            windowAdapter.windowClosing(null);
            return;
        }
        else if(source == batchMenuItem)
            batchFileDialog.setVisible(true);
        else if(source == cmdAndProfilesMenuItem) 
            profilesDialog.setVisible(true);
        else if(source == optionsMenuItem)
            optionsDialog.setVisible(true);
        else if(source == pluginsMenuItem)
            pluginsDialog.setVisible(true);
        pluginsPanel.updateComponents();
        volCommand = DatabaseConn.getVolCommand();
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
            } else if(pluginsPanel.runBatchFile()>0 && DatabaseConn.batchFilePluginCount(pluginsPanel.runBatchFile())<1){
                showMessage("this batch file has no commands.");
                return;
            }
            
            String [] cmd =null;
            OutputStreamWriter [] out = null;
            
            if(pluginsPanel.runBatchFile()>0){
                int batchFileID = pluginsPanel.runBatchFile();
                BatchFileWizardDialog batchFileWizard = new BatchFileWizardDialog(frame,batchFileID);
                batchFileWizard.setVisible(true);
                while(batchFileWizard.isVisible()){
                    try{
                        Thread.sleep(1000);
                    }
                    catch(Exception ex){}
                }
                if(!batchFileWizard.isReady()) return;
                cmd = batchFileWizard.getCommands();
                for(int i=0;i<cmd.length;i++) cmd[i]= "cmd /c \"" + volCommand + " " + pluginsPanel.getCommand() + " " + cmd[i] + "\"";
                if (pluginsPanel.shouldWriteToFile()) {
                    BatchFile batchFile = DatabaseConn.getBatchFile(batchFileID);
                    String [] plugins = batchFileWizard.getPluginNames();
                    out = new OutputStreamWriter[plugins.length];
                    for(int i=0;i<out.length;i++){
                        out[i] = getOutputStream(pluginsPanel.getOutputDir(),pluginsPanel.getFileName()+"-"+batchFile.getName()
                                +"-"+plugins[i]+"-output");
                    }    
                }
                
            }
            else{
                cmd = new String[1];
                //String cmd = "cmd /c \"" + volCommand + " " + pluginsPanel.getCommand() + " " + optionsPanel.getCommand() + "\"";
                
                cmd[0] = "cmd /c \"" + volCommand + " " + pluginsPanel.getCommand() + " ";
                cmd[0] = cmd[0]+ pluginsPanel.getPluginName() + " " + optionsPanel.getCommand() + "\"";
                
                if (pluginsPanel.shouldWriteToFile()) {
                    out = new OutputStreamWriter[1];
                    out[0] = getOutputStream(pluginsPanel.getOutputDir(),pluginsPanel.getFileName()+"-"+pluginsPanel.getPluginName()+"-output");
                }   
            }
            outputPanel.addNewTextArea();
            outputFiles.add(out);
            commandExecuter.add(new CommandExecuter(cmd, new CommandExecuterCom(), ids++));
            
            Thread t = new Thread(commandExecuter.get(commandExecuter.size() - 1));
            t.start();
        }

        @Override
        public void listIndexChanged(int pluginID) {
            
            optionsPanel.updateVisibleOptions(pluginID);
            revalidate();
            repaint();

        }
        
        private OutputStreamWriter getOutputStream(String dir,String name){
            try{
                String fPath = dir + File.separator + name;
                Integer i = 0;
                while (Files.exists(Paths.get(fPath + i.toString() + ".txt"))) {
                    i++;
                }
                fPath = fPath + i.toString() + ".txt";
                OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fPath));
                return out;
            }
            catch(Exception ex){
                return null;
            }
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
                    System.out.println(e.getMessage());
                }
            }
        }

        @Override
        public void threadClosed(int id) {
            try {
                OutputStreamWriter [] osw = outputFiles.get(id);
                for(OutputStreamWriter o: osw) o.close();
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
                    //System.out.print("YES");
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
