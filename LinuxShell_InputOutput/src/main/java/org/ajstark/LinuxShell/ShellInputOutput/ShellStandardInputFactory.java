package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;
/**
 * Created by Albert on 12/17/16.
 */
public class ShellStandardInputFactory {
    
    private static ShellStandardInputFactory factory;
    
    public static ShellStandardInputFactory getFactory() {
        factory = new ShellStandardInputFactory();
        return factory;
    }
    
    public ShellStandardInput getShellStandardInput( MqEnvProperties.InputType inputOutputType ) throws ShellInputOutputException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        switch ( inputOutputType ) {
            case CONSOLE: {
                return new ShellStandardInputConsole();
            }
            case MQ:
            case MQ_CONSOLE: {
                String queueName       = System.getProperty( "QueueName" );
                if ( queueName == null ) {
                    ShellInputOutputException inOutExcp = new ShellInputOutputException( "missing MQ queue name" );
    
                    logger.logException( "ShellStandardInputFactory", "getShellStandardInput",
                            "missing MQ queue name", inOutExcp);
                    
                    throw inOutExcp;
                }
                
                MqConnection connection = null;
                MqConsumer   consumer   = null;
                
                try {
                    connection = MqConnection.getMqConnection( );
                    consumer   = connection.creatMqConsumer( queueName );
                }
                catch (MqException excp ) {
                    ShellInputOutputException inOutExcp = new ShellInputOutputException( excp.getMessage() );
                    inOutExcp.initCause( excp );
    
                    logger.logException( "ShellStandardInputFactory", "getShellStandardInput",
                            excp.getMessage(), inOutExcp);
    
                    throw inOutExcp;
                }
                
                return new ShellStandardInputMQ( consumer );
            }
            default: {
                return new ShellStandardInputConsole();
            }
        }
    }
    
}
