package org.ajstark.LinuxShell.ClientInputOutput;

import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;
import org.ajstark.LinuxShell.InputOutput.*;

/**
 * Created by Albert on 12/19/16.
 */
public class ReceiveFromShell {
    
    
    MqConsumer   consumer;
    
    private ReceiveFromShell( MqConsumer consumer) {
        this.consumer = consumer;
    }
    
    public static ReceiveFromShell getReceiveFromShell( MqEnvProperties.OutputType outputType, String uuid ) throws ClientMqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        MqConnection connection = null;
        MqConsumer   consumer   = null;
        try {
            connection = MqConnection.getMqConnection( );
            consumer = connection.creatMqConsumerTopic( outputType, uuid  );
        }
        catch (MqException excp) {
            ClientMqException inOutExcp = new ClientMqException(excp.getMessage());
            inOutExcp.initCause(excp);
            
            logger.logException("ReceiveFromShell", "ReceiveFromShell",
                    excp.getMessage(), inOutExcp);
            
            throw inOutExcp;
        }
    
        ReceiveFromShell receiveFromShell = new ReceiveFromShell( consumer );
        
        return receiveFromShell;
    }
    
    public String consume() throws ClientMqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        if (consumer == null) {
            logger.logError("ReceiveFromShell", "consume",
                    "cannot consume from queue" );
    
            ClientMqException inOutExcp = new ClientMqException("cannot consume from queue");

            throw inOutExcp;
        }
        
        try {
            InputOutputData inputOutPutData = consumer.getInput();
            if (inputOutPutData == null) {
                return null;
            }
            
            return inputOutPutData.getData();
        }
        catch (MqException excp) {
            logger.logException("ReceiveFromShell", "consume",
                    "cannot consume from queue", excp);
            
            ClientMqException inOutExcp = new ClientMqException("cannot consume from queue");
            inOutExcp.initCause(excp);
            throw inOutExcp;
        }
    }
    
    public void cleanUp() {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        if (consumer != null) {
            try {
                consumer.cleanUp();
            }
            catch (Exception excp) {
                logger.logException("ReceiveFromShell", "cleanUp",
                        "cannot close MQ connection", excp);
            }
        }
    }
}