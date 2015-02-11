package gvol;

import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
    
    private final String volCommand;
    private final Plugin[] plugins;
    private final Option[] options;
    private final Profile[] profiles;
    private CommandExecuter commandExecuter;
    private PluginsPanel pluginsPanel;
    private OptionsPanel optionsPanel;
    private OutputPanel outputPanel;
    private final JPanel mainPanel;
    private  volatile boolean isRunning;
    
    public MainFrame(String cmd,Plugin [] plugins,Option [] options, Profile [] profiles){
        super("GVol - A GUI for Volatility memory forensics tool");
        this.volCommand = cmd;
        this.plugins = plugins;
        this.options = options;
        this.profiles  = profiles;
        isRunning = false;
        setSize(820,650);
        this.setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mainPanel= new JPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(null);
        
        initOptionsPanel();
        initPluginsPanel();
        initOutputPanel();
        
        
    }

    private void initPluginsPanel() {
        pluginsPanel = new PluginsPanel(plugins,profiles,new PluginPanelCom());
        Insets insets = mainPanel.getInsets();
        pluginsPanel.setBounds(insets.left+5, insets.top+5, 400, 450);
        mainPanel.add(pluginsPanel);
    }

    private void initOptionsPanel() {
        optionsPanel = new OptionsPanel(options);
        Insets insets = mainPanel.getInsets();
        optionsPanel.setBounds(insets.left+410, insets.top+5, 400, 450);
        mainPanel.add(optionsPanel);
    }

    private void initOutputPanel() {
        outputPanel = new OutputPanel();
        Insets insets = mainPanel.getInsets();
        outputPanel.setBounds(insets.left+5, insets.top+460, 805, 150);
        mainPanel.add(outputPanel);
    }
    
    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
    
    private boolean confirmClose(){
        String msg = "Are you sure you want to stop the current command?";
        int n = JOptionPane.showConfirmDialog(this,msg,"Confirm",JOptionPane.YES_NO_OPTION);
        return (n==0);
    }
    
    class PluginPanelCom implements ComLayerWithPluginPanel{

        @Override
        public void buttonClicked() {
            
            if(isRunning ){
                if(confirmClose()){
                    commandExecuter.setStop();
                }
                return;
            }
            if(!optionsPanel.hasValidValues()){
                showMessage("you must enter values for all selected options.");
                return;
            }
            else if(!pluginsPanel.hasValidValues()){
                showMessage("you must specify an input image file");
                return;
            }
            
            isRunning = true;
            String cmd = volCommand+" "+pluginsPanel.getComand()+" "+optionsPanel.getComand();
            commandExecuter = new CommandExecuter(cmd,new CommandExecuterCom());
            Thread t = new Thread(commandExecuter);
            t.start();
            pluginsPanel.setButtonText("Stop Execution");
        }

        @Override
        public void listIndexChanged(int ind) {
            int [] arr = new int[plugins[ind-1].Count()];
            for(int i=0;i<plugins[ind-1].Count();i++){
                arr[i] = plugins[ind-1].getOption(i);
            }
            optionsPanel.updateVisibleOptions(arr);
            revalidate();
            repaint();
            
        }
        
    }
    
    class CommandExecuterCom implements ComLayerWithThread{

        @Override
        public void addToConsole(String line) {
            outputPanel.appendText(line+"\n");
        }

        @Override
        public void threadClosed() {
            isRunning = false;
            pluginsPanel.setButtonText("Run Command");
        }
        
    }
}
