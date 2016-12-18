package org.ajstark.LinuxShell.ShellClient;

import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;
import org.ajstark.LinuxShell.ShellInputOutput.*;

import org.ajstark.LinuxShell.org.ajstark.LinuxShell.ClientInputOutput.*;

import java.util.*;

/**
 * Created by Albert on 12/18/16.
 */
class MqClient implements Runnable {
    
    private Thread             threadCommand;
    
    
    private ShellStandardInput  standardInput;
    private PublishToShell      publishToShell;
    
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
    public MqClient()  throws Exception {
        logger = LinuxShellLogger.getLogger();
    
        ShellStandardInputFactory factory = ShellStandardInputFactory.getFactory();
        
        standardInput = factory.getShellStandardInput( MqEnvProperties.InputType.CONSOLE );
        
        publishToShell = PublishToShell.getPublishToShell();

        
        threadCommand = new Thread( this );
        threadCommand.start();
    }
    
    
    public void run() {
        
        boolean continueLooping = true;
        
        int retryCount = 0;
        while (  continueLooping ) {
            InputOutputData inputOutputData = null;
            
            try {
                inputOutputData = standardInput.getInput();
            }
            catch ( ShellInputOutputException excp ) {
                inputOutputData = null;
            }
            
            String inputStr = null;
            if ( inputOutputData != null ) {
                inputStr = inputOutputData.getData();
    
                System.out.println("inStr: " + inputStr);
    
                try {
                    publishToShell.publish(inputStr);
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
    
            if ( inputStr.compareTo("END") == 0 ) {
                continueLooping = false;
            }
        }
        
        // standardError.sendError( "\n\nGOOD BYE!!!\n\n"  );
        
        System.err.println( "\n\nGOOD BYE!!!\n\n"  );
        
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
    
    
    
}
