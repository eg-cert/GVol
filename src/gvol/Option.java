package gvol;

/**
 *
 * @author Shawkey
 */


public class Option {
    private OptionValueType valType;
    private String cmd;
    private String desc;
    private String value;
    
    public Option(OptionValueType valType,String cmd) {
        this.valType = valType;
        this.cmd = cmd;
        this.desc = "";
        this.value = "";
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
    
    public void setValue(String value){
        this.value = value;
    }
    
    public String getValue(){
        return value;
    }
}
