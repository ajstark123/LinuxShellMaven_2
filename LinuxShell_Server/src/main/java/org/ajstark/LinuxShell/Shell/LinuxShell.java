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
    private ShellStandardOutput  standardOutput;
    private ShellStandardError   standardError;
    
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
    
        UUID                      uuidObj   = UUID.randomUUID();
        String                    uuid      = uuidObj.toString();
        MqEnvProperties.InputType inputType = MqEnvProperties.getEnvPropertyInputOutputType();
        standardInput = factory.getShellStandardInput( inputType, uuid );
    
        standardOutput = null;
        standardError  = null;
        
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
                    handleClosingStandaInOut();
                    return;
                }
            }
            
            try {
                String uuidStr = inputOutputData.getUuidStr();
                
                ShellStandardOutputFactory ouputFactory = ShellStandardOutputFactory.getFactory();
                standardOutput = ouputFactory.getShellStandardOutput( uuidStr );
    
                ShellStandardErrorFactory  errorFactory = ShellStandardErrorFactory.getFactory();
                standardError = errorFactory.getShellStandardError( uuidStr );
            }
            catch ( ShellInputOutputException excp ) {
                logger.logException( "LinuxShell", "run",
                        "Cannot create StandardInput or StandardOut: " + inputOutputData.getData() , excp);
                System.err.println( "Cannot create StandardInput or StandardOut: " + inputOutputData.getData() );
                excp.printStackTrace( System.err );
                System.err.flush();
    
                handleClosingStandaInOut();
                return;
            }
    
            String inputStr = inputOutputData.getData();
            if ( inputStr.compareTo("END") == 0 ) {
                // clent is stopping
                // send end message back to the client to stop topic threads
                endEndMessageClient(  );
            }
            else {
                if (retryCount == 0) {
                    continueLooping = processInput(inputOutputData);
                }
            }
        }
    
        standardInput.cleanUp();
    
        
        endEndMessageClient( );
    
        System.err.println( "\n\nGOOD BYE!\n\n" );
        logger.logInfo( "LinuxShell", "run", "end of method call" );
    }
    
    
    private void endEndMessageClient() {
        InputOutputData lastdata        = new InputOutputData();
        InputOutputData inputOutputData = new InputOutputData( "END" );
        
        if ( standardOutput != null ) {
            standardOutput.put( inputOutputData );
            standardOutput.put( lastdata );
            
            Thread.yield();
            
            standardOutput.cleanUp();
        }
    
        if ( standardError != null ) {
            standardError.put( inputOutputData );
            standardOutput.put( lastdata );
    
            Thread.yield();
            
            standardError.cleanUp();
        }
    }
    
    
    
    
    private void handleClosingStandaInOut() {
        standardInput.cleanUp();
        if ( standardOutput != null ) {
            standardOutput.cleanUp();
        }
    
        if ( standardError != null ) {
    
    
            InputOutputData errMsgObj = new InputOutputData( "Server Is Having Technical Difficulty. It is not accepting additional commands" );
            standardError.put( errMsgObj );
            standardError.cleanUp();
        }
    }
    
    private boolean processInput( InputOutputData inputOutputData ) {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        String          inputStr        = inputOutputData.getData();
        
        logger.logInfo( "LinuxShell", "run", "Command: " + inputStr );
    
        if ( inputStr.compareTo("KILL THE FUCKING SERVER") == 0 ) {
            return false;
        }
    
        if  ( (inputStr != null) && (inputStr.compareTo("") != 0)  && (inputStr.compareTo("END") != 0) ) {
            Command cmd = parseInputStr( inputStr );
        
            if ( cmd != null ) {
                cmd.execute();
                waitForCmdToFinish(cmd);
            }
        }
    
        currentDirectory.addCmdToHistory( inputStr );
        
        return true;
    }
    

    /*
     *  returns a Command object  if the input string was parsed correctly.  otherwise null
     */
    private Command parseInputStr( String inputString ) {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();

        Command cmd = null;
        try {
            CommandParser cmdParser = new CommandParser(currentDirectory, inputString, standardOutput, standardError );

            cmd = cmdParser.parse();
        }
        catch (CommandParsingException excp) {
            
            String commandStrBeingParse = excp.getCommandStrBeingParsed();
    
            InputOutputData errMsgObj = new InputOutputData( "Invalid Syntax For Command: " + commandStrBeingParse );
    
            standardError.put( errMsgObj  );
            logger.logException( "LinuxShell", "parseInputStr",
                                 "Invalid Syntax For Command: " + commandStrBeingParse , excp);

            String msg = excp.getMessage();
            if (msg != null) {
                msg = msg.trim();
                if ( msg.compareTo("") != 0 ) {
                    String message = excp.getMessage();
                    logger.logError( "LinuxShell", "parseInputStr", message );
                }
            }
            
            return null;
        }
        catch (UnknowCommandException excp) {
            String commandStrBeingParse = excp.getCommandStrBeingParsed();
    
            InputOutputData errMsgObj = new InputOutputData( "Unrecognized Command Name: " + commandStrBeingParse  );
            standardError.put( errMsgObj  );

            logger.logException( "LinuxShell", "parseInputStr",
                    "Unrecognized Command Name: " + commandStrBeingParse , excp);

            String msg = excp.getMessage();
            if (msg != null) {
                msg = msg.trim();
                if ( msg.compareTo("") != 0 ) {
                    String message = excp.getMessage();
                    logger.logError( "LinuxShell", "parseInputStr", message );
                }
            }
    
            String commandName = excp.getCommandName();
            logger.logError( "LinuxShell", "parseInputStr",
                    "Command Name: " + commandName );
    
            String className = excp.getClassName();
            logger.logError( "LinuxShell", "parseInputStr",
                    "Command Name: " + excp.getClassName() );
            
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
