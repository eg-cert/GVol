package gvol;

import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.JFrame;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class MainClass {

    static String volCommand;
    static Plugin[] plugins;
    static Option[] options;
    static Profile[] profiles;
    static final String configFile = "GVol.confg";

    public static void main(String[] args) {
        String res = readConfiguration();

        if (res.compareTo("TRUE") != 0) {
            showMessage(res);
            return;
        }
        DatabaseConn.init();
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                JFrame frame = new MainFrame(volCommand, plugins, options, profiles);
                frame.setVisible(true);
            }
        
        });
    }

    private static String readConfiguration() {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(configFile),Charset.defaultCharset());
        } catch (IOException ex) {
            return "Cannot read configuration file.";
        }
        //read the volatility command 
        int it = 0;
        it = readCommand(it, lines);
        if (it == -1) {
            return "Error in configuration file: cannot find program command.";
        }

        //read profiles
        it = readProfiles(it, lines);
        if (it == -1) {
            return "Error in configuration file: profiles are not formatted correctly.";
        }
        //read options 
        it = readOptions(it, lines);
        if (it == -1) {
            return "Error in configuration file: options are not formatted correctly.";
        }
        //read plugins
        it = readPlugins(it, lines);
        if (it == -1) {
            return "Error in configuration file: plugins are not formatted correctly.";
        }
        
        return "TRUE";
    }

    private static void showMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    private static int readCommand(int it, List<String> lines) {
        boolean read = false;
        while (it < lines.size() && !read) {
            String line = lines.get(it).trim();
            if (line.length() > 0 && line.charAt(0) != '#') {//skip comments and blanklines
                read = true;
                volCommand = line;
            }
            it++;
        }
        if (!read) {
            it = -1;
        }
        return it;
    }

    private static int readProfiles(int it, List<String> lines) {
        int count = -1;
        //read the number of profiles
        while (it < lines.size()) {
            String line = lines.get(it++).trim();
            if (line.length() > 0 && line.charAt(0) != '#') {
                try {
                    count = Integer.parseInt(line);
                } 
                catch (Exception e) {
                }
                break;
            }
        }
        if (count < 1) {
            return -1;
        }
        //read {count} lines 
        profiles = new Profile[count];
        while (it < lines.size() && count > 0) {
            String line = lines.get(it).trim();
            if (line.length() > 0 && line.charAt(0) != '#') {
                count--;
                String [] res = line.split("(\\s)*,(\\s)*", 2);
                if(res.length < 2) return -1;
                profiles[count]=new Profile(res[0],res[1]);
            }
            it++;
        }

        if (count != 0) {
            it = -1;
        }
        return it;
    }

    private static int readOptions(int it, List<String> lines) {
        int count = -1;
        //read the number of Options
        while (it < lines.size()) {
            String line = lines.get(it++).trim();
            if (line.length() > 0 && line.charAt(0) != '#') {
                try {
                    count = Integer.parseInt(line);
                } 
                catch (Exception e) {}
                break;
            }
        }
        if (count < 1) {
            return -1;
        }
        //read {count} lines 
        options = new Option[count];
        int i =0;
        while (it < lines.size() && i < count) {
            String line = lines.get(it).trim();
            if (line.length() > 0 && line.charAt(0) != '#') {
                String [] res = line.split("(\\s)*,(\\s)*", 5);
                if(res.length != 4) return -1;
                try{
                    if(i+1!=Integer.parseInt(res[0]) || res[1].length()<1) return -1;
                    options[i]=new Option(OptionValueType.valueOf(res[3]),res[1],res[2]);
                }
                catch(Exception e){return -1;}
                i++;
            }
            it++;
        }

        if (i != count) {
            it = -1;
        }
        return it;
    }

    private static int readPlugins(int it, List<String> lines) {
        int count = -1;
        //read the number of plugins
        while (it < lines.size()) {
            String line = lines.get(it++).trim();
            if (line.length() > 0 && line.charAt(0) != '#') {
                try {
                    count = Integer.parseInt(line);
                } 
                catch (Exception e) {}
                break;
            }
        }
        if (count < 1) {
            return -1;
        }
        //read {count} lines 
        plugins = new Plugin[count];
        int i =0;
        while (it < lines.size() && i < count) {
            String line = lines.get(it).trim();
            if (line.length() > 0 && line.charAt(0) != '#') {
                String [] res = line.split("(\\s)*,(\\s)*");
                try{
                    int optionsCount = Integer.parseInt(res[1]);
                    if(res[0].length()<1 || optionsCount<0 
                      || optionsCount > options.length || res.length != optionsCount+2) 
                        return -1;
                    
                    plugins[i] = new Plugin(0,res[0],optionsCount);
                    boolean [] taken = new boolean[options.length];
                    for(int j=0;j<options.length;j++) taken[j] = false;
                    for(int j=0;j<optionsCount;j++)
                    {
                        int op = Integer.parseInt(res[j+2]);
                        if(op<1 || op>options.length || taken[op-1]) return -1;
                        taken[op-1]=true;
                        plugins[i].addOption(op);
                    }
                }
                catch(Exception e){return -1;}
                i++;
            }
            it++;
        }

        if (i != count) {
            it = -1;
        }
        return it;
    }
}
