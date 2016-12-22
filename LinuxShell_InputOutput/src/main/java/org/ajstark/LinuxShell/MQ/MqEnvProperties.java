package org.ajstark.LinuxShell.MQ;

import org.ajstark.LinuxShell.Logger.*;

import java.util.*;
import java.io.*;

/**
 * Created by Albert on 12/18/16.
 */
public class MqEnvProperties {
    
    static public enum InputType { CONSOLE, MQ, MQ_CONSOLE };
    static public enum OutputType { StandardOut,  StandardErr }
    private static MqEnvProperties mqProps = null;
    
    private String           mqConfigFileName;
    private LinuxShellLogger logger;
    private Properties       mqConfigProps;
    
    public static MqEnvProperties getMqEnvProperties() {
        if ( mqProps!= null ) {
            return mqProps;
        }
    
        mqProps = new MqEnvProperties();
        return mqProps;
    }
    
    private MqEnvProperties() {
        this.logger = LinuxShellLogger.getLogger();
    
        loadMqConfigurations();
    }
    
    
    private void loadMqConfigurations() {
    
        InputType type = getEnvPropertyInputOutputType();
        
        if ( type == InputType.CONSOLE ) {
            return;
        }
    
        mqConfigFileName = System.getProperty( "MqConfigurationFile" );
        if (  mqConfigFileName == null ) {
            logger.logFatal( "MqEnvProperties","loadMqConfigurations",
                    "Fatal Error Missing MqConfigurationFile property" );
            
            System.err.println( "Fatal Error Missing MqConfigurationFile property" );
            System.err.flush();
            
            logger.shutdown();
            System.exit( 1 );
        }
        
        File mqConfigFile = new File( mqConfigFileName );
        if ( !mqConfigFile.exists() ) {
            logger.logFatal( "MqEnvProperties","loadMqConfigurations",
                    "Fatal Error MQ Config File does not exist: " + mqConfigFileName );
    
            System.err.println( "Fatal Error MQ Config File does not exist: " + mqConfigFileName );
            System.err.flush();
    
            logger.shutdown();
            System.exit( 1 );
        }
    
        if (  !mqConfigFile.isFile() ) {
            logger.logFatal( "MqEnvProperties","loadMqConfigurations",
                    "Fatal Error MQ Config File is not a file: " + mqConfigFileName );
        
            System.err.println( "Fatal Error MQ Config File is not a file: " + mqConfigFileName );
            System.err.flush();
        
            logger.shutdown();
            System.exit( 1 );
        }
    
        
        try {
            FileReader fileReader = new FileReader(mqConfigFile);
            mqConfigProps = new Properties( );
            mqConfigProps.load( fileReader );
        }
        catch ( Exception excp ){
            logger.logFatal( "MqEnvProperties","loadMqConfigurations",
                    "Can not load configurations from, Config File: " + mqConfigFileName, excp );
    
            System.err.println( "Can not load configurations from, Config File: " + mqConfigFileName );
            System.err.flush();
    
            logger.shutdown();
            System.exit( 1 );
        }
        
        validateConfigurations( "UserName" );
        validateConfigurations( "Password" );
        validateConfigurations( "VirtualHost" );
        validateConfigurations( "MqBrokerHost" );
        validateConfigurations( "TopicExchangeStdOut" );
        validateConfigurations( "TopicExchangeStdErr" );
        validateConfigurations( "ShellInputQueueName" );
    }
    
    private  void validateConfigurations( String configPropertyName )  {
        String configPropertyValue  = mqConfigProps.getProperty( configPropertyName );
        
        if ( configPropertyValue == null ) {
            logger.logFatal( "MqEnvProperties","validateConfigurations",
                    "missing configuration property value for: " + configPropertyName );
    
            System.err.println( "missing configuration property value for: " + configPropertyName  );
            System.err.flush();
    
            logger.shutdown();
            System.exit( 1 );
        }
    }
    
    private String getProperty( String configPropertyName ) {
        String configPropertyValue  = mqConfigProps.getProperty( configPropertyName );
        return configPropertyValue;
    }
    
    
    public static InputType getEnvPropertyInputOutputType() {
        String inputOutputType = System.getProperty( "InputOutputType" );
        
        if ( inputOutputType.compareTo("MQ") == 0 ) {
            return InputType.MQ;
        }
    
        if ( inputOutputType.compareTo("MQ_CONSOLE") == 0 ) {
            return InputType.MQ_CONSOLE;
        }
        
        return InputType.CONSOLE;
    }
    
    
    public static String getUserName() {
        MqEnvProperties props = getMqEnvProperties();
        
        String queueName       = props.getProperty( "UserName" );
        
        return queueName;
    }

    public static String getPassword() {
        MqEnvProperties props = getMqEnvProperties();
        
        String queueName       = props.getProperty( "Password" );
        
        return queueName;
    }
    
    public static String getVirtualHost() {
        MqEnvProperties props = getMqEnvProperties();
        
        String exchange       = props.getProperty( "VirtualHost" );
        
        return exchange;
    }
    
    public static String getExchangeName( MqEnvProperties.OutputType outputType ) {
        if ( outputType == OutputType.StandardOut ) {
            return getTopicExchangeStdOut();
        }
        
        return getTopicExchangeStdErr();
    }
    
    
    public static String getTopicExchangeStdOut() {
        MqEnvProperties props = getMqEnvProperties();
        
        String exchange       = props.getProperty( "TopicExchangeStdOut" );
        
        return exchange;
    }
    
    public static String getTopicExchangeStdErr() {
        MqEnvProperties props = getMqEnvProperties();
        
        String exchange       = props.getProperty( "TopicExchangeStdErr" );
        
        return exchange;
    }
    
    public static String getMqBrokerHost() {
        MqEnvProperties props = getMqEnvProperties();
        
        String exchange       = props.getProperty( "MqBrokerHost" );
        
        return exchange;
    }
    
    public static String getShellInputQueueName() {
        MqEnvProperties props = getMqEnvProperties();
        
        String queueName       = props.getProperty( "ShellInputQueueName" );
        
        return queueName;
    }
    
    
    public static String getOutputTypeString( MqEnvProperties.OutputType outputType) {
        if ( outputType == OutputType.StandardOut ) {
            return "StandardOut";
        }
        
        return "StandardErr";
    }
    
    
}
