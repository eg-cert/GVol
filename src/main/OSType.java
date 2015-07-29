
package main;

public class OSType {
    private static boolean resultReady = false;
    private static boolean isWindows;
    
    public static boolean isWindows(){
        if(resultReady) return isWindows;
        String OS = System.getProperty("os.name", "generic").toLowerCase();
        isWindows = (OS.contains("win"));
        resultReady = true;
        return isWindows;
    }
}
