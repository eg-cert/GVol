package gvol;

import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
    
    private final String volCommand;
    private final Plugin[] plugins;
    private final Option[] options;
    private final Profile[] profiles;
    private final ArrayList<CommandExecuter> commandExecuter;
    private PluginsPanel pluginsPanel;
    private OptionsPanel optionsPanel;
    private OutputPanel outputPanel;
    private final JPanel mainPanel;
    private int ids;
    final JFrame frame;
    
    public MainFrame(String cmd, Plugin[] plugins, Option[] options, Profile[] profiles) {
        super("GVol - A GUI for Volatility memory forensics tool");
        this.volCommand = cmd;
        this.plugins = plugins;
        this.options = options;
        this.profiles = profiles;
        this.ids = 0;
        this.commandExecuter = new ArrayList<>();
        setSize(820, 700);
        this.setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame = this;
        addWindowListener(new SubWindowAdapter());
        
        mainPanel = new JPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(null);
        
        initOptionsPanel();
        initPluginsPanel();
        initOutputPanel();
        
    }
    
    private void initPluginsPanel() {
        pluginsPanel = new PluginsPanel(plugins, profiles, new PluginPanelCom());
        Insets insets = mainPanel.getInsets();
        pluginsPanel.setBounds(insets.left + 5, insets.top + 5, 400, 450);
        mainPanel.add(pluginsPanel);
    }
    
    private void initOptionsPanel() {
        optionsPanel = new OptionsPanel(options);
        Insets insets = mainPanel.getInsets();
        optionsPanel.setBounds(insets.left + 410, insets.top + 5, 400, 450);
        mainPanel.add(optionsPanel);
    }
    
    private void initOutputPanel() {
        outputPanel = new OutputPanel(new OutputPanelCom());
        Insets insets = mainPanel.getInsets();
        outputPanel.setBounds(insets.left + 5, insets.top + 460, 805, 200);
        mainPanel.add(outputPanel);
    }
    
    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
    
    class PluginPanelCom implements ComLayerWithPluginPanel {
        
        @Override
        public void buttonClicked() {
            if (!optionsPanel.hasValidValues()) {
                showMessage("you must enter values for all selected options.");
                return;
            } else if (!pluginsPanel.hasValidValues()) {
                showMessage("you must specify an input image file");
                return;
            }
            
            String cmd = "cmd /c \"" + volCommand + " " + pluginsPanel.getComand() + " " + optionsPanel.getComand() + "\"";
            commandExecuter.add(new CommandExecuter(cmd, new CommandExecuterCom(), ids++));
            outputPanel.addNewTextArea();
            Thread t = new Thread(commandExecuter.get(commandExecuter.size() - 1));
            t.start();
        }
        
        @Override
        public void listIndexChanged(int ind) {
            int[] arr = new int[plugins[ind - 1].Count()];
            for (int i = 0; i < plugins[ind - 1].Count(); i++) {
                arr[i] = plugins[ind - 1].getOption(i);
            }
            optionsPanel.updateVisibleOptions(arr);
            revalidate();
            repaint();
            
        }
        
    }
    
    class CommandExecuterCom implements ComLayerWithThread {
        
        @Override
        public void addToConsole(String line, int id) {
            outputPanel.appendText(line + "\n", id);
        }
        
        @Override
        public void threadClosed(int id) {
            
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
