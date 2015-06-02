package gvol;

/**
 *
 * @author Moahamd Shawkey
 */
public class BatchFile {
    private final int ID;
    private final String Name;
    
    public BatchFile(int ID, String Name){
        this.ID = ID;
        this.Name = Name;
    }
    
    public String getName(){
        return Name;
    }
    
    public int getID(){
        return ID;
    }
    
    
}
