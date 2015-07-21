package database;

public class Plugin {
    
    private final String name;
    private final int ID;
    private final String desc;
    
    public Plugin(int id, String name, String desc){
        this.name = name;
        this.ID = id;
        this.desc = desc;
    }
    
    
    public String getName(){
        return name;
    }

    public int getID() {
        return ID;
    }
    
    public String getDesc(){
        return desc;
    }
    
    public String getTooltip(){
        String tip = ((desc==null)?"":desc);
        tip = "<html>"+name+"<br>"+tip+"</html>";
        return tip;
    }
}
