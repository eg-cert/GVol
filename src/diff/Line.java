
package diff;


public class Line {
    private int index;
    private boolean removed;
    final private String txt;
    
    public Line(String txt){
        this.txt = txt ;
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
