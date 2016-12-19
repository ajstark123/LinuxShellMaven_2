package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;

/**
 * Created by Albert on 12/19/16.
 */
public class ShellStandardErrorMq  implements ShellStandardError {
    
    private String           uuidStr;
    private MqConnection     connection;
    private MqPublisherTopic publisher;
    private LinuxShellLogger logger;
    
    
    ShellStandardErrorMq( String uuidStr, MqConnection connection, MqPublisherTopic   publisher ) {
        this.uuidStr    = uuidStr;
        this.connection = connection;
        this.publisher  = publisher;
        this.logger     = LinuxShellLogger.getLogger();
    }
    
    
    private void sendError( String outStr ) {
        try {
            publisher.publish(outStr);
        }
        catch ( Exception excp ) {
            logger.logException( "ShellStandardErrorMq", "sendError",
                    "can not publish to a queue", excp);
        }
    }
    
    public String getUuidStr() {
        return uuidStr;
    }
    
    public void put( InputOutputData outData ){
        if (outData.isLastDataSent() ){
            cleanUp();
        }
        else {
            String outStr = outData.getData();
    
            sendError(outStr);
        }
    }
    
    public void cleanUp() {
        try {
            connection.close();
        }
        catch ( MqException excp ) {
            logger.logException( "ShellStandardErrorMq", "cleanUp",
                    "could not close the connection", excp);
        }
    }
}
