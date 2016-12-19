package org.ajstark.LinuxShell.MQ;

import com.rabbitmq.client.*;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;

import java.io.*;

/**
 * Created by Albert on 12/18/16.
 */
public class MqPublisherTopic {
    private Channel channel;
    private String  exchangeName;
    private String  routingKey;
    
    MqPublisherTopic(  Channel channel, String exchangeName, String routingKey ) {
        this.channel      = channel;
        this.exchangeName = exchangeName;
        this.routingKey   = routingKey;
    }
    
    
    public void publish(  String inOutDataStr ) throws MqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        ByteArrayOutputStream bos = null;
        try {
            InputOutputData inOutData = new InputOutputData( inOutDataStr );
        
            bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(inOutData);
            out.flush();
            byte[] inOutDataBytes = bos.toByteArray();
            
            channel.basicPublish( exchangeName, routingKey, null, inOutDataBytes );
        }
        catch ( Exception excp ) {
            logger.logException( "MqPublisherTopic", "publish",
                    "can not publish to a queue", excp);
        
            MqException inOutExcp = new MqException( "can not publish to a queue" );
            inOutExcp.initCause( excp );
            throw inOutExcp;
        }
        finally {
            try {
                bos.close();
            } catch (Exception ex) {
                // ignore close exception
            }
        }
        
        
        
        
        
    }
    
}
