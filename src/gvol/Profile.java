package gvol;

/**
 *
 * @author Shawkey
 */
public class Profile {
    private String name,Desc;
    
    public Profile(String name, String Desc) {
        this.name = name;
        this.Desc = Desc;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDescription() {
        return Desc;
    }
}
