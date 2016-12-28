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
    
    private ShellStandardInputFactory factory;
    
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
    public MqClient( String uuid  )  {
        this.uuid              = uuid;
    
        doNotPublishInput  = false;
        
        logger = LinuxShellLogger.getLogger();
    
        factory = ShellStandardInputFactory.getFactory();
        
        threadCommand = new Thread( this );
        threadCommand.start();
    }
    
    
    public void run() {
        boolean continueLooping = true;
    
        System.out.print( "<<  " );
        int retryCount = 0;
        while (  continueLooping  ) {
            InputOutputData inputOutputData = null;
            
            try {
                if ( standardInput == null ) {
                    standardInput = factory.getShellStandardInput(MqEnvProperties.InputType.CONSOLE);
                }
                
                inputOutputData = standardInput.getInput();
                inputOutputData.setUuidStr( uuid );
            }
            catch ( ShellInputOutputException excp ) {
                inputOutputData = null;
    
                try {
                    publishToShell = PublishToShell.getPublishToShell(uuid);
                }
                catch ( Exception excp0 ) {
                    logger.logException( "MqClient", "run",
                            "could not create a new factory, after sleeping 15 seconds", excp0);
                }
    
            }
            
            String inputStr = null;
            if ( inputOutputData != null ) {
                inputStr = inputOutputData.getData();
    
                if (doNotPublishInput) {
                    standardInput.cleanUp();
                    publishToShell.cleanUp();
        
                    return;
                }
    
    
                if ((inputStr.compareTo("END") == 0) || (inputStr.compareTo("KILL THE FUCKING SERVER") == 0)) {
                    continueLooping = false;
                }
    
                if (inputStr.compareTo("END") != 0) {
                    try {
                        if (publishToShell == null) {
                            publishToShell = PublishToShell.getPublishToShell(uuid);
                        }
                        publishToShell.publish(inputStr);
                    }
                    catch (ClientMqException excp) {
                        logger.logException("MqClient", "run",
                                "cannot publish to a MQ queue", excp);
            
                        try {
                            threadCommand.sleep(15000);
                        }
                        catch (Exception excp1) {
                            // do nothing
                        }
            
                        try {
                            publishToShell = PublishToShell.getPublishToShell(uuid);
                        }
                        catch (Exception excp0) {
                            logger.logException("MqClient", "run",
                                    "could not create a new factory, after sleeping 15 seconds", excp0);
                        }
            
                    }
                }
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
