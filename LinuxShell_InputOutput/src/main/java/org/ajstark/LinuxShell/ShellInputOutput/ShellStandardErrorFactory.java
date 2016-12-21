package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;

/**
 * Created by Albert on 12/17/16.
 */
public class ShellStandardErrorFactory extends ShellStandardOutErrBaseFactory  {
    
    private static ShellStandardErrorFactory factory;
    
    public static ShellStandardErrorFactory getFactory() {
        factory = new ShellStandardErrorFactory();
        return factory;
    }
    
    public ShellStandardError getShellStandardError( String uuidStr ) throws ShellInputOutputException {

    
        String inputOutputType = System.getProperty( "InputOutputType" );
        
        switch ( inputOutputType ) {
            case "CONSOLE": {
                return new ShellStandardErrorConsole( uuidStr);
            }
            case "MQ": {
                return createShellStandardOutputMQ( uuidStr );
            }
            case "MQ_CONSOLE": {
                ShellStandardErrorList list = new ShellStandardErrorList( uuidStr );
                list.add( new ShellStandardErrorConsole(uuidStr) );
                list.add( createShellStandardOutputMQ( uuidStr ) );
                return list;
            }
            default: {
                return new ShellStandardErrorConsole( uuidStr );
            }
        }
    }
    
    private ShellStandardErrorMq createShellStandardOutputMQ( String uuidStr  ) throws ShellInputOutputException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        try {
            MqConnection     connection = getMqConnection( );
            
            MqPublisherTopic publisher  = getMqPublisherTopic( uuidStr, MqEnvProperties.OutputType.StandardErr, connection );
        
            return new ShellStandardErrorMq( uuidStr, connection, publisher);
        }
        catch( ShellInputOutputException excp ) {
            logger.logError("ShellStandardErrorFactory", "createShellStandardOutputMQ",
                    excp.getMessage() );
        
            throw excp;
        }
    }
    
}
