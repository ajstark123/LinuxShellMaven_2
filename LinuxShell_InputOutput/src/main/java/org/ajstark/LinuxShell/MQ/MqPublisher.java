package org.ajstark.LinuxShell.MQ;

import com.rabbitmq.client.*;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;

import java.io.*;

/**
 * Created by Albert on 12/18/16.
 */
public class MqPublisher {
    Channel          channel;
    String           queueName;
    
    MqPublisher(  Channel channel, String queueName ) {
        this.channel   = channel;
        this.queueName = queueName;
    }
    
    
    public void publish(  String str ) throws MqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        ByteArrayOutputStream bos = null;
        try {
            InputOutputData inOutData = new InputOutputData( str );
            
            bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(inOutData);
            out.flush();
            byte[] yourBytes = bos.toByteArray();
            
            channel.basicPublish( "", queueName,  null, yourBytes );
        }
        catch ( Exception excp ) {
            logger.logException( "MqPublisher", "publish",
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
