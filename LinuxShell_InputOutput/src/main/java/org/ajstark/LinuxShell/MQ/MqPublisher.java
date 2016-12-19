package org.ajstark.LinuxShell.MQ;

import com.rabbitmq.client.*;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;

import java.io.*;

/**
 * Created by Albert on 12/18/16.
 */
public class MqPublisher {
    private Channel          channel;
    private String           queueName;
    private String           uuid;
    
    MqPublisher(  Channel channel, String queueName, String uuid ) {
        this.channel   = channel;
        this.queueName = queueName;
        this.uuid      = uuid;
    }
    
    
    public void publish(  String str ) throws MqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        ByteArrayOutputStream bos = null;
        try {
            InputOutputData inOutData = new InputOutputData( str );
            inOutData.setUuidStr( uuid );
            
            bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(inOutData);
            out.flush();
            byte[] yourBytes = bos.toByteArray();
            
            System.out.println( "\n\nMqPublisher::publish" );
            System.out.println( inOutData.getData() );
            System.out.println( inOutData.getUuidStr() );
            
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
