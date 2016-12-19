package org.ajstark.LinuxShell.ShellClient;

import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;
import org.ajstark.LinuxShell.ShellInputOutput.*;
import org.ajstark.LinuxShell.org.ajstark.LinuxShell.ClientInputOutput.*;

/**
 * Created by Albert on 12/19/16.
 */
public class MqClientRcvFromShell extends MQClientBase {
    
    private Thread             threadCommand;
    private String             uuid;
    private ReceiveFromShell   receiveFromShell;
    
    private boolean            stoppedFlag;
    
    LinuxShellLogger logger;
    
    /**
     * Created by Albert on 12/18/16.
     *
     * Main driver for the MqClient
     *
     * @version $Id$
     *
     *
     *
     */
    public MqClientRcvFromShell( String uuid, MqEnvProperties.OutputType outputType )  throws Exception {
        this.uuid = uuid;
    
        stoppedFlag = false;
        
        logger = LinuxShellLogger.getLogger();
        
        ShellStandardInputFactory factory = ShellStandardInputFactory.getFactory();
    
    
        receiveFromShell = ReceiveFromShell.getReceiveFromShell( outputType, uuid );
        
        
        threadCommand = new Thread( this );
        threadCommand.start();
    }
    
    
    public void run() {
        while (  ! threadCommand.interrupted() ) {
            try {
                String outStr = receiveFromShell.consume();
                System.out.println("" + outStr);
            }
            catch (ClientMqException excp) {
        
                logger.logException("MqClientRcvFromShell", "run",
                        "cannot read from a MQ queue", excp);
            }
        }
    
    
        receiveFromShell.cleanUp();
        
        logger.logInfo( "MqClientRcvFromShell", "run", "end of method call" );
    }
    
    public Thread getThreadCommand() {
        
        return threadCommand;
    }
    
    public void setStopFlag()  {
        stoppedFlag = true;
    }
    
}
