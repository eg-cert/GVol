package gvol;

/**
 *
 * @author Shawkey
 */
public class Plugin {
    private int [] options;
    private String name;
    private int optionsCount;
    
    public Plugin(String name,int optionsCount){
        this.name = name;
        options=new int[optionsCount];
        for(int i=0;i<optionsCount;i++)
            options[i]=-1;
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
}
