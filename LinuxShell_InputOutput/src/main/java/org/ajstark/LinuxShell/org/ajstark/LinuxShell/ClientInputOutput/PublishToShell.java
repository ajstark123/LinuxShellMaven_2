package org.ajstark.LinuxShell.org.ajstark.LinuxShell.ClientInputOutput;

import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;

/**
 * Created by Albert on 12/18/16.
 */
public class PublishToShell {
    MqConnection connection;
    MqPublisher  publisher;
    
    private PublishToShell( MqConnection connection, MqPublisher  publisher )  {
        this.connection = connection;
        this.publisher  = publisher;
    }
        

    
    public static PublishToShell getPublishToShell() throws ClientMqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        String queueName       = System.getProperty( "QueueName" );
        if ( queueName == null ) {
            ClientMqException inOutExcp = new ClientMqException( "missing MQ queue name" );
        
            logger.logException( "PublishToShell", "getPublishToShell",
                    "missing MQ queue name", inOutExcp);
        
            throw inOutExcp;
        }
    
        MqConnection connection = null;
        MqPublisher  publisher  = null;
        try {
            connection = MqConnection.getMqConnection(queueName);
            publisher   = connection.createMqPublisher();
        }
        catch (MqException excp ) {
            ClientMqException inOutExcp = new ClientMqException( excp.getMessage() );
            inOutExcp.initCause( excp );
        
            logger.logException( "PublishToShell", "getPublishToShell",
                    excp.getMessage(), inOutExcp);
        
            throw inOutExcp;
        }
    
        PublishToShell publishToShell = new PublishToShell( connection, publisher );
        
        return publishToShell;
    }
    
    public void publish( String str ) throws ClientMqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();

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
        
        if ( connection != null) {
            try {
                connection.close();
            }
            catch ( Exception excp ) {
                logger.logException("ShellStandardInputMQ", "cleanUp",
                        "cannot close MQ connectionr", excp);
            }
        }
        
        connection = null;
    }
    
}