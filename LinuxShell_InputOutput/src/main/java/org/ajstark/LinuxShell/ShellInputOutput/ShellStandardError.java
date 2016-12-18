package org.ajstark.LinuxShell.ShellInputOutput;

/**
 * Created by Albert on 12/17/16.
 */
public interface ShellStandardError {
    public void sendError( String errStr );
    
    public String getUuidStr();
    
    public void cleanUp();
}
