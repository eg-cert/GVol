/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gvol;

import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Shawkey
 */
public class MainFrame extends JFrame {
    
   
    private final String volCommand;
    private final Plugin[] plugins;
    private final Option[] options;
    private final Profile[] profiles;
    
    private JPanel pluginsPanel;
    private JPanel optionsPanel;
    private JPanel outputPanel;
    private JPanel mainPanel;
    
    
    public MainFrame(String cmd,Plugin [] plugins,Option [] options, Profile [] profiles){
        super("GVol - A GUI for Volatility memory forensics tool");
        this.volCommand = cmd;
        this.plugins = plugins;
        this.options = options;
        this.profiles  = profiles;
        setSize(820,650);
        this.setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mainPanel= new JPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(null);
        
        initPluginsPanel();
        initOptionsPanel();
        initOutputPanel();
        
        
        
    }

    private void initPluginsPanel() {
        pluginsPanel = new PluginsPanel(plugins,profiles);
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
    
}
