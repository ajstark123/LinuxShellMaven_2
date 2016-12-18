package org.ajstark.LinuxShell.MQ;

/**
 * Created by Albert on 12/18/16.
 */
public class MqException extends Exception {
    private String message;
    
    public MqException( String message ) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
    
}
