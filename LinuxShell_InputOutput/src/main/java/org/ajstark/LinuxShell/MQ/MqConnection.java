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
        factory.setUsername(    MqEnvProperties.getUserName() );
        factory.setPassword(    MqEnvProperties.getPassword() );
        factory.setVirtualHost( MqEnvProperties.getVirtualHost() );
        factory.setHost(        MqEnvProperties.getMqBrokerHost() );
        
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
    
    
    public MqConsumer creatMqConsumer() throws MqException {
    
        String queueName = MqEnvProperties.getShellInputQueueName();
    
        QueueingConsumer consumerMQ = null;
        
        MqChannel mqChannel = null;
        try {
            mqChannel = createChannel();
            mqChannel.queueDeclare( queueName );
            
            consumerMQ = mqChannel.createQueueingConsumer();
            mqChannel.basicConsume(queueName, consumerMQ);
        }
        catch (MqException excp ) {
            logger.logException( "MqConnection", "creatMqConsumer",
                    "cannot create MQ queue consumer", excp);
    
            MqException inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
    
            closeMqConnection( connectionMQ, inOutExcp );
        }
    
        MqConsumer  consumer = new MqConsumer( consumerMQ, mqChannel );
        
        return consumer;
    }
    
    
    public MqConsumer creatMqConsumerTopic( MqEnvProperties.OutputType outputType, String uuid ) throws MqException {
    
    
        String exchangeName = MqEnvProperties.getExchangeName( outputType );
        
        String routingKey = MqEnvProperties.getOutputTypeString( outputType ) + "." + uuid;
        

        QueueingConsumer consumerMq = null;
        MqChannel mqChannel = null;
        try {
            mqChannel = createChannel();
            mqChannel.exchangeDeclare( exchangeName );
            
            String queueName = mqChannel.getQueueNameFromQueueDeclare();
            mqChannel.queueBind(queueName, exchangeName, routingKey);
            
            consumerMq = mqChannel.createQueueingConsumer();
    
            mqChannel.basicConsume(queueName, consumerMq);
        }
        catch (MqException excp ) {
            logger.logException( "MqConnection", "creatMqConsumerTopic",
                    "cannot create MQ queue consumer", excp);
            
            MqException inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
    
            closeMqConnection( connectionMQ, inOutExcp );;
        }
        
        
        MqConsumer  consumer = new MqConsumer( consumerMq, mqChannel );
        
        return consumer;
    }
    
    
    
    public MqPublisher createMqPublisher( String uuid ) throws MqException {
    
        String queueName = MqEnvProperties.getShellInputQueueName();
    
        MqChannel mqChannel = null;
        try {
            mqChannel = createChannel();
            mqChannel.queueDeclare( queueName );
        }
        catch (MqException excp ) {
            logger.logException( "MqConnection", "createMqPublisher",
                    "cannot create MQ queue consumer", excp);
    
            closeMqConnection( connectionMQ, excp );;
        }
            
        return new MqPublisher( mqChannel, queueName, uuid ) ;
    }
    
    
    public MqPublisherTopic createMqPublisherTopic(  MqEnvProperties.OutputType outputType, String uuid ) throws MqException {
    
        String exchangeName = MqEnvProperties.getExchangeName( outputType);
        
        String routingKey  = MqEnvProperties.getOutputTypeString( outputType ) + "." + uuid;
    
        MqChannel mqChannel = null;
        try {
            mqChannel = createChannel();
            mqChannel.exchangeDeclare( exchangeName );
        }
        catch (MqException excp ) {
            logger.logException( "MqConnection", "createMqPublisherTopic",
                    "cannot create MQ queue topic publisher", excp);
            
            closeMqConnection( connectionMQ, excp );;
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
    
    private MqChannel createChannel() throws MqException {
    
        Channel channel = null;
        try {
            channel = connectionMQ.createChannel();
        }
        catch (IOException excp ) {
            logger.logException( "MqConnection", "createChannel",
                    "cannot create MQ channel", excp);
    
            MqException inOutExcp = new MqException( "getQueueNameFromQueueDeclare failed" );
            inOutExcp.initCause( excp );
    
            throw inOutExcp;
        }
            
        MqChannel mqChannel = new MqChannel( channel );
        
        return mqChannel;
    }
    
    
}
