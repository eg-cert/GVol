/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gvol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Shawkey
 */
class PluginsPanel extends JPanel implements ActionListener{

    

    PluginsPanel(Plugin[] plugins, Profile[] profiles) {
        super();
        setBorder(BorderFactory.createTitledBorder("Plugins"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
