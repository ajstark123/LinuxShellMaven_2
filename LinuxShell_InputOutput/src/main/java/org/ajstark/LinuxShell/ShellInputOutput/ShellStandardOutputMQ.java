package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;

/**
 * Created by Albert on 12/19/16.
 */
public class ShellStandardOutputMQ implements ShellStandardOutput {
    private             String           uuidStr;
    private             MqPublisherTopic publisher;
    private             LinuxShellLogger logger;
    
    
    ShellStandardOutputMQ( String uuidStr, MqPublisherTopic   publisher ) {
        this.uuidStr    = uuidStr;
        this.publisher  = publisher;
        this.logger     = LinuxShellLogger.getLogger();
    }
    
    
    private void sendOutput( String outStr ) {
        try {
            publisher.publish(outStr);
        }
        catch ( Exception excp ) {
            logger.logException( "ShellStandardOutputMQ", "sendOutput",
                    "can not publish to a queue", excp);
         }
    }
    
    public String getUuidStr() {
        return uuidStr;
    }
    
    public void put( InputOutputData outData ){
        if ( outData.isLastDataSent()  ) {
            cleanUp();
        }
        else {
            String outStr = outData.getData();
    
            sendOutput( outStr );
        }
    }
    
    public void cleanUp() {
        try {
            publisher.cleanUp();
        }
        catch ( MqException excp ) {
            logger.logException( "ShellStandardOutputMQ", "cleanUp",
                    "could not clean up MQ", excp);
        }
    }
}
