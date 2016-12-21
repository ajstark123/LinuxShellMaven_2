package org.ajstark.LinuxShell.Shell;

/**
 * Created by Albert on 12/21/16.
 */
public class ShellException extends Exception {
    private String message;
    
    ShellException( String message ){
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
