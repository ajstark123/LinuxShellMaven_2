package org.ajstark.LinuxShell.CommandInfrastructure;

import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.ShellInputOutput.*;

/**
 * Created by Albert on 12/28/16.
 *
 * used to seqenize the output from the various command groups
 */
public class OutputCommand extends BaseCommand {
    
    
    public void parse( EnvironmentVariables envVar, boolean stdInFromPipe ) throws CommandParsingException {
        // do nothing - nothing to parse
    }
    
    public void processCommandData( InputOutputData data ) {
        LinuxShellLogger logger         = LinuxShellLogger.getLogger();
        Thread           threadCommand  = Thread.currentThread();
        String           tempThreadName = threadCommand.getName();
        logger.logInfo( "OutputCommand", "processCommandData", "Start of thread: " + tempThreadName );
    
        StandardOut     stdOut = getStandardOutput();
    
        if ( stdOut != null ) {
    
            if ( ! data.isLastDataSent() ) {
                stdOut.put(data);
            }
        }
    
        logger.logInfo( "OutputCommand", "processCommandData", "End of thread: " + tempThreadName );
    }
}
