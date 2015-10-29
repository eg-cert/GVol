/**
 *
 * @author Mohamad Shawkey
 * @since Sept, 15th, 2015
 */
package diff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JDiff {

    enum LineState {
        NOCHANGE, DELETED, ADDED, MODIFIED      
    }
    
    class Res {

        public int ind;
        public LineState state;
        public int ind2;
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: JDiff.jar first.txt second.txt output.txt");
            System.err.println(args[0] + " " + args[1] + "  length = " + args.length);
        }
        try {
            JDiff jDiff = new JDiff(args[0], args[1]);
            jDiff.getSimpleOutputSep(args[2]);
            jDiff.getFormattedOutputSep(args[2] + ".html");
            System.out.println("Done :)");
        } catch (Exception ex) {
            System.err.println("Error happened!");
            System.err.println(ex.getMessage());
        }
    }

    private final List<String> beforeLines;
    private final List<String> afterLines;
    private final List<Res> output;
    private boolean resultsReady;

    public JDiff(String beforeFile, String afterFile) throws IOException {
        File f = new File(beforeFile);
        beforeLines = Files.readAllLines(f.toPath(), Charset.defaultCharset());

        f = new File(afterFile);
        afterLines = Files.readAllLines(f.toPath(), Charset.defaultCharset());

        

        output = new ArrayList<Res>();
        resultsReady = false;
    }

    // this function needs to be refactored and split into several smaller functions
    private void getDiff() {
        if(resultsReady) return;
        List <String> before = preFormatLines(beforeLines);
        List <String> after = preFormatLines(afterLines);
        List <Line> orig = new ArrayList<Line>();
        // this map maps a line in the first file to its index 
        // or indicies if it exists more than once
        HashMap<String, ArrayList<Integer> > mp = new HashMap<String, ArrayList<Integer> >();
        ArrayList<Integer> al ;
        // a list of the new added lines in the second file 
        // which doesn't exist in the first line 
        List<Line> added = new ArrayList<Line>();
        
        // initialize the map 
        for (int i = 0; i < before.size(); i++) {
            Line li = new Line(before.get(i));
            li.setRemoved(true);
            li.setIndex(i);
            orig.add(li);
            if(mp.containsKey(before.get(i))){
                al = mp.get(before.get(i));
                al.add(i);
            }
            else{
                al = new ArrayList<Integer>();
                al.add(i);
                mp.put(before.get(i), al);
            }
        }
        
        processDiff(after, before, orig, added, mp);
        
        resultsReady = true;
        
        prepareResult(orig, added);
    }
    
    private void processDiff(List<String> after, List <String> before, List<Line>orig, List <Line> added, HashMap<String,ArrayList<Integer> > mp){
        
        // determine what lines exist in the second files, 
        // the deleted lines, and the added lines 
         ArrayList<Integer> al ;
        for (int i = 0; i < after.size(); i++) {
            Line li;
            if (mp.containsKey(after.get(i))) {
                al = mp.get(after.get(i));
                int index = -1;
                for(int j=0;j<al.size();j++){
                    if(orig.get(al.get(j)).getRemoved()){
                        index = al.get(j);
                    }
                }
                if (index != -1) {
                    orig.get(index).setRemoved(false);
                } else {
                    li = new Line(after.get(i));
                    li.setIndex(i);
                    added.add(li);
                }
            } else {
                li = new Line(after.get(i));
                li.setIndex(i);
                added.add(li);
            }
        }
        // add a step here to find the modified lines 
        for(int i=0;i<orig.size();i++){
            if(!orig.get(i).getRemoved()) continue;
            for(int j=0;j<added.size();j++){
                if(isModified(orig.get(i).getLine(), added.get(j).getLine())){
                    orig.get(i).setModified(added.get(j).getIndex());
                    added.remove(j);
                    break;
                }
            }
        }
    }
    
    private void prepareResult(List <Line>orig,List <Line> added){
        // create a list of lines for the output
        // to handle the order of lines 
        int i, j;
        i = j = 0;
        Res res;
        while (i < orig.size() || j < added.size()) {
            res = new Res();
            if (i >= orig.size() || (j< added.size() && orig.get(i).getIndex() > added.get(j).getIndex())) {
                res.state = LineState.ADDED;
                res.ind = added.get(j).getIndex();
                j++;
            } else{
                if(orig.get(i).getModified() >=0) {
                    res.state = LineState.MODIFIED;
                    res.ind2 = orig.get(i).getModified();
                }
                else res.state = (orig.get(i).getRemoved() ? LineState.DELETED : LineState.NOCHANGE);
                res.ind = orig.get(i).getIndex();
                i++;
            } 
            output.add(res);
        }
    }
    
    public void getSimpleOutputSep(String outputFile)throws FileNotFoundException, IOException {
        if (!resultsReady) {
            getDiff();
        }
        LineState [] states = {LineState.NOCHANGE, LineState.DELETED, LineState.MODIFIED, LineState.ADDED};
        Writer fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
        for(int j=0;j<states.length;j++){
            for (int i = 0; i < output.size(); i++) {
                if(output.get(i).state != states[j]) continue;
                if(output.get(i).state == LineState.NOCHANGE ){
                    fout.write(" "+beforeLines.get(output.get(i).ind)+"\n");
                }
                else if(output.get(i).state == LineState.DELETED){
                    fout.write("-"+beforeLines.get(output.get(i).ind)+"\n");
                }
                else if (output.get(i).state == LineState.ADDED){
                    fout.write("+"+afterLines.get(output.get(i).ind)+"\n");
                }
                else if(output.get(i).state == LineState.MODIFIED){
                    fout.write("!"+beforeLines.get(output.get(i).ind)+"\n");
                    fout.write("!"+afterLines.get(output.get(i).ind2)+"\n");
                }
            }
        }
        fout.flush();
        fout.close();
    }

    public void getSimpleOutput(String outputFile) throws FileNotFoundException, IOException {
        if (!resultsReady) {
            getDiff();
        }

        Writer fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
        for (int i = 0; i < output.size(); i++) {
            if(output.get(i).state == LineState.NOCHANGE ){
                fout.write(" "+beforeLines.get(output.get(i).ind)+"\n");
            }
            else if(output.get(i).state == LineState.DELETED){
                fout.write("-"+beforeLines.get(output.get(i).ind)+"\n");
            }
            else if (output.get(i).state == LineState.ADDED){
                fout.write("+"+afterLines.get(output.get(i).ind)+"\n");
            }
            else if(output.get(i).state == LineState.MODIFIED){
                fout.write("!"+beforeLines.get(output.get(i).ind)+"\n");
                fout.write("!"+afterLines.get(output.get(i).ind2)+"\n");
            }
        }
        fout.flush();
        fout.close();
    }

     public void getFormattedOutputSep(String outputFile) throws FileNotFoundException, IOException {
        if (!resultsReady) {
            getDiff();
        }
        LineState [] states = {LineState.NOCHANGE, LineState.DELETED, LineState.MODIFIED, LineState.ADDED};
        
        Writer fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
        for(int j=0;j<states.length;j++){
            for (int i = 0; i < output.size(); i++) {
                if(output.get(i).state != states[j]) continue;
                if(output.get(i).state == LineState.NOCHANGE ){
                    fout.write("<div>");
                    fout.write(beforeLines.get(output.get(i).ind)+ "</div>\n");
                }
                else if(output.get(i).state == LineState.DELETED){
                    fout.write("<div style=\"background-color:#FFC7CE;\">");
                    fout.write(beforeLines.get(output.get(i).ind) +"</div>\n");
                }
                else if(output.get(i).state == LineState.ADDED){
                    fout.write("<div style=\"background-color:#C6EFCE;\">");
                    fout.write(afterLines.get(output.get(i).ind)+ "</div>\n");
                }
                else if(output.get(i).state == LineState.MODIFIED){
                    fout.write("<div style=\"background-color:#FFEB9C;\">");
                    fout.write(beforeLines.get(output.get(i).ind) +"</div>\n");
                    fout.write("<div style=\"background-color:#FFEB9C;\">");
                    fout.write(afterLines.get(output.get(i).ind2)+ "</div>\n");
                }
            }
        }
        fout.flush();
        fout.close();
    }
  
    public void getFormattedOutput(String outputFile) throws FileNotFoundException, IOException {
        if (!resultsReady) {
            getDiff();
        }
        Writer fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
        for (int i = 0; i < output.size(); i++) {
            if(output.get(i).state == LineState.NOCHANGE ){
                fout.write("<div>");
                fout.write(beforeLines.get(output.get(i).ind)+ "</div>\n");
            }
            else if(output.get(i).state == LineState.DELETED){
                fout.write("<div style=\"background-color:#FFC7CE;\">");
                fout.write(beforeLines.get(output.get(i).ind) +"</div>\n");
            }
            else if(output.get(i).state == LineState.ADDED){
                fout.write("<div style=\"background-color:#C6EFCE;\">");
                fout.write(afterLines.get(output.get(i).ind)+ "</div>\n");
            }
            else if(output.get(i).state == LineState.MODIFIED){
                fout.write("<div style=\"background-color:#FFEB9C;\">");
                fout.write(beforeLines.get(output.get(i).ind) +"</div>\n");
                fout.write("<div style=\"background-color:#FFEB9C;\">");
                fout.write(afterLines.get(output.get(i).ind2)+ "</div>\n");
            }
        }
        fout.flush();
        fout.close();
    }

    private List<String> preFormatLines(List<String> lines) {
        List<String> res = new ArrayList<String>();
        for (int i = 0; i < lines.size(); i++) {
            StringBuilder sb = new StringBuilder(lines.get(i).trim());
            if (isNullOrWhitespace(lines.get(i))) {
                lines.remove(i);
                i--;
            } else {
                for (int j = 0; j < sb.length() - 1; j++) {
                    if (Character.isWhitespace(sb.charAt(j))) {
                        sb.setCharAt(j, ' ');
                    }

                    if (Character.isWhitespace(sb.charAt(j)) && Character.isWhitespace(sb.charAt(j + 1))) {
                        sb.deleteCharAt(j);
                        j--;
                    }
                }
                res.add(sb.toString());
            }
        }
        return res;
    }

    private boolean isNullOrWhitespace(String str) {
        return (str == null || str.isEmpty() || str.trim().isEmpty());
    }
    
    private boolean isModified(String bLine, String aLine){
        if(bLine == null || aLine == null){
            return false;
        }
        
        int pre, post;
        pre = 0;
        post = 0;
        
        for(int i =0 ;i<Math.min(bLine.length(), aLine.length());i++){
            if(bLine.charAt(i) == aLine.charAt(i)) pre++;
            else break;  
        }
        
        for(int i = Math.min(bLine.length(), aLine.length())-1;i>=pre; i--){
            if(bLine.charAt(i) == aLine.charAt(i)) post++;
            else break; 
        }
        
        int mod = Math.max(bLine.length()-pre-post, aLine.length()-pre-post);
        return (mod <= (pre+post)/3); 
    }
            
}
