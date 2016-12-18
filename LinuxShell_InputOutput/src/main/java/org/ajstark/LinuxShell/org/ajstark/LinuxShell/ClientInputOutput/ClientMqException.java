package org.ajstark.LinuxShell.org.ajstark.LinuxShell.ClientInputOutput;

/**
 * Created by Albert on 12/18/16.
 */
public class ClientMqException extends Exception {
    String message;
    
    public ClientMqException( String messsge ) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
   
}
