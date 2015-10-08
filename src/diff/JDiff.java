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

    class Res {

        public int ind;
        public int change;
    }

    private final List<String> before;
    private final List<String> after;
    private final List<Res> output;
    private boolean resultsReady;

    public JDiff(String beforeFile, String afterFile) throws IOException {
        File f = new File(beforeFile);
        before = Files.readAllLines(f.toPath(), Charset.defaultCharset());

        f = new File(afterFile);
        after = Files.readAllLines(f.toPath(), Charset.defaultCharset());

        preFormatLines(before);
        preFormatLines(after);

        output = new ArrayList<Res>();
        resultsReady = false;
    }

    private void getDiff() {

        List<Line> orig = new ArrayList<Line>();
        HashMap<String, ArrayList<Integer> > mp = new HashMap<String, ArrayList<Integer> >();
        ArrayList<Integer> al ;
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

        List<Line> added = new ArrayList<Line>();

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
        int i, j;
        i = j = 0;
        Res res;
        while (i < orig.size() || j < added.size()) {
            res = new Res();
            if (i >= orig.size()) {
                res.change = 2;
                res.ind = added.get(j).getIndex();
                j++;
            } else if (j >= added.size()) {
                res.change = (orig.get(i).getRemoved() ? 1 : 0);
                res.ind = orig.get(i).getIndex();
                i++;
            } else {
                if (orig.get(i).getIndex() > added.get(j).getIndex()) {
                    res.change = 2;
                    res.ind = added.get(j).getIndex();
                    j++;
                } else {
                    res.change = (orig.get(i).getRemoved() ? 1 : 0);
                    res.ind = orig.get(i).getIndex();
                    i++;
                }
            }
            output.add(res);
        }
        resultsReady = true;
        
        
    }

    public void getSimpleOutput(String outputFile) throws FileNotFoundException, IOException {
        if (!resultsReady) {
            getDiff();
        }

        Writer fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
        for (int i = 0; i < output.size(); i++) {
            if(output.get(i).change == 0 ){
                fout.write(" "+before.get(output.get(i).ind)+"\n");
            }
            else if(output.get(i).change == 1){
                fout.write("-"+before.get(output.get(i).ind)+"\n");
            }
            else{
                fout.write("+"+after.get(output.get(i).ind)+"\n");
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
            if(output.get(i).change == 0 ){
                fout.write("<div>");
                fout.write(before.get(output.get(i).ind)+ "</div>\n");
            }
            else if(output.get(i).change == 1){
                fout.write("<div style=\"background-color:#FFC7CE;\">");
                fout.write(before.get(output.get(i).ind) +"</div>\n");
            }
            else{
                fout.write("<div style=\"background-color:#C6EFCE;\">");
                fout.write(after.get(output.get(i).ind)+ "</div>\n");
            }
         
        }
        fout.flush();
        fout.close();
    }

    private void preFormatLines(List<String> lines) {

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
                lines.set(i, sb.toString());
            }
        }
    }

    private boolean isNullOrWhitespace(String str) {
        return (str == null || str.isEmpty() || str.trim().isEmpty());
    }
}
