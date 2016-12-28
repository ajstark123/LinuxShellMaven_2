package org.ajstark.LinuxShell.Shell;

import org.ajstark.LinuxShell.CommandInfrastructure.*;
import org.ajstark.LinuxShell.CommandInfrastructure.Command;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.ShellInputOutput.*;

import org.ajstark.LinuxShell.MQ.*;
import org.ajstark.LinuxShell.Util.*;

import java.util.*;


/**
 * Created by Albert on 11/8/16.
 *
 * @version $Id$
 *
 */
public class LinuxShell  implements Runnable {
    private Thread                    threadCommand;
    private ThreadGroup               mainThreadGroup;
 
    private CurrentDirectory          currentDirectory;
    private ShellStandardInput        standardInput;
    private ShellStandardInputFactory factory;
    
    private LinuxShellLogger logger;
    
    /**
     * Created by Albert on 11/8/16.
     *
     * Main driver for the LinuxShell
     *
     * @version $Id$
     *
     */
    public LinuxShell( ThreadGroup  mainThreadGroup ) {
        this.mainThreadGroup = mainThreadGroup;
    
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        factory = ShellStandardInputFactory.getFactory();
        
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
    
        
        threadCommand = new Thread( mainThreadGroup, this, "Linux Shell" );
        threadCommand.start();
    }


    public void run() {
        int i = 0;
        boolean continueLooping = true;
    
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        
        while (  continueLooping ) {
            InputOutputData inputOutputData = readInput();
            
            if ( inputOutputData != null ) {
                String inputStr = inputOutputData.getData();
                
                if (inputStr.compareTo("KILL THE FUCKING SERVER") == 0) {
                    continueLooping = false;
                }
                else {
                    try {
                        if ((inputStr != null) && (inputStr.compareTo("") != 0)) {
                            Command cmd = parseInputStr(inputOutputData);
                            if (cmd != null) {
                                cmd.execute();
                            }
            
                            currentDirectory.addCmdToHistory(inputStr);
                        }
                    }
                    catch (Exception excp) {
                        // do nothing.
                    }
                }
            }
            else {
                logger.logError( "LinuxShell", "run",
                        "standardInput.getInput() returned a null (inputOutputData)");
            }
        }
    
        standardInput.cleanUp();
    
        System.err.println( "\n\nGOOD BYE!\n\n" );
        logger.logInfo( "LinuxShell", "run", "end of method call" );
    }
    

    private InputOutputData readInput() {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        MqEnvProperties.InputType inputType = MqEnvProperties.getEnvPropertyInputOutputType();
        
        InputOutputData inputOutputData = null;
        try {
            if (standardInput == null) {
                standardInput = factory.getShellStandardInput( inputType );
            }
            inputOutputData = standardInput.getInput();
        }
        catch ( ShellInputOutputException excp ) {
            logger.logException( "LinuxShell", "run",
                    "cannot read from MQ, sleep 15 seconds", excp);
        
            try {
                threadCommand.sleep(15000 );
            }
            catch ( Exception excp1 ) {
                // do nothing
            }
        
            try {
                standardInput = factory.getShellStandardInput(inputType);
            }
            catch ( Exception excp0 ) {
                logger.logException( "LinuxShell", "run",
                        "could not create a new factory, after sleeping 15 seconds", excp0);
            }
        }
        
        return inputOutputData;
    }
    
    
    
    /*
     *  returns a Command object  if the input string was parsed correctly.  otherwise null
     */
    private Command parseInputStr( InputOutputData inputOutputData ) throws ShellException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        CommandParser cmdParser = null;
        Command       cmd       = null;
        try {
            cmdParser = new CommandParser(currentDirectory, inputOutputData, mainThreadGroup );
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
