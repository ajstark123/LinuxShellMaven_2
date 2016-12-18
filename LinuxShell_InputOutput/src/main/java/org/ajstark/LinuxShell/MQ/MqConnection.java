package org.ajstark.LinuxShell.MQ;

import com.rabbitmq.client.*;

import org.ajstark.LinuxShell.Logger.*;


import java.io.*;
import java.util.concurrent.*;

/**
 * Created by Albert on 12/18/16.
 */
public class MqConnection {
    
    private String           queueName;
    private Connection       connectionMQ;
    private Channel          channel;
    
    private LinuxShellLogger logger;
    
    private MqConnection( String queueName, Connection connectionMQ, Channel channel )  {
        this.queueName    = queueName;
        this.connectionMQ = connectionMQ;
        this.channel      = channel;
    }
    
    public static MqConnection getMqConnection( String queueName ) throws MqException {
    
        Connection       connectionMQ;
        Channel          channel       = null;
             
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(        "localhost" );
    
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        MqException inOutExcp = null;
        connectionMQ = null;
        try {
            connectionMQ = factory.newConnection();
        
            channel = connectionMQ.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
        }
        catch ( Exception excp ) {
            logger.logException( "MqConnection", "getMqConnection",
                    "cannot create MQ queue consumer", excp);
        
            inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
            closeMqConnection( connectionMQ, inOutExcp );
        }
   
        MqConnection connection = new MqConnection( queueName, connectionMQ, channel );
        
        return connection;
    }
    
    
    public MqConsumer creatMqConsumer(  ) throws MqException {
    
        QueueingConsumer consumerMQ = new QueueingConsumer(channel);
    
        try {
            consumerMQ = new QueueingConsumer(channel);
            channel.basicConsume(queueName, true, consumerMQ);
            
            
        }
        catch (IOException excp ) {
            logger.logException( "MqConnection", "creatMqConsumer ctor",
                    "cannot create MQ queue consumer", excp);
    
            MqException inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
    
            closeMqConnection( connectionMQ, inOutExcp );;
        }
    
        MqConsumer  consumer = new MqConsumer( consumerMQ );
        
        return consumer;
    }
    
    public MqPublisher createMqPublisher( )  {
        return new MqPublisher( channel, queueName ) ;
    }
    
    
    public void close() throws MqException {
        closeMqConnection( connectionMQ, null );
    }
    
    
    private static void closeMqConnection( Connection connectionMQ, MqException inOutExcp ) throws MqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        if ( connectionMQ != null) {
            try {
                connectionMQ.close();
    
                if ( inOutExcp != null ) {
                    throw inOutExcp;
                }
            }
            catch ( IOException excp ) {
                
                if ( inOutExcp != null ) {
                    throw inOutExcp;
                }
                else {
                    logger.logException("MqConnection", "closeMqConnection",
                            "cannot close MQ connectionr", excp);
                    
                    inOutExcp = new MqException( "cannot close MQ connection" );
                    inOutExcp.initCause( excp );
                    throw inOutExcp;
                }
            }
        }
        
        
        
    }
    
    
    
    
    
}
