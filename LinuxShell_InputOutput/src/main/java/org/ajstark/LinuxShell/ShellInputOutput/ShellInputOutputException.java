package org.ajstark.LinuxShell.ShellInputOutput;

/**
 * Created by Albert on 12/17/16.
 */
public class ShellInputOutputException extends Exception {
    String message;
    
    ShellInputOutputException( String messsge ) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
