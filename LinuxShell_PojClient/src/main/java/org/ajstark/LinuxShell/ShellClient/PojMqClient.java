package org.ajstark.LinuxShell.ShellClient;

import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;

import java.text.*;
import java.util.*;

/**
 * Created by Albert on 12/18/16.
 */
public class PojMqClient {
    
    public static void main(String[] args) {
        
        LinuxShellLogger logger = null;
        try {
            logger = LinuxShellLogger.getLogger();
        }
        catch ( LoggerConfigurationException excp ) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm:ss");
            Date             date       = new Date();
            
            System.err.println( "Date: " + dateFormat.format( date ) );
            System.err.print( "Logger exception: " + excp.getMessage() );
            excp.printStackTrace( System.err );
            return;
        }
        
        logger.logInfo( "LinuxShellServer", "main", "test of writing an error message to a file");
        
        //System.out.println( "LinuxShell Version: " + LinuxShell.getVersion() );
        //System.out.println( "TestDrive  Version: " + LinuxShellServer.getVersion() );
        
        
        
        Thread mainThread = Thread.currentThread();
    
        MqClient              client    = null;
        MqClientRcvFromShell  rcvStdOut = null;
        MqClientRcvFromShell  rcvStdErr = null;
    
        UUID   uuidObj   = UUID.randomUUID();
        String uuid      = uuidObj.toString();
        System.out.println( "uuid: " +  uuid );
    
    
        try {
            client     = new MqClient(  uuid );
            rcvStdOut  = new MqClientRcvFromShell( uuid, MqEnvProperties.OutputType.StandardOut );
            rcvStdErr  = new MqClientRcvFromShell( uuid, MqEnvProperties.OutputType.StandardErr );
        }
        catch( Exception excp ) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm:ss");
            Date date = new Date();
            
            System.err.println( "Date: " + dateFormat.format( date ) );
            logger.logException( "PojMqClient", "main", "creation of threads failed ", excp );
            
            System.err.println( "Exception:                   " + excp.getClass().getName() );
            System.err.println( "Get Message:                 " + excp. getMessage() );
            
            System.err.println( "" );
            System.err.println( "" );
            excp.printStackTrace();
        }
    
        waitForChildThreadToEnd( client );
    
        Thread rcvStdOutThread = rcvStdOut.getThreadCommand();
        System.out.println( "\n\nbefore rcvStdOutThread.interrupt\n\n" );
        rcvStdOutThread.interrupt();
        System.out.println( "after rcvStdOutThread.interrupt" );
    
        Thread rcvStdErrThread = rcvStdErr.getThreadCommand();
        System.out.println( "\n\nbefore rcvStdErrThread.interrupt\n\n" );
        rcvStdErrThread.interrupt();
        System.out.println( "\n\nafter rcvStdErrThread.interrupt\n\n" );
        
        System.err.println( "\n\nend of main" );
    
        logger.logInfo( "LinuxShellServer", "main", "end of method call");
        logger.shutdown();
        
    }
    
    
    private static void waitForChildThreadToEnd( MQClientBase childThread ) {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        try {
        
            boolean     continueProcessing       = true;
        
            while ( continueProcessing ) {
                synchronized (childThread) {
                    Thread shellThread = childThread.getThreadCommand();
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
        
    }
    
    
    
    
    public static String getVersion() {
        String version = "$Id$";
        
        return version;
    }
    
    
    
    
    
}
