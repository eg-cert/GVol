
package diff;


public class Line {
    private int index;
    private boolean removed;
    final private String txt;
    private int modified;
    
    public Line(String txt){
        this.txt = txt ;
        this.modified = -1;
    }
    
    public int getModified(){
        return modified;
    }
    
    public void setModified(int m){
        modified = m;
    }
    
    public boolean getRemoved(){
        return removed;
    }
    
    public void setRemoved(boolean  b){
        removed = b;
    }
    
    public int getIndex(){
        return index;
    }
    
    public void setIndex(int ind){
        index = ind;
    }
    
    public String getLine(){
        return txt;
    }
}
