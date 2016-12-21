package org.ajstark.LinuxShell.ShellClient;

import org.ajstark.LinuxShell.ClientInputOutput.*;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;
import org.ajstark.LinuxShell.ShellInputOutput.*;

/**
 * Created by Albert on 12/18/16.
 */
class MqClient extends MQClientBase {
    
    private Thread             threadCommand;
    private String             uuid;
    
    private ShellStandardInput  standardInput;
    private PublishToShell      publishToShell;
    
    private LinuxShellLogger logger;
    
    private boolean doNotPublishInput;
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
    public MqClient( String uuid  )  throws Exception {
        this.uuid              = uuid;
    
        doNotPublishInput  = false;
        
        logger = LinuxShellLogger.getLogger();
    
        ShellStandardInputFactory factory = ShellStandardInputFactory.getFactory();
        
        standardInput = factory.getShellStandardInput( MqEnvProperties.InputType.CONSOLE, uuid );
        
        publishToShell = PublishToShell.getPublishToShell(  uuid );
    
        threadCommand = new Thread( this );
        threadCommand.start();
    }
    
    
    public void run() {
        boolean continueLooping = true;
        
        int retryCount = 0;
        while (  continueLooping  ) {
            InputOutputData inputOutputData = null;
            
            try {
                inputOutputData = standardInput.getInput();
                inputOutputData.setUuidStr( uuid );
            }
            catch ( ShellInputOutputException excp ) {
                inputOutputData = null;
            }
            
            String inputStr = null;
            if ( inputOutputData != null ) {
                inputStr = inputOutputData.getData();
                
                if ( doNotPublishInput ) {
                    standardInput.cleanUp();
                    publishToShell.cleanUp();
    
                    return;
                }
                
                try {
                    publishToShell.publish( inputStr );
                    retryCount = 0;
                }
                catch (ClientMqException excp) {
                    ++retryCount;
    
                    logger.logException( "MqClient", "run",
                            "cannot publish to a MQ queue", excp );
    
                    if (retryCount == 3) {
                        standardInput.cleanUp();
                        publishToShell.cleanUp();
        
                        return;
                    }
                }
            }
    
            if ( (inputStr.compareTo("END") == 0) || (inputStr.compareTo("KILL THE FUCKING SERVER") == 0) ) {
                continueLooping = false;
            }
        }
        
        standardInput.cleanUp();
        publishToShell.cleanUp();
        
        logger.logInfo( "MqClient", "run", "end of method call" );
    }
    
    
    
    public Thread getThreadCommand() {
        
        return threadCommand;
    }
    
    
    public static String getVersion() {
        String version = "$Id$";
        
        return version;
    }
    
    public void setDoNotPublishInput() {
        doNotPublishInput = true;
    }
    
}
