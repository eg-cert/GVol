package gvol;


public class Profile {
    
    private final String Desc;
    private final String name;
    
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
