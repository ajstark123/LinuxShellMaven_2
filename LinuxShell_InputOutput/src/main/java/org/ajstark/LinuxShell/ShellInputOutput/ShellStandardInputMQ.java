package org.ajstark.LinuxShell.ShellInputOutput;

import java.io.*;
import java.util.concurrent.*;

import com.rabbitmq.client.*;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;

/**
 * Created by Albert on 12/17/16.
 */
public class ShellStandardInputMQ implements ShellStandardInput {
    private MqConsumer    consumer;
    private MqConnection  connection;
    
    ShellStandardInputMQ( MqConnection connection, MqConsumer consumer )  {
         this.connection = connection;
         this.consumer   = consumer;
    }
    
    
    public InputOutputData getInput() throws ShellInputOutputException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();

        try {
            InputOutputData inputData = consumer.getInput();
    
            return inputData;
        }
        catch ( MqException excp ) {
            logger.logException( "ShellStandardInputMQ", "getInput",
                    "cannot read from queue", excp);
    
            ShellInputOutputException inOutExcp = new ShellInputOutputException( "cannot read from queue" );
            inOutExcp.initCause( excp );
            throw inOutExcp;
        }
    }
    
    
    public void cleanUp() {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        if ( connection != null) {
            try {
                connection.close();
            }
            catch ( Exception excp ) {
                logger.logException("ShellStandardInputMQ", "cleanUp",
                        "cannot close MQ connectionr", excp);
            }
        }
    
        connection = null;
    }
    
    
    
    
}
