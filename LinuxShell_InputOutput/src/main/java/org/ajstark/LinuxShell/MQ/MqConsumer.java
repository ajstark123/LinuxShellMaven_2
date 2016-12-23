package org.ajstark.LinuxShell.MQ;

import java.io.*;
import com.rabbitmq.client.*;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;


/**
 * Created by Albert on 12/18/16.
 */
public class MqConsumer {
    private MqChannel        channel;
    private QueueingConsumer consumerMQ;

    
    MqConsumer( QueueingConsumer consumerMQ, MqChannel channel ) {
        this.consumerMQ = consumerMQ;
        this.channel    = channel;
    }
    
    public InputOutputData getInput() throws MqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        QueueingConsumer.Delivery delivery = null;
        try {
            if ( consumerMQ != null ) {
                delivery = consumerMQ.nextDelivery();
            }
            else {
                logger.logError( "MqConsumer", "getInput",
                        "consumerMQ is null,  cannot read from queue" );
                
                MqException inOutExcp = new MqException( "consumerMQ is null,  cannot read from queue" );
                
                throw inOutExcp;
            }
        }
        catch ( Exception excp ) {
            cleanUp() ;
    
            logger.logException( "MqConsumer", "getInput",
                    "cannot read from queue", excp);
    
            MqException inOutExcp = new MqException( "cannot read from queue" );
            inOutExcp.initCause( excp );
            
            throw inOutExcp;
        }
    
        byte[] inOutDataByte = delivery.getBody();
    
        try {
            ByteArrayInputStream in        = new ByteArrayInputStream(inOutDataByte);
            ObjectInputStream    is        = new ObjectInputStream(in);
            InputOutputData      inputData = (InputOutputData) is.readObject();
            return inputData;
        }
        catch ( Exception excp ) {
            logger.logException( "MqConsumer", "getInput",
                    "cannot create an InputOutputData object", excp);
            
            return null;
        }
    }
    
    public void cleanUp( ) throws MqException {
        if ( channel != null ) {
            channel.close();
        }
    }
}
