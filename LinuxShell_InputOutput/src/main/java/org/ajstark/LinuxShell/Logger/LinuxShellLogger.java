package org.ajstark.LinuxShell.Logger;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.spi.*;
import org.apache.logging.log4j.core.appender.*;


import java.util.*;
import java.io.*;

/**
 * Created by Albert on 12/16/16.
 */
public class LinuxShellLogger {
    private static LinuxShellLogger logger;
    
    private Logger     log4jLogger;
    
    public static LinuxShellLogger getLogger( ) {
        
        if ( logger != null ) {
            return logger;
        }
        
        Properties props            = System.getProperties();
        String     propertyFileName = props.getProperty( "LoggerConfigurationFile" );
    
        validatePropertyFileName( propertyFileName );
        props.setProperty( "log4j.configurationFile" , propertyFileName );
        
        logger = new LinuxShellLogger(  );
        return logger;
    }
    
    private LinuxShellLogger( ) {
        log4jLogger = LogManager.getLogger(  );
    }
    
    
    public void logInfo( String className, String methodName, String message ) {
        StringBuffer strBuffer = new StringBuffer( ""  ) ;
        strBuffer.append( "Class: " );
        strBuffer.append( className );
        strBuffer.append( " Method: " );
        strBuffer.append( methodName );
        strBuffer.append( " Message: " );
        strBuffer.append( message );
        
        log4jLogger.info( strBuffer.toString() );
    }
    
    
    public void logDebug( String className, String methodName, String message ) {
        StringBuffer strBuffer = new StringBuffer( ""  ) ;
        strBuffer.append( "Class: " );
        strBuffer.append( className );
        strBuffer.append( " Method: " );
        strBuffer.append( methodName );
        strBuffer.append( " Message: " );
        strBuffer.append( message );
        
        log4jLogger.debug( strBuffer.toString() );
    }
    
    
    
    public void logError( String className, String methodName, String message ) {
        StringBuffer strBuffer = new StringBuffer( ""  ) ;
        strBuffer.append( "Class: " );
        strBuffer.append( className );
        strBuffer.append( " Method: " );
        strBuffer.append( methodName );
        strBuffer.append( " Message: " );
        strBuffer.append( message );
    
        log4jLogger.error( message );
    }
    
    public void logFatal( String className, String methodName, String message ) {
        StringBuffer strBuffer = new StringBuffer( ""  ) ;
        strBuffer.append( "Class: " );
        strBuffer.append( className );
        strBuffer.append( " Method: " );
        strBuffer.append( methodName );
        strBuffer.append( " Message: " );
        strBuffer.append( message );
    
        log4jLogger.fatal( message );
    }
    
    
    public void logException( String className, String methodName, String message, Exception excp ) {
        StringBuffer strBuffer = new StringBuffer( ""  ) ;
        strBuffer.append( "Class: " );
        strBuffer.append( className );
        strBuffer.append( " Method: " );
        strBuffer.append( methodName );
        strBuffer.append( " Message: " );
        strBuffer.append( message );
    
        log4jLogger.error( message, excp );
    }
    
    public void logFatal( String className, String methodName, String message, Exception excp ) {
        StringBuffer strBuffer = new StringBuffer( ""  ) ;
        strBuffer.append( "Class: " );
        strBuffer.append( className );
        strBuffer.append( " Method: " );
        strBuffer.append( methodName );
        strBuffer.append( " Message: " );
        strBuffer.append( message );
        
        log4jLogger.fatal( message, excp );
    }
    
    
    
    
    public void shutdown() {
        LogManager.shutdown();
    }
    
    
    private static void validatePropertyFileName( String propertyFileName ) {
        if ( (propertyFileName == null)  ) {
            LoggerConfigurationException loggerExcp =
                    new LoggerConfigurationException( "LoggerConfigurationFile property not specified on the command line" );
            throw loggerExcp;
        }
    
        File file = new File( propertyFileName );
        if ( ! file.exists() ) {
            LoggerConfigurationException loggerExcp =
                    new LoggerConfigurationException( "LoggerConfigurationFile property: " + propertyFileName + " does not exist" );
            throw loggerExcp;
        }
    
        if ( ! file.isFile() ) {
            LoggerConfigurationException loggerExcp =
                    new LoggerConfigurationException( "LoggerConfigurationFile property: " + propertyFileName + " is not a file" );
            throw loggerExcp;
        }
    }
    
     
     
}
