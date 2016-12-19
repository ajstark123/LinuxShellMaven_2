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
    private String           uuid;
    
    private LinuxShellLogger logger;
    
    private MqConnection( String queueName, Connection connectionMQ, Channel channel, String uuid )  {
        this.queueName    = queueName;
        this.connectionMQ = connectionMQ;
        this.channel      = channel;
        this.uuid         = uuid;
    
        this.logger       = LinuxShellLogger.getLogger();
    }
    
    public static MqConnection getMqConnection( String queueName, String uuid ) throws MqException {
    
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
            
        }
        catch ( Exception excp ) {
            logger.logException( "MqConnection", "getMqConnection",
                    "cannot create MQ queue consumer", excp);
        
            inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
            closeMqConnection( connectionMQ, inOutExcp );
        }
   
        MqConnection connection = new MqConnection( queueName, connectionMQ, channel, uuid );
        
        return connection;
    }
    
    
    public MqConsumer creatMqConsumer(  ) throws MqException {
    
        QueueingConsumer consumerMQ = null;
    
        try {
            channel.queueDeclare(queueName, false, false, false, null);
            
            consumerMQ = new QueueingConsumer(channel);
            channel.basicConsume(queueName, true, consumerMQ);
        }
        catch (IOException excp ) {
            logger.logException( "MqConnection", "creatMqConsumer",
                    "cannot create MQ queue consumer", excp);
    
            MqException inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
    
            closeMqConnection( connectionMQ, inOutExcp );;
        }
    
        MqConsumer  consumer = new MqConsumer( consumerMQ );
        
        return consumer;
    }
    
    
    public MqConsumer creatMqConsumerTopic( MqEnvProperties.OutputType outputType ) throws MqException {
    
    
        String exchangeName = MqEnvProperties.getExchangeName();
        if ( exchangeName == null ) {
            logger.logError( "MqConnection", "creatMqConsumerTopic",
                    "ExchangeName Property not specified");
        
            MqException inOutExcp = new MqException( "ExchangeName Property not specified" );
            closeMqConnection( connectionMQ, inOutExcp );
        }
        
        String routingKey = MqEnvProperties.getOutputType( outputType ) + "." + uuid;
        
        QueueingConsumer consumerMQ = null;
        try {
            channel.exchangeDeclare(exchangeName, "topic");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchangeName, routingKey);
            
            consumerMQ = new QueueingConsumer(channel);
            
            channel.basicConsume(queueName, true, consumerMQ);
            
        }
        catch (IOException excp ) {
            logger.logException( "MqConnection", "creatMqConsumerTopic",
                    "cannot create MQ queue consumer", excp);
            
            MqException inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
            
            closeMqConnection( connectionMQ, inOutExcp );;
        }
        
        MqConsumer  consumer = new MqConsumer( consumerMQ );
        
        return consumer;
    }
    
    
    
    public MqPublisher createMqPublisher( ) throws MqException {
    
        try {
            channel.queueDeclare(queueName, false, false, false, null);
        }
        catch (IOException excp ) {
            logger.logException( "MqConnection", "createMqPublisher",
                    "cannot create MQ queue consumer", excp);
    
            MqException inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
    
            closeMqConnection( connectionMQ, inOutExcp );;
        }
            
        return new MqPublisher( channel, queueName, uuid ) ;
    }
    
    
    public MqPublisherTopic createMqPublisherTopic(  MqEnvProperties.OutputType outputType ) throws MqException {
    
        String exchangeName = MqEnvProperties.getExchangeName();
        if ( exchangeName == null ) {
            logger.logError( "MqConnection", "createMqPublisherTopic",
                     "ExchangeName Property not specified");
    
            MqException inOutExcp = new MqException( "ExchangeName Property not specified" );
            closeMqConnection( connectionMQ, inOutExcp );
        }
        
        String routingKey = routingKey = MqEnvProperties.getOutputType( outputType ) + "." + uuid;
        try {
            channel.exchangeDeclare( exchangeName,"topic");
        }
        catch (IOException excp ) {
            logger.logException( "MqConnection", "createMqPublisherTopic",
                    "cannot create MQ queue topic publisher", excp);
            
            MqException inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
            
            closeMqConnection( connectionMQ, inOutExcp );;
        }
        
        return new MqPublisherTopic( channel, exchangeName, routingKey ) ;
    }
    
    
    
    
    
    public void close() throws MqException {
        closeMqConnection( connectionMQ, null );
    }
    
    
    private static void closeMqConnection( Connection connectionMQ, MqException inOutExcp ) throws MqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        if ( connectionMQ != null) {
            try {
                if ( connectionMQ.isOpen() ) {
                    connectionMQ.close();
                }
    
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
