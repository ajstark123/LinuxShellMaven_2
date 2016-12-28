package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;

/**
 * Created by Albert on 12/19/16.
 */
public class ShellStandardErrorMq  implements ShellStandardError {
    
    private String           uuidStr;
    private MqPublisherTopic publisher;
    private LinuxShellLogger logger;
    
    
    ShellStandardErrorMq( String uuidStr, MqConnection connection, MqPublisherTopic   publisher ) {
        this.uuidStr    = uuidStr;
        this.publisher  = publisher;
        this.logger     = LinuxShellLogger.getLogger();
    }
    
    
    private void sendError( String outStr ) {
        // do nothing
    }
    
    public String getUuidStr() {
        return uuidStr;
    }
    
    public void put( InputOutputData outData ){
        if (outData.isLastDataSent() ){
            cleanUp();
        }
        else {
            try {
                publisher.publish(outData);
            }
            catch ( Exception excp ) {
                logger.logException( "ShellStandardErrorMq", "sendError",
                        "can not publish to a queue", excp);
            }
        }
    }
    
    public void cleanUp() {
        try {
            publisher.cleanUp();
        }
        catch ( MqException excp ) {
            logger.logException( "ShellStandardErrorMq", "cleanUp",
                    "could not clean up MQ", excp);
        }
    }
}
