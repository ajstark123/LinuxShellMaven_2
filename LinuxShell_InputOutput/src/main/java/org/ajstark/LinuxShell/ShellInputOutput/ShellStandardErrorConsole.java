package org.ajstark.LinuxShell.ShellInputOutput;

/**
 * Created by Albert on 12/17/16.
 */
public class ShellStandardErrorConsole implements ShellStandardError {
    private String uuidStr;
    
    ShellStandardErrorConsole( String uuidStr ) {
        this.uuidStr = uuidStr;
    }
    
    public void sendError( String outStr ) {
        System.err.println( outStr );
        System.err.flush();
    }
    
    public String getUuidStr() {
        return uuidStr;
    }
    
    public void cleanUp() {
        //empty body
    }
}
