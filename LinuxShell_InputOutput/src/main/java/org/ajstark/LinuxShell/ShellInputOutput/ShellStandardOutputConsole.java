package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.InputOutput.*;

/**
 * Created by Albert on 12/17/16.
 */
public class ShellStandardOutputConsole implements ShellStandardOutput {
    private String uuidStr;
    
    ShellStandardOutputConsole( String uuidStr ) {
        this.uuidStr = uuidStr;
    }
    
    
    private void sendOutput( String outStr ) {
        System.out.println( outStr );
        System.out.flush();
    }
    
    public String getUuidStr() {
        return uuidStr;
    }
    
    public void put( InputOutputData outData ){
        if ( ! outData.isLastDataSent() ) {
            String outStr = outData.getData();
    
            sendOutput(outStr);
        }
    }
    
    public void cleanUp() {
        //empty body
    }
}
