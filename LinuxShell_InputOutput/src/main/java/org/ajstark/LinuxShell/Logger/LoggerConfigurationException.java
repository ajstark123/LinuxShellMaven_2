package org.ajstark.LinuxShell.Logger;

/**
 * Created by Albert on 12/16/16.
 */
public class LoggerConfigurationException extends RuntimeException {
    private String message;
    
    LoggerConfigurationException( String message ) {
        this.message = message;
    }
    
    public String getMessage( ){
        return message;
    }
    
}
