package org.ajstark.LinuxShell.MQ;

import java.io.*;
import com.rabbitmq.client.*;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;


/**
 * Created by Albert on 12/18/16.
 */
public class MqConsumer {
    private QueueingConsumer consumerMQ;
    private int i;
    
    MqConsumer( QueueingConsumer consumerMQ ) {
        i = 0;
        this.consumerMQ = consumerMQ;
    }
    
    public InputOutputData getInput() throws MqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        try {
            //System.out.println( "" + i + " before consumerMQ.nextDelivery");
            QueueingConsumer.Delivery delivery = consumerMQ.nextDelivery();
            //System.out.println( "" + i + " after consumerMQ.nextDelivery");
            
            //++i;
            
            byte[] inOutDataByte = delivery.getBody();
    
            ByteArrayInputStream in        = new ByteArrayInputStream(inOutDataByte);
            ObjectInputStream    is        = new ObjectInputStream(in);
            InputOutputData      inputData = (InputOutputData) is.readObject();
            
            return inputData;
        }
        catch ( Exception excp ) {
            logger.logException( "MqConsumer", "getInput",
                    "cannot read from queue", excp);
            
            MqException inOutExcp = new MqException( "cannot read from queue" );
            inOutExcp.initCause( excp );
            throw inOutExcp;
        }
    }
    
    
    
}
