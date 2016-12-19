package org.ajstark.LinuxShell.MQ;

/**
 * Created by Albert on 12/18/16.
 */
public interface MqEnvProperties {
    
    static public enum InputType { CONSOLE, MQ, MQ_CONSOLE };
    
    static public enum OutputType { StandardOut,  StandardErr }
    
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
    
    public static String getShellInputQueueName() {
        String queueName       = System.getProperty( "QueueName" );
        
        return queueName;
    }
    

    public static String getExchangeName() {
        String exchange       = System.getProperty( "ExchangeName" );
        
        return exchange;
    }

    
    public static String getOutputType( MqEnvProperties.OutputType outputType) {
        if ( outputType == OutputType.StandardOut ) {
            return "StandardOut";
        }
        
        return "StandardErr";
    }
    
    
}
