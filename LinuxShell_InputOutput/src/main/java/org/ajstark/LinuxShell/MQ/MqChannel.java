package org.ajstark.LinuxShell.MQ;

import com.rabbitmq.client.*;

import org.ajstark.LinuxShell.Logger.*;
import java.io.*;
import java.util.concurrent.*;


/**
 * Created by Albert on 12/21/16.
 */
public class MqChannel implements ShutdownListener {
    private Channel          channel;
    private LinuxShellLogger logger;
    
    private ScheduledExecutorService executor;
    
    MqChannel( Channel  channel ) {
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
    
    void queueDeclare( String queueName ) throws MqException {
        boolean durable    = true;
        boolean exclusive  = false;
        boolean autodelete = false;
        try {
            channel.queueDeclare(queueName, durable, exclusive, autodelete, null);
        }
        catch (IOException excp) {
            logger.logException( "MqChannel", "queueDeclare",
                    "queueDeclared failed", excp);
    
            MqException inOutExcp = new MqException( "queueDeclared failed");
            inOutExcp.initCause( excp );
    
            try {
                close();
            }
            catch (Exception junkExcp) {
                // do nothing
            }
            
            throw inOutExcp;
        }
    }
    
    void exchangeDeclare( String exchangeName ) throws MqException {
        //  worked
        // channel.exchangeDeclare(exchangeName, "topic");
        // but the following is better
        //
        // if the excghange does not exist create it otherwise do nothing
        // the durable = true and autodelete = false keep the exchange
        // around after a restart ot the rabbit mq broker
        //
        boolean durable    = true;
        boolean autodelete = false;
        
        try {
            channel.exchangeDeclare(exchangeName, "topic", durable, autodelete, null);
        }
        catch ( IOException excp ) {
            logger.logException( "MqChannel", "exchangeDeclare",
                    "exchangeDeclare failed", excp);
    
            MqException inOutExcp = new MqException( "exchangeDeclare failed" );
            inOutExcp.initCause( excp );
    
            try {
                close();
            }
            catch (Exception junkExcp) {
                // do nothing
            }
            
            throw inOutExcp;
        }
    
    }
    
    QueueingConsumer createQueueingConsumer()  {
        QueueingConsumer consumerMQ = new QueueingConsumer(channel);
        
        return consumerMQ;
    }
    
    void basicConsume( String queueName, QueueingConsumer consumerMQ ) throws MqException {
        try {
            channel.basicConsume(queueName, true, consumerMQ);
        }
        catch( IOException excp ) {
            logger.logException( "MqChannel", "basicConsume",
                    "basicConsume failed", excp);
    
            MqException inOutExcp = new MqException( "basicConsume failed" );
            inOutExcp.initCause( excp );
    
            try {
                close();
            }
            catch (Exception junkExcp) {
                // do nothing
            }
    
            throw inOutExcp;
        }
    }
    
    void queueBind( String queueName, String exchangeName, String routingKey ) throws MqException {
        try {
            channel.queueBind(queueName, exchangeName, routingKey);
        }
        catch ( IOException excp ) {
            logger.logException( "MqChannel", "queueBind",
                    "queueBind failed", excp);
    
            MqException inOutExcp = new MqException( "queueBind failed" );
            inOutExcp.initCause( excp );
    
            try {
                close();
            }
            catch (Exception junkExcp) {
                // do nothing
            }
            
            throw inOutExcp;
        }
    }
    
    String getQueueNameFromQueueDeclare() throws MqException {
        
        String queueName = null;
        
        try {
            queueName = channel.queueDeclare().getQueue();
        }
        catch( IOException excp ) {
            logger.logException( "MqChannel", "queueBind",
                    "getQueueNameFromQueueDeclare failed", excp);
    
            MqException inOutExcp = new MqException( "getQueueNameFromQueueDeclare failed" );
            inOutExcp.initCause( excp );
    
            try {
                close();
            }
            catch (Exception junkExcp) {
                // do nothing
            }
    
            throw inOutExcp;
        }

        return queueName;
    }
    
    public void close() throws MqException {
        if ( channel != null) {
            try {
                // the isOpen method can't be fully trusted.
                //another thread may colse the channel after the check is done
                if ( channel.isOpen() ) {
                    if ( channel.isOpen() ) {
                        channel.close();
                    }
                }
                channel = null;
            }
            catch ( Exception excp ) {
                logger.logException("MqChannel", "close",
                        "cannot close MQ channel", excp);
            }
        }
    }
    
    public void shutdownCompleted(  ShutdownSignalException cause ) {
        // reconnect only on on unexpected errors
        if ( ! cause.isInitiatedByApplication() ){
            logger.logException( "MqChannel", "shutDownComplete",
                    "lost MQ connection", cause);
            
            asyncWaitAndReconnect();
        }
        else {
            executor.shutdown();
        }
    }
    
    private void asyncWaitAndReconnect() {
        executor.schedule( new Runnable()
        {
            @Override
            public void run() {
                start();
            }
        }, 15, TimeUnit.SECONDS );
    }
    
    private void  start() {
        try {
            close();
        }
        catch (MqException excp ) {
            // do nothing
            logger.logException( "MqChannel", "stop",
                    "cannot close MQ channel", excp);
        }
        
        channel = null;
    }
    
    public void stop() {
        try {
            logger.logInfo( "MqChannel", "stop",
                    "stop method called" );
            close();
        }
        catch (Exception excp) {
            // do nothing
            logger.logException( "MqChannel", "stop",
                    "cannot close MQ channel", excp);
        }
    }
    
    
}
