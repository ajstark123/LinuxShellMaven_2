package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.InputOutput.*;

/**
 * Created by Albert on 12/17/16.
 */
public class ShellStandardErrorConsole implements ShellStandardError {
    private String uuidStr;
    
    ShellStandardErrorConsole( String uuidStr ) {
        this.uuidStr = uuidStr;
    }
    
    private void sendError( String outStr ) {
        System.err.println( outStr );
        System.err.flush();
    }
    
    public String getUuidStr() {
        return uuidStr;
    }
    
    public void put( InputOutputData outData ){
        if ( ! outData.isLastDataSent() ) {
            String outStr = outData.getData();
    
            sendError(outStr);
        }
    }
    
    public void cleanUp() {
        //empty body
    }
}
