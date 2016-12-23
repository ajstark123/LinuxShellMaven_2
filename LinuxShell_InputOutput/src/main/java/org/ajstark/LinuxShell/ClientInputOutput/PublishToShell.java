package org.ajstark.LinuxShell.ClientInputOutput;

import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;

/**
 * Created by Albert on 12/18/16.
 *
 *
 *
 *
 *
 *
 */
public class PublishToShell {
    private MqPublisher  publisher;
    
    private PublishToShell( MqPublisher  publisher )  {
        this.publisher  = publisher;
    }
        

    
    public static PublishToShell getPublishToShell( String uuid ) throws ClientMqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        MqConnection connection = null;
        MqPublisher  publisher  = null;
        try {
            connection = MqConnection.getMqConnection( );
            publisher   = connection.createMqPublisher( uuid );
        }
        catch (MqException excp ) {
            ClientMqException inOutExcp = new ClientMqException( excp.getMessage() );
            inOutExcp.initCause( excp );
        
            logger.logException( "PublishToShell", "getPublishToShell",
                    excp.getMessage(), inOutExcp);
        
            throw inOutExcp;
        }
    
        PublishToShell publishToShell = new PublishToShell( publisher );
        
        return publishToShell;
    }
    
    public void publish( String str ) throws ClientMqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        if ( publisher == null) {
            logger.logError( "PublishToShell", "publish",
                    "cannot publish to queue" );
    
            ClientMqException inOutExcp = new ClientMqException( "cannot publish to queue" );

            throw inOutExcp;
        }
        
        try {
            publisher.publish(str);
        }
        catch ( MqException excp ) {
            logger.logException( "PublishToShell", "publish",
                    "cannot publish to queue", excp);
    
            ClientMqException inOutExcp = new ClientMqException( "cannot publish to queue" );
            inOutExcp.initCause( excp );
            throw inOutExcp;
        }
    }
    
    public void cleanUp() {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
            try {
                publisher.cleanUp();
            }
            catch ( Exception excp ) {
                logger.logException("ShellStandardInputMQ", "cleanUp",
                        "cannot close MQ connectionr", excp);
            }
    }
    
}
