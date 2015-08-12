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
        
         String tip = "<html>" + name + "<br>";
         tip = tip + String.format("<html><div WIDTH=%d>%s</div><html>", 400,((desc==null)?"":desc)  + "</html>");
                    
        return tip;
    }
}
