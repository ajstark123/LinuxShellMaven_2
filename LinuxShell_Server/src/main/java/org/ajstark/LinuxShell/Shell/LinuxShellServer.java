package org.ajstark.LinuxShell.Shell;


import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;
import org.ajstark.LinuxShell.Util.*;

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
        ThreadGroup  mainThreadGroup = new ThreadGroup( "LinuxShellServer main method" );

        try {

            LinuxShell shell = new LinuxShell( mainThreadGroup );
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
    
    
        MqConnection mqConnection = null;
        try {
            mqConnection = MqConnection.getMqConnection();
            mqConnection.closeAndShutdown();
        }
        catch( MqException excp ) {
            logger.logException( "LinuxShellServer", "main",  "can not close connection andshutdown the executor", excp );
            
            System.err.println( "can not close connection andshutdown the executor" );
            excp.printStackTrace( System.err );
            System.err.flush();
        }
    
        
        if ( mqConnection != null ) {
            logger.logInfo("LinuxShellServer", "main", "is mqConnection still open: " + mqConnection.isConnectionOpen());
            System.err.println( "\n\nis mqConnection still open: " + mqConnection.isConnectionOpen());
            System.err.flush();
        }
    
    
    
    
    
        logger.logInfo( "LinuxShellServer", "main", "end of method call");
        logger.shutdown();
    
        System.err.println( "end of method call" );
        System.err.flush();
    
    
        ThreadGroupPrint.printThreadgroup( mainThreadGroup );
        
        
        
        System.exit( 0 );
    }
    
    public static String getVersion() {
        String version = "$Id$";
        
        return version;
    }
    
}
