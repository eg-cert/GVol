package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import database.DatabaseConn;

public class MainClass {

    

    public static void main(String[] args) {
        
        DatabaseConn.init();
     
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                JFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        
        });
    }

}
