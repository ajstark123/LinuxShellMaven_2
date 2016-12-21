package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;

/**
 * Created by Albert on 12/17/16.
 */
public class ShellStandardOutputFactory extends ShellStandardOutErrBaseFactory {
    private static ShellStandardOutputFactory factory;
    
    public static ShellStandardOutputFactory getFactory() {
        factory = new ShellStandardOutputFactory();
        return factory;
    }
    
    public ShellStandardOutput getShellStandardOutput( String uuidStr ) throws ShellInputOutputException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        String inputOutputType = System.getProperty( "InputOutputType" );
        
        switch ( inputOutputType ) {
            case "CONSOLE": {
                return new ShellStandardOutputConsole( uuidStr);
            }
            case "MQ": {
                return createShellStandardOutputMQ( uuidStr );
            }
            case "MQ_CONSOLE": {
                ShellStandardOutputList list = new ShellStandardOutputList( uuidStr );
                list.add(  new ShellStandardOutputConsole(uuidStr)  );
                list.add(  createShellStandardOutputMQ(  uuidStr) );
                
                return list;
            }
            default: {
                return new ShellStandardOutputConsole(uuidStr );
            }
        }
    }
    
    private ShellStandardOutputMQ createShellStandardOutputMQ( String uuidStr ) throws ShellInputOutputException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        try {
            MqConnection      connection = MqConnection.getMqConnection();
            MqPublisherTopic  publisher  = connection.createMqPublisherTopic( MqEnvProperties.OutputType.StandardOut,  uuidStr );
        
            return new ShellStandardOutputMQ( uuidStr, publisher);
        }
        catch( Exception excp ) {
            logger.logError("ShellStandardOutputFactory", "ShellStandardOutputMQ",
                    excp.getMessage() );
    
            ShellInputOutputException newExcp = new ShellInputOutputException( excp.getMessage() );
            throw newExcp;
        }
    }
    
}
