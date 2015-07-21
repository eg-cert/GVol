package database;

public class Option {
    final private OptionValueType valType;
    final private String cmd;
    final private String desc;
    final private int ID;
    
    
    public Option(int ID, OptionValueType valType,String cmd,String desc) {
        this.valType = valType;
        this.cmd = cmd;
        this.desc = desc;
        this.ID = ID;
    }
    public OptionValueType getValueType(){
        return valType;
    }
    
    public String getCmd(){
        return cmd;
    }
    
    public String getDesc(){
        return desc;
    }
     
    public int getID(){
        return ID;
    }
    
   
    
}
