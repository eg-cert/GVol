package gvol;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandExecuter implements Runnable {
    
    private final ComLayerWithThread comLayer;
    private final String cmd;
    private volatile boolean isStopped;
    //private final ;
    public CommandExecuter(String cmd,ComLayerWithThread comLayer){
        this.cmd = cmd;
        this.comLayer = comLayer;
    }
    
    @Override
    public void run() {
        Process p;
        try {
            comLayer.addToConsole(cmd);
            p = Runtime.getRuntime().exec(cmd);
            
            BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            
            while(true){
                Thread.sleep(100);
                while(out.ready()){
                    comLayer.addToConsole(out.readLine());
                }
                while(err.ready()){
                    comLayer.addToConsole(err.readLine());
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
        comLayer.threadClosed();
    }
    
    public void setStop(){
        isStopped = true;
    }
}
