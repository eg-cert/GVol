/**
 *
 * @author Mohamad Shawkey
 */
package gvol;

public class Command {
    private final int ID;
    private final int batchFileID;
    private final String text;
    
    public Command(int ID,String Cmd,int batchFileID){
        this.ID = ID;
        this.text = Cmd;
        this.batchFileID  = batchFileID;
    }
    
    public int getID(){
        return ID;
    }
    
    public int getBatchFileID(){
        return batchFileID;
    }
    
    public String getCmd(){
        return text;
    }
}
