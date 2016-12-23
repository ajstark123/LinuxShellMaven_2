package org.ajstark.LinuxShell.ShellClient;

import org.ajstark.LinuxShell.ClientInputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;
import org.ajstark.LinuxShell.ShellInputOutput.*;

/**
 * Created by Albert on 12/19/16.
 */
public class MqClientRcvFromShell extends MQClientBase {
    private MqClient           publisherToShell;
    private Thread             threadCommand;
    private ReceiveFromShell   receiveFromShell;
    private String             uuid;
    
    private MqEnvProperties.OutputType outputType;
    
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
    public MqClientRcvFromShell( String uuid, MqEnvProperties.OutputType outputType, MqClient publisherToShell )  throws Exception {
        this.publisherToShell  = publisherToShell;
        this.uuid              = uuid;
        this.outputType        = outputType;
        
        logger = LinuxShellLogger.getLogger();
        
        ShellStandardInputFactory factory = ShellStandardInputFactory.getFactory();
    
    
        receiveFromShell = null;
    
        threadCommand = new Thread( this );
        threadCommand.start();
    }
    
    
    public void run() {
        boolean continueLooping = true;
        while ( continueLooping ) {
            try {
                if ( receiveFromShell == null ) {
                    receiveFromShell = ReceiveFromShell.getReceiveFromShell( outputType, uuid );
                }
                String outStr = receiveFromShell.consume();
                
                if ( outStr != null ) {
                    System.out.println("" + outStr);
    
                    if (outStr.compareTo("END") == 0) {
                        publisherToShell.setDoNotPublishInput();
                        continueLooping = false;
                    }
                }
            }
            catch (ClientMqException excp) {
                logger.logException("MqClientRcvFromShell", "run",
                        "cannot read from a MQ queue", excp);
    
                try {
                    threadCommand.sleep(15000 );
                }
                catch ( Exception excp1 ) {
                    // do nothing
                }
    
                try {
                    receiveFromShell = ReceiveFromShell.getReceiveFromShell( outputType, uuid );
                }
                catch ( Exception excp0 ) {
                    logger.logException( "MqClientRcvFromShell", "run",
                            "could not create a new factory, after sleeping 15 seconds", excp0);
                }
            }
        }
        
        receiveFromShell.cleanUp();
        
        logger.logInfo( "MqClientRcvFromShell", "run", "end of method call" );
        System.out.println( "\n\nend run " + "MqClient " + MqEnvProperties.getOutputTypeString(outputType) );
    }
    
    public Thread getThreadCommand() {
        
        return threadCommand;
    }
    

}
