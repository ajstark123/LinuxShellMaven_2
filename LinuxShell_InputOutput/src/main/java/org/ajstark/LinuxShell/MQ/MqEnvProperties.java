package org.ajstark.LinuxShell.MQ;

/**
 * Created by Albert on 12/18/16.
 */
public interface MqEnvProperties {
    
    static public enum InputType { CONSOLE, MQ };
    
    public static InputType getEnvPropertyInputOutputType() {
        String inputOutputType = System.getProperty( "InputOutputType" );
        
        if ( inputOutputType.compareTo("MQ") == 0 ) {
            return InputType.MQ;
        }
        
        return InputType.CONSOLE;
    }
    
    
    public static String getShellInputQueueName() {
        String queueName       = System.getProperty( "QueueName" );
        
        return queueName;
    }
}
