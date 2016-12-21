package org.ajstark.LinuxShell.Shell;

import org.ajstark.LinuxShell.CommandInfrastructure.*;
import org.ajstark.LinuxShell.CommandInfrastructure.Command;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.ShellInputOutput.*;

import org.ajstark.LinuxShell.MQ.*;

import java.util.*;


/**
 * Created by Albert on 11/8/16.
 *
 * @version $Id$
 *
 */
public class LinuxShell  implements Runnable {
    private Thread               threadCommand;

    private CurrentDirectory     currentDirectory;
    private ShellStandardInput   standardInput;
    
    /**
     * Created by Albert on 11/8/16.
     *
     * Main driver for the LinuxShell
     *
     * @version $Id$
     *
     */
    public LinuxShell( ) throws ShellInputOutputException {
        
        ShellStandardInputFactory factory = ShellStandardInputFactory.getFactory();
    
        MqEnvProperties.InputType inputType = MqEnvProperties.getEnvPropertyInputOutputType();
        standardInput = factory.getShellStandardInput( inputType );
        
        Properties envVariables     = new Properties();
        currentDirectory = new CurrentDirectory();

        Properties        systemProperties = System.getProperties();

        String value = systemProperties.getProperty( "user.name" );
        if ( value != null ) {
            envVariables.put( "$USER",    value );
            envVariables.put( "$LOGNAME", value );
        }

        value = systemProperties.getProperty( "user.home" );
        if ( value != null ) {
            envVariables.put( "$HOME", value );
            envVariables.put( "$PWD",  value );
        }

        value = systemProperties.getProperty( "os.name" );
        if ( value != null ) {
            envVariables.put( "$OS", value );
        }
    
        threadCommand = new Thread(this );
        threadCommand.start();
    }


    public void run() {
        int i = 0;
        boolean continueLooping = true;
    
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        int retryCount = 0;
        while (  continueLooping ) {
            InputOutputData inputOutputData = null;
    
            try {
                inputOutputData = standardInput.getInput();
   
                retryCount = 0;
            }
            catch ( ShellInputOutputException excp ) {
                ++retryCount;
                
                if ( retryCount == 3 ) {
                    standardInput.cleanUp();
                    return;
                }
            }
            
            String inputStr = inputOutputData.getData();
            if ( inputStr.compareTo("KILL THE FUCKING SERVER") == 0 ) {
                continueLooping = false;
            }
            else {
                if (retryCount == 0) {
                    try {
                        if  ( (inputStr != null) && (inputStr.compareTo("") != 0) ) {
                            Command cmd = parseInputStr( inputOutputData );
                            if ( cmd != null) {
                                cmd.execute();
                            }
        
                            currentDirectory.addCmdToHistory( inputStr );
                        }
                    }
                    catch (Exception excp) {
                        // do nothing.
                    }
                }
            }
        }
    
        standardInput.cleanUp();
    
        System.err.println( "\n\nGOOD BYE!\n\n" );
        logger.logInfo( "LinuxShell", "run", "end of method call" );
    }
    

    /*
     *  returns a Command object  if the input string was parsed correctly.  otherwise null
     */
    private Command parseInputStr( InputOutputData inputOutputData ) throws ShellException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        CommandParser cmdParser = null;
        Command       cmd       = null;
        try {
            cmdParser = new CommandParser(currentDirectory, inputOutputData );
            cmd       = cmdParser.parse();
        }
        catch ( ShellException excp ) {
            return null;
        }
    
        return cmd;
    }

    private void waitForCmdToFinish( Command cmd ) {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        try {
            boolean continueProcessing = true;

            while (continueProcessing) {
                synchronized (cmd) {
                    Thread threadCommand = cmd.getThreadCommand();
                    threadCommand.join();


                    if (threadCommand.isAlive()) {
                        threadCommand.join();
                    } else {
                        continueProcessing = false;
                    }
                }
            }
        }
        catch (Exception excp) {
            logger.logException( "LinuxShell", "waitForCmdToFinish",
                    "Exception: " + excp.getClass().getName() , excp);
            logger.logError( "LinuxShell", "waitForCmdToFinish", excp.getMessage() );
        }
    }

    public Thread getThreadCommand() {

        return threadCommand;
    }


    public static String getVersion() {
        String version = "$Id$";
        
        return version;
    }
    
    

}
