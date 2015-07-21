package database;


public class Profile {
    
    private final String desc;
    private final String name;
    private final int ID;
    
    public Profile(String name, String desc) {
        this.name = name;
        this.desc = desc;
        ID = 0;
    }
    
    public Profile(int ID, String name, String desc){
        this.name = name;
        this.desc = desc;
        this.ID = ID;
    }
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return desc;
    }
    
    public int getID(){
        return ID;
    }
}
