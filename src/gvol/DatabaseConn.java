package gvol;

import java.awt.GridBagConstraints;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.sqlite.SQLiteConfig;

public class DatabaseConn {

    private static Connection c;

    public static boolean init() {
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            c = DriverManager.getConnection("jdbc:sqlite:GVol.db", config.toProperties());
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public static int commandsCount(int batchFileID) {
        return getCount("command","where batchfileid = " + ((Integer) batchFileID).toString());
    }

    public static int profilesCount() {
        return getCount("profile",null);
    }

    public static int batchFileCount() {
        return getCount("batchfile",null);
    }

    public static int optionsCount() {
        return getCount("option",null);
    }
    
    private static int pluginsCount() {
        return getCount("plugin",null);
    }
    
    private static int pluginOptionsCount(int pluginID){
        return getCount("pluginOption","where pluginID = "+((Integer)pluginID).toString());
    }
    
    private static int getCount(String tableName, String where) {
        String sql = "select count(*) from " + tableName +" "+ ((where==null)?"":where)+";";
        
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("count(*)");
            }
            stmt.close();
        } catch (Exception ex) {
        }
        return 0;
    }
    
    public static BatchFile[] getBatchFiles() {
        BatchFile[] batchFiles = new BatchFile[batchFileCount()];
        String sql = "select * from batchfile order by name;";
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int i = 0;
            while (rs.next()) {
                batchFiles[i] = new BatchFile();
                batchFiles[i].ID = rs.getInt("id");
                batchFiles[i].Name = rs.getString("name");
                i++;
            }
            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return batchFiles;
    }

    public static Profile[] getProfiles() {
        Profile[] profiles = new Profile[profilesCount()];
        String sql = "select * from profile order by name;";
        int ID;
        String name;
        String desc;
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int i = 0;
            while (rs.next()) {
                ID = rs.getInt("id");
                name = rs.getString("name");
                desc = rs.getString("desc");
                profiles[i] = new Profile(ID, name, desc);
                i++;
            }
            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return profiles;
    }

    public static String getVolCommand() {
        String volCommand = null;
        String sql = "select * from volcommand limit 1;";
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                volCommand = rs.getString("cmd");
            } else {
                stmt.close();
                throw new Exception("Couldn't read vol command from database.");
            }
            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return volCommand;

    }

    public static Option[] getOptions() {
        Option[] options = new Option[optionsCount()];
        String sql = "select * from option order by name;";
        int ID;
        String name;
        String desc;
        String type;
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int i = 0;
            while (rs.next()) {
                ID = rs.getInt("id");
                name = rs.getString("name");
                desc = rs.getString("desc");
                type = rs.getString("type");
                options[i] = new Option(ID, OptionValueType.valueOf(type), name, desc);
                i++;
            }
            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return options;
    }

    private static Option getOption(int optionID){
        Option option = null;
        String sql = "select * from option where ID = ";
        sql += ((Integer)optionID).toString() + " order by name;";
        int ID;
        String name;
        String desc;
        String type;
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                ID = rs.getInt("id");
                name = rs.getString("name");
                desc = rs.getString("desc");
                type = rs.getString("type");
                option = new Option(ID, OptionValueType.valueOf(type), name, desc);
                
            }
            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return option;
    }
    
    public static Option[] getPluginOptions(int PluginID){
        Option[] options = new Option[pluginOptionsCount(PluginID)];
        String sql = "select * from pluginoption ";
        sql += "where pluginID = " +((Integer)PluginID).toString()+";";
        
        int optionID;
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int i = 0;
            while (rs.next()) {
                optionID = rs.getInt("optionID");
                options[i] = getOption(optionID);
                i++;
            }
            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return options;
    }
    
    public static Plugin[] getPlugins() {
       Plugin [] plugins = new Plugin[pluginsCount()];
       String sql = "select * from plugin order by name;";
        int ID;
        String name;
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int i = 0;
            while (rs.next()) {
                ID = rs.getInt("id");
                name = rs.getString("name");
                Option[] options = getPluginOptions(ID);
                plugins[i] = new Plugin(ID,  name, options);
                i++;
            }
            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
       return plugins;
    }
    
    public static void setVolCommand(String newCmd) {
        String sql = "delete from volcommand;";
        try {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
            sql = "insert into volcommand (cmd) values('" + newCmd + "');";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void addProfile(Profile p) {
        String sql = "insert into profile (name,[desc]) values('" + p.getName() + "'";
        sql = sql + ",'" + p.getDescription() + "');";
        try {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);

            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void addOption(Option op) {
        String sql = "insert into option (name,[desc],[type]) values('" + op.getCmd() + "'";
        sql = sql + ",'" + op.getDesc() + "','"+op.getValueType().toString()+"');";
        try {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);

            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void addPlugin(Plugin p) {
        String sql = "insert into plugin (name) values('" + p.getName()+ "');";
       
        try {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);

            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
     
    public static void addPluginOption(int pluginID, int optionID) {
         String sql = "insert into pluginOption (pluginID,optionID) values(" + ((Integer)pluginID).toString()+ ",";
         sql += ((Integer) optionID).toString()+");";
        try {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);

            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public static void deleteProfile(int ID) {
        String sql = "delete from profile where id =" + ((Integer) ID).toString();
        try {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);

            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void deleteOption(int ID) {
        String sql = "delete from option where id =" + ((Integer) ID).toString();
        try {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);

            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void deletePlugin(int pluginID) {
        String [] sql= new String[2];
        sql[0]= "delete from plugin where id = " + ((Integer)pluginID).toString();
        sql[1] = "delete from PluginOption where PluginID = "+ ((Integer)pluginID).toString();
        
        for(int i=1;i>=0;i--){
            try {
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql[i]);

                stmt.close();
            } catch (Exception ex) {
                System.out.println("delete Plugin: "+ex.getMessage());
            }
        }
    }
       
    public static void deletePluginOption(int pluginID, int optionID) {
        String sql = "delete from PluginOption where PluginID =";
        sql += ((Integer) pluginID).toString();
        sql += " and optionID = " +((Integer)optionID).toString();
        try {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);

            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public static boolean profileExists(String name) {
        String sql = "select * from profile where name ='" + name.trim() + "';";
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }
    
    public static boolean pluginExists(String pluginName) {
        String sql = "select * from plugin where name ='" + pluginName.trim() + "';";
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public static boolean pluginOptionExists(int pluginID, int optionID) {
       String sql = "select * from pluginOption where pluginID =";
       sql += ((Integer)pluginID).toString();
       sql += " and optionID = "+ ((Integer) optionID).toString();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    

    

}
