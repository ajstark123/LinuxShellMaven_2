package org.ajstark.LinuxShell.MQ;

import com.rabbitmq.client.*;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;

import java.io.*;

/**
 * Created by Albert on 12/18/16.
 */
public class MqPublisherTopic {
    private MqChannel channel;
    private String    exchangeName;
    private String    routingKey;
    
    MqPublisherTopic(  MqChannel channel, String exchangeName, String routingKey ) {
        this.channel      = channel;
        this.exchangeName = exchangeName;
        this.routingKey   = routingKey;
    }
    
    
    public void publish(  InputOutputData inOutData ) throws MqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        ByteArrayOutputStream bos            = null;
        byte[]                inOutDataBytes = null;
        try {
            bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(inOutData);
            out.flush();
            inOutDataBytes = bos.toByteArray();
        }
        catch ( Exception excp ) {
            logger.logException( "MqPublisherTopic", "publish",
                    "can not publish to a queue", excp);
    
            MqException inOutExcp = new MqException( "can not publish to a queue" );
            inOutExcp.initCause( excp );
            
            return;
        }
        finally {
            try {
                if ( bos != null ) {
                    bos.close();
                }
            } catch (Exception ex) {
                // ignore close exception
            }
        }
           
        try {
            channel.basicPublish( exchangeName, routingKey, inOutDataBytes );
        }
        catch ( Exception excp ) {
            logger.logException( "MqPublisherTopic", "publish",
                    "can not publish to a queue", excp);
            
            cleanUp();
        
            MqException inOutExcp = new MqException( "can not publish to a queue" );
            inOutExcp.initCause( excp );
            throw inOutExcp;
        }
    }
    
    public void cleanUp() throws MqException {
        channel.close();
    }
    
}
