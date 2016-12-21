package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;

/**
 * Created by Albert on 12/19/16.
 */
public class ShellStandardOutErrBaseFactory {
    
    
    
    
    public MqConnection getMqConnection( String uuid ) throws ShellInputOutputException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        String queueName = System.getProperty("QueueName");
        if (queueName == null) {
            ShellInputOutputException inOutExcp = new ShellInputOutputException("missing MQ queue name");
            
            logger.logException("ShellStandardOutErrBaseFactory", "getShellStandardMqOutErr",
                    "missing MQ queue name", inOutExcp);
            
            throw inOutExcp;
        }
        
        MqConnection       connection = null;
        
        try {
            connection = MqConnection.getMqConnection( queueName, uuid);
        }
        catch (MqException excp) {
            ShellInputOutputException inOutExcp = new ShellInputOutputException(excp.getMessage());
            inOutExcp.initCause(excp);
            
            logger.logException("ShellStandardOutErrBaseFactory", "getShellStandardMqOutErr",
                    excp.getMessage(), inOutExcp);
            
            throw inOutExcp;
        }
        
        return connection;
    }
    
    
    public MqPublisherTopic getMqPublisherTopic( String                     uuid,
                                                 MqEnvProperties.OutputType outType,
                                                 MqConnection               connection )
            throws ShellInputOutputException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        String queueName = System.getProperty("QueueName");
        if (queueName == null) {
            ShellInputOutputException inOutExcp = new ShellInputOutputException("missing MQ queue name");
            
            logger.logException("ShellStandardOutErrBaseFactory", "getShellStandardMqOutErr",
                    "missing MQ queue name", inOutExcp);
            
            throw inOutExcp;
        }
        MqPublisherTopic   publisher  = null;
        
        try {
            publisher = connection.createMqPublisherTopic( outType );
        }
        catch (MqException excp) {
            ShellInputOutputException inOutExcp = new ShellInputOutputException(excp.getMessage());
            inOutExcp.initCause(excp);
            
            logger.logException("ShellStandardOutErrBaseFactory", "getShellStandardMqOutErr",
                    excp.getMessage(), inOutExcp);
            
            throw inOutExcp;
        }
        
        return publisher;
    }
    
    
    
}
 
    
