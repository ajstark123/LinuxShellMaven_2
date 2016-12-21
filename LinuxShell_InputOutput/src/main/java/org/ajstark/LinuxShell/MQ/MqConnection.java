package org.ajstark.LinuxShell.MQ;

import com.rabbitmq.client.*;

import org.ajstark.LinuxShell.Logger.*;


import java.io.*;
import java.util.concurrent.*;

import java.util.*;

/**
 * Created by Albert on 12/18/16.
 */
public class MqConnection {
    
    private static MqConnection mqConnection = null;
    private Connection          connectionMQ;
    private LinuxShellLogger    logger;
    
    private MqConnection( Connection connectionMQ )  {
        this.connectionMQ = connectionMQ;
    
        this.logger       = LinuxShellLogger.getLogger();
    }
    
    public static MqConnection getMqConnection( ) throws MqException {
        
        if ( mqConnection != null ) {
            return mqConnection;
        }
    
        Connection       connectionMQ;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(        "localhost" );
    
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        MqException inOutExcp = null;
        connectionMQ = null;
        try {
            connectionMQ = factory.newConnection();
        }
        catch ( Exception excp ) {
            logger.logException( "MqConnection", "getMqConnection",
                    "cannot create MQ queue consumer", excp);
        
            inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
            closeMqConnection( connectionMQ, inOutExcp );
        }
    
        mqConnection = new MqConnection( connectionMQ );
        
        return mqConnection;
    }
    
    
    public MqConsumer creatMqConsumer( String queueName ) throws MqException {
        
        QueueingConsumer consumerMQ = null;
    
        MqChannel mqChannel = null;
        try {
            Channel   channel   = connectionMQ.createChannel();
            mqChannel           = new MqChannel( channel );
    
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
    
        MqConsumer  consumer = new MqConsumer( consumerMQ, mqChannel );
        
        return consumer;
    }
    
    
    public MqConsumer creatMqConsumerTopic( MqEnvProperties.OutputType outputType, String uuid ) throws MqException {
    
    
        String exchangeName = MqEnvProperties.getExchangeName() + "." + MqEnvProperties.getOutputType( outputType );
        if ( exchangeName == null ) {
            logger.logError( "MqConnection", "creatMqConsumerTopic",
                    "ExchangeName Property not specified");
        
            MqException inOutExcp = new MqException( "ExchangeName Property not specified" );
            closeMqConnection( connectionMQ, inOutExcp );
        }
        
        String routingKey = MqEnvProperties.getOutputType( outputType ) + "." + uuid;
        
        MqChannel mqChannel = null;
        QueueingConsumer consumerMQ = null;
        try {
            Channel channel = connectionMQ.createChannel();
            mqChannel       = new MqChannel( channel );
            
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
        
        MqConsumer  consumer = new MqConsumer( consumerMQ, mqChannel );
        
        return consumer;
    }
    
    
    
    public MqPublisher createMqPublisher( String queueName, String uuid ) throws MqException {
    
        MqChannel mqChannel = null;
        try {
            Channel   channel   = connectionMQ.createChannel();
            mqChannel = new MqChannel( channel );
            
            channel.queueDeclare(queueName, false, false, false, null);
        }
        catch (IOException excp ) {
            logger.logException( "MqConnection", "createMqPublisher",
                    "cannot create MQ queue consumer", excp);
    
            MqException inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
    
            closeMqConnection( connectionMQ, inOutExcp );;
        }
            
        return new MqPublisher( mqChannel, queueName, uuid ) ;
    }
    
    
    public MqPublisherTopic createMqPublisherTopic(  MqEnvProperties.OutputType outputType, String uuid ) throws MqException {
    
        String exchangeName = MqEnvProperties.getExchangeName() + "." + MqEnvProperties.getOutputType( outputType );
        if ( exchangeName == null ) {
            logger.logError( "MqConnection", "createMqPublisherTopic",
                     "ExchangeName Property not specified");
    
            MqException inOutExcp = new MqException( "ExchangeName Property not specified" );
            closeMqConnection( connectionMQ, inOutExcp );
        }
        
        String routingKey = routingKey = MqEnvProperties.getOutputType( outputType ) + "." + uuid;
    
        MqChannel mqChannel = null;
        try {
            Channel   channel   = connectionMQ.createChannel();
            mqChannel = new MqChannel( channel );
            
            channel.exchangeDeclare( exchangeName,"topic");
        }
        catch (IOException excp ) {
            logger.logException( "MqConnection", "createMqPublisherTopic",
                    "cannot create MQ queue topic publisher", excp);
            
            MqException inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
            
            closeMqConnection( connectionMQ, inOutExcp );;
        }
        
        return new MqPublisherTopic( mqChannel, exchangeName, routingKey ) ;
    }
    
    
    public void close() throws MqException {
        closeMqConnection( connectionMQ, null );
    }
    
    
    private static void closeMqConnection( Connection connectionMQ, MqException inOutExcp ) throws MqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        if (connectionMQ != null) {
            try {
                if (connectionMQ.isOpen()) {
                    connectionMQ.close();
                }
                connectionMQ = null;
    
                if (inOutExcp != null) {
                    throw inOutExcp;
                }
            }
            catch (Exception excp) {
    
                if (inOutExcp != null) {
                    throw inOutExcp;
                }
                else {
                    logger.logException("MqConnection", "closeMqConnection",
                            "cannot close MQ connection", excp);
        
                    inOutExcp = new MqException("cannot close MQ connection");
                    inOutExcp.initCause(excp);
                    throw inOutExcp;
                }
            }
        }
    }
    
}
