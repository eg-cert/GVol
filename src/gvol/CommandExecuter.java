package gvol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CommandExecuter implements Runnable {
    
    private final ComLayerWithThread comLayer;
    private final String cmd;
    private volatile boolean isStopped;
    private final int id;
    //private final ;
    public CommandExecuter(String cmd,ComLayerWithThread comLayer,int id){
        this.cmd = cmd;
        this.comLayer = comLayer;
        this.id = id;
    }
    
    @Override
    public void run() {
        Process p;
        long st,end;
        st = System.currentTimeMillis()/1000;
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        comLayer.addToConsole("Running command: "+cmd + "\r\n",id);
        comLayer.addToConsole("Execution Started at: "+ timeStamp+"\r\n\r\n", id);
        try {
            //comLayer.addToConsole(cmd);
            p = Runtime.getRuntime().exec(cmd);
            
            
            
            BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            
            while(true){
                Thread.sleep(100);
                while(out.ready()){
                    comLayer.addToConsole(out.readLine(),id);
                }
                while(err.ready()){
                    comLayer.addToConsole(err.readLine(),id);
                }
                if(isStopped) {
                    p.destroy();
                    break;
                }
                try{
                    int y=p.exitValue();
                    break;
                }
                catch(Exception e){
                }
            }
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        try {
        } catch (Exception ex) {}
        
        end = System.currentTimeMillis()/1000;
        end -= st;
        timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        comLayer.addToConsole("\r\n\r\nExecution ended at: "+timeStamp, id);
        comLayer.addToConsole("Elapsed time: "+end+" sec.", id);
        comLayer.threadClosed(id);
        isStopped = true;
        
        
            
        
    }
    
    public void setStop(){
        isStopped = true;
    }
    
    public boolean isRunning(){
        return !isStopped;
    }
}
