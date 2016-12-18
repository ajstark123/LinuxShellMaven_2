package org.ajstark.LinuxShell.ShellClient;

import org.ajstark.LinuxShell.Logger.*;

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
        
        
        try {
            
            MqClient client = new MqClient();
            
            boolean     continueProcessing       = true;
            
            while ( continueProcessing ) {
                synchronized (client) {
                    Thread shellThread = client.getThreadCommand();
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
