package gvol;

public class Option {
    private final OptionValueType valType;
    private final String cmd;
    private final String desc;
    
    public Option(OptionValueType valType,String cmd) {
        this.valType = valType;
        this.cmd = cmd;
        this.desc = "";
    }
    
    public Option(OptionValueType valType,String cmd,String desc) {
        this.valType = valType;
        this.cmd = cmd;
        this.desc = desc;
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
     
}
