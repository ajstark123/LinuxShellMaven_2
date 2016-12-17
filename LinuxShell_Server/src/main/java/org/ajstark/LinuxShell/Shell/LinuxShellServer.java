package org.ajstark.LinuxShell.Shell;


import org.ajstark.LinuxShell.Logger.*;

import java.text.*;
import java.util.*;

/**
 * Created by Albert on 11/4/16.
 *
 * @version $Id$
 *
 */
public class LinuxShellServer {

    public static void main(String[] args) {
    
        LinuxShellLogger logger = null;
        try {
            logger = LinuxShellLogger.getLogger();
        }
        catch ( LoggerConfigurationException excp ) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm:ss");
            Date date = new Date();
            
            System.err.println( "Date: " + dateFormat.format( date ) );
            System.err.print( "Logger exception: " + excp.getMessage() );
            excp.printStackTrace( System.err );
            return;
        }
    
        logger.logInfo( "LinuxShellServer", "main", "test of writing an error message to a file");

        System.out.println( "LinuxShell Version: " + LinuxShell.getVersion() );
        System.out.println( "TestDrive  Version: " + LinuxShellServer.getVersion() );
        
 
        
        Thread mainThread = Thread.currentThread();


        // CommandParser cmdParser = new CommandParser( "  ls -ld * | grep -i Roscoe  ");
        // CommandParser cmdParser = new CommandParser( "  cd /Users/Albert/Documents/Development/IdeaProjects/LinuxShell/src/org/ajstark/LinuxShell/Command ; pwd; ls -ld * | grep -i Command  ");
        // CommandParser cmdParser = new CommandParser( "  cd /Users/Albert/Documents/Development/IdeaProjects/LinuxShell/src1  ");
        // CommandParser cmdParser = new CommandParser( "  cd /Users/Albert/Documents/Development/IdeaProjects/LinuxShell/LinuxShell.iml  ");

        try {

            LinuxShell shell = new LinuxShell();

            boolean     continueProcessing       = true;

            while ( continueProcessing ) {
                synchronized (shell) {
                    Thread shellThread = shell.getThreadCommand();
                    if ( shellThread.isAlive() ) {
                        shellThread.join();
                    }
                    else {
                        continueProcessing = false;
                    }
                }
            }

        }
        catch( Exception excp ) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm:ss");
            Date date = new Date();
    
            System.err.println( "Date: " + dateFormat.format( date ) );
            logger.logException( "LinuxShellServer", "main", "test of writing an error message to a file", excp );
    
            System.err.println( "Exception:                   " + excp.getClass().getName() );
            System.err.println( "Get Message:                 " + excp. getMessage() );

            System.err.println( "" );
            System.err.println( "" );
            excp.printStackTrace();
        }
    
        logger.logInfo( "LinuxShellServer", "main", "end of method call");
        logger.shutdown();

    }
    
    public static String getVersion() {
        String version = "$Id$";
        
        return version;
    }
    
}
