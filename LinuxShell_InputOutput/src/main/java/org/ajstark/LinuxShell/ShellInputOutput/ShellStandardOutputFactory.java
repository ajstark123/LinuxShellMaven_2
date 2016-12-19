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
                return createShellStandardOutputMQ(  uuidStr );
            }
            case "MQ_CONSOLE": {
                ShellStandardOutputList list = new ShellStandardOutputList( uuidStr );
                list.add(  new ShellStandardOutputConsole(uuidStr)  );
                list.add(  createShellStandardOutputMQ(uuidStr) );
                
                return list;
            }
            default: {
                return new ShellStandardOutputConsole(uuidStr );
            }
        }
    }
    
    private ShellStandardOutputMQ createShellStandardOutputMQ( String uuidStr ) throws ShellInputOutputException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        ShellStandardOutputMQ outMq = null;
        try {
            MqConnection      connection = getMqConnection( uuidStr );
            MqPublisherTopic  publisher  = getMqPublisherTopic( uuidStr, MqEnvProperties.OutputType.StandardOut, connection );
        
            return new ShellStandardOutputMQ( uuidStr, connection, publisher);
        }
        catch( ShellInputOutputException excp ) {
            logger.logError("ShellStandardOutputFactory", "ShellStandardOutputMQ",
                    excp.getMessage() );
        
            throw excp;
        }
    }
    
}
