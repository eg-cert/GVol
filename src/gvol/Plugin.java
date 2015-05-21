package gvol;

public class Plugin {
    private final int [] options; //will be deleted
    private final Option [] _options;
    private final String name;
    private final int optionsCount;
    private final int ID;
    
    
    public Plugin(int id,String name,int optionsCount){
        this.name = name;
        this.optionsCount = optionsCount;
        options=new int[optionsCount];
        _options = new Option[optionsCount];
        for(int i=0;i<optionsCount;i++)
            options[i]=-1;
        
        ID = id;
    }
    
    public Plugin(int id, String name, Option[] ops){
        this.name = name;
        this.ID = id;
        _options = ops;
        if(ops==null){
            this.optionsCount = 0;
        }
        else this.optionsCount = ops.length;
        options = new int[2];
    }
    
    public void addOption(int op){
        for(int i=0;i<optionsCount;i++)
            if(options[i] == -1) {
                options[i] = op;
                return;
            }
    }
    
    public int Count(){
        return optionsCount;
    }
    
    public int getOption(int index){
        if(index < 0 || index >= optionsCount) return -1;
        else return options[index];
    }
    
    public String getName(){
        return name;
    }

    int getID() {
        return ID;
    }
}
