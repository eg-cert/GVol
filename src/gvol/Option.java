/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gvol;

/**
 *
 * @author Shawkey
 */


public class Option {
    OptionValueType val;
    String cmd;
    String Desc;
    
    public Option(OptionValueType val,String cmd) {
        this.val = val;
        this.cmd = cmd;
        this.Desc = "";
    }
    
    public Option(OptionValueType val,String cmd,String Desc) {
        this.val = val;
        this.cmd = cmd;
        this.Desc = Desc;
    }
    
    public OptionValueType getValueType(){
        return val;
    }
    
    public String getCmd(){
        return cmd;
    }
    
    public String getDesc(){
        return Desc;
    }
}
