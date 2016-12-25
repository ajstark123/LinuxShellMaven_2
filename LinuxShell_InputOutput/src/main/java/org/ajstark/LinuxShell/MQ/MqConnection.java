package org.ajstark.LinuxShell.MQ;

import com.rabbitmq.client.*;

import org.ajstark.LinuxShell.Logger.*;


import java.io.*;
import java.util.concurrent.*;

import java.util.*;

/**
 * Created by Albert on 12/18/16.
 */
public class MqConnection implements ShutdownListener {
    
    private static MqConnection mqConnection = null;
    
    private ScheduledExecutorService executor;
    
    private volatile Connection          connection;
    
    private LinuxShellLogger    logger;
    
    private MqConnection( )  {
        this.connection = null;
    
        this.logger       = LinuxShellLogger.getLogger();
        
        this.executor     = Executors.newSingleThreadScheduledExecutor();
    }
    
    private void setMqConnection( Connection connection ) {
        this.connection = connection;
    }
    
    public static MqConnection getMqConnection( ) throws MqException {
        
        if ( mqConnection != null ) {
            return mqConnection;
        }
    
        mqConnection = new MqConnection( );
        Connection       connection;
        
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        MqException inOutExcp = null;
        connection = null;
        try {
            connection = mqConnection.createMqConnection(  );
        }
        catch ( Exception excp ) {
            logger.logException( "MqConnection", "getMqConnection",
                    "cannot create MQ queue consumer", excp);
        
            inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
            
            closeMqConnection( connection, inOutExcp );
        }
    
        //mqConnection = new MqConnection( connection );
        mqConnection.setMqConnection( connection );
        
        return mqConnection;
    }
    
    
    public MqConsumer creatMqConsumer() throws MqException {
    
        String queueName = MqEnvProperties.getShellInputQueueName();
    
        QueueingConsumer consumerMQ = null;
        
        MqChannel mqChannel = null;
        try {
            mqChannel = createChannel();
            if ( mqChannel != null ) {
                mqChannel.queueDeclare(queueName);
    
                consumerMQ = mqChannel.createQueueingConsumer();
                mqChannel.basicConsume(queueName, consumerMQ);
            }
        }
        catch (MqException excp ) {
            logger.logException( "MqConnection", "creatMqConsumer",
                    "cannot create MQ queue consumer", excp);
    
            MqException inOutExcp = new MqException( "cannot create MQ queue consumer" );
            inOutExcp.initCause( excp );
    
            closeMqConnection( connection, inOutExcp );
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
            if (mqChannel == null ){
                return null;
            }
            
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
    
            closeMqConnection( connection, inOutExcp );;
        }
        
        
        MqConsumer  consumer = new MqConsumer( consumerMq, mqChannel );
        
        return consumer;
    }
    
    
    
    public MqPublisher createMqPublisher( String uuid ) throws MqException {
    
        String queueName = MqEnvProperties.getShellInputQueueName();
    
        MqChannel mqChannel = null;
        try {
            mqChannel = createChannel();
            if ( mqChannel == null ) {
                return null;
            }
            mqChannel.queueDeclare( queueName );
        }
        catch (MqException excp ) {
            logger.logException( "MqConnection", "createMqPublisher",
                    "cannot create MQ queue consumer", excp);
    
            closeMqConnection( connection, excp );;
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
            
            closeMqConnection( connection, excp );;
        }
        
        return new MqPublisherTopic( mqChannel, exchangeName, routingKey ) ;
    }
    
    
    public void close() throws MqException {
        closeMqConnection( connection, null );
    }
    
    
    private static void closeMqConnection( Connection connection, MqException inOutExcp ) throws MqException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        if (connection != null) {
            try {
                if (connection.isOpen()) {
                    connection.close();
                }
                connection = null;
    
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
        else {
            if ( inOutExcp  != null ) {
                throw inOutExcp;
            }
        }
    }
    
    private MqChannel createChannel() throws MqException {
    
        if ( connection == null ) {
            try {
                connection = createMqConnection();
            }
            catch ( MqException excp ) {
                logger.logException( "MqConnection", "createChannel",
                        "cannot create Connection", excp);
    
                return null;
            }
        }
        
        Channel channel = null;
        try {
            channel = connection.createChannel();
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
            connection = createMqConnection(  );
        }
        catch ( Exception excp ) {
            logger.logException( "MqConnection", "start",
                    "cannot create MQ queue consumer", excp);
    
            asyncWaitAndReconnect();
        }
    }
    
    
    private Connection createMqConnection(  ) throws MqException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(    MqEnvProperties.getUserName() );
        factory.setPassword(    MqEnvProperties.getPassword() );
        factory.setVirtualHost( MqEnvProperties.getVirtualHost() );
        factory.setHost(        MqEnvProperties.getMqBrokerHost() );
    
        // Disable Topology recovery.  Topology recovery involves recovery of exchanges, queues, bindings and
        // consumers.
        factory.setTopologyRecoveryEnabled( false );
    
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        connection = null;
        try {
            connection = factory.newConnection();
            connection.addShutdownListener( this );
        }
        catch ( Exception excp ) {
            logger.logException( "MqConnection", "createMqConnection",
                    "cannot create MQ connection", excp);
    
            MqException inOutExcp = new MqException( "cannot create MQ connection.addShutdownListener( this );" );
            inOutExcp.initCause( excp );
            
            throw inOutExcp;
        }
        
        return connection;
    }
    
    public void shutdownCompleted(  ShutdownSignalException cause ) {
        // reconnect only on on unexpected errors
        if ( ! cause.isInitiatedByApplication() ){
            logger.logException( "MqConnection", "shutDownComplete",
                    "lost MQ connection", cause);
    
            asyncWaitAndReconnect();
        }
        else {
            executor.shutdown();
        }
    }
    
    
    public void stop() {
        try {
            logger.logInfo( "MqConnection", "stop",
                    "stop method called" );
            closeMqConnection(connection, null);
        }
        catch (Exception excp) {
                // do nothing
                logger.logException( "MqConnection", "stop",
                        "cannot close MQ connection", excp);
        }
    }
    
    
}
