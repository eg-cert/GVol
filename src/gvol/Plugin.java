package gvol;

public class Plugin {
    
    private final Option [] options;
    private final String name;
    private final int optionsCount;
    private final int ID;
     
    public Plugin(int id, String name, Option[] ops){
        this.name = name;
        this.ID = id;
        options = ops;
        if(ops==null){
            this.optionsCount = 0;
        }
        else this.optionsCount = ops.length;
    }
    
  
    public int Count(){
        return optionsCount;
    }
    
    public Option getOption(int index){
        if(index < 0 || index >= optionsCount) return null;
        else return options[index];
    }
    
    public String getName(){
        return name;
    }

    int getID() {
        return ID;
    }
}
