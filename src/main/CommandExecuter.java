package main;

import diff.JDiff;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import iface.*;

public class CommandExecuter implements Runnable {
    
    private final ComLayerWithThread comLayer;
    private final String [][] cmd;
    private final String [][] diff;
    private volatile boolean isStopped;
    private final int id;
    public CommandExecuter(String [][] cmd, ComLayerWithThread comLayer,int id, String [][] diff){
        this.cmd = cmd;
        this.comLayer = comLayer;
        this.id = id;
        this.diff = diff;
    }
    
    @Override
    public void run() {
        Process p;
        long st,end;
        st = System.currentTimeMillis()/1000;
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        comLayer.addToConsole("Execution Started at: "+ timeStamp+"\r\n\r\n", id,-1);
        for(int i=0;i<cmd.length;i++){
            if(isStopped) break;
            String cmdString = "";
            for(int j=0;j<cmd[i].length;j++) cmdString = cmdString + " " +cmd[i][j];
            comLayer.addToConsole("Running command: "+cmdString + "\r\n",id,-1);
            try {
                p = Runtime.getRuntime().exec(cmd[i]);
                //p = Runtime.getRuntime().exec(cmdString);
                BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while(true){
                    Thread.sleep(100);
                    while(out.ready()){
                        comLayer.addToConsole(out.readLine(),id,i);
                    }
                    while(err.ready()){
                        comLayer.addToConsole(err.readLine(),id,i);
                    }
                    if(isStopped) {
                        p.destroy();
                        //p.destroyForcibly();
                        break;
                    }
                    try{
                        int y=p.exitValue();
                        break;
                    }
                    catch(Exception e){
                        System.err.println(e.getMessage());
                    }
                }

            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
        
        comLayer.threadClosed(id);
        runDiff(diff);
        
        
        end = System.currentTimeMillis()/1000;
        end -= st;
        timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        comLayer.addToConsole("\r\n\r\nExecution ended at: "+timeStamp, id, -1);
        comLayer.addToConsole("Elapsed time: "+end+" sec.", id, -1);
        
        isStopped = true;
        
    }
    
    public void setStop(){
        isStopped = true;
    }
    
    public boolean isRunning(){
        return !isStopped;
    }

    private void runDiff(String[][] diff) {
        if(diff == null) return;
        comLayer.addToConsole("\r\nRunning diff ", id, -1);
        for(int i=0;i<diff.length;i++){
            try {
                JDiff jDiff = new JDiff(diff[i][0], diff[i][1]);
                jDiff.getFormattedOutputSep(diff[i][1]+".diff.html");
            } catch (Exception ex) {
                System.err.println("Failed to diff");
            }
        }
    }
}
