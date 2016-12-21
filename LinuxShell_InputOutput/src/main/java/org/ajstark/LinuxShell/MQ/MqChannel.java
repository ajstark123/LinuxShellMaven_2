package org.ajstark.LinuxShell.MQ;

import com.rabbitmq.client.*;

import org.ajstark.LinuxShell.Logger.*;
import java.io.*;


/**
 * Created by Albert on 12/21/16.
 */
public class MqChannel {
    private Channel          channel;
    private LinuxShellLogger logger;
    
    MqChannel( Channel channel ) {
        this.channel = channel;
        this.logger = LinuxShellLogger.getLogger();
    }
    
    /**
     * publishes data to a queue or topic
     *
     * @param exchangeName
     * @param routingKey     can be either a queue name or topic routing keey
     * @param message        byte array with the message to be published
     * @throws MqException
     */
    void basicPublish( String exchangeName, String routingKey, byte[] message ) throws MqException {
        if ( channel != null ) {
            try {
                channel.basicPublish( exchangeName, routingKey, null, message );
            }
            catch (IOException excp) {
                logger.logException("MqChannel", "basicPublish",
                        "can not publish to a queue", excp);
    
                MqException inOutExcp = new MqException("can not publish to a queue");
                inOutExcp.initCause(excp);
                throw inOutExcp;
            }
        }
    }
    
    public void close() throws MqException {
        if ( channel != null) {
            try {
                if ( channel.isOpen() ) {
                    channel.close();
                }
                channel = null;
            }
            catch ( Exception excp ) {
                logger.logException("MqChannel", "close",
                        "cannot close MQ channel", excp);
            }
        }
    }
}
