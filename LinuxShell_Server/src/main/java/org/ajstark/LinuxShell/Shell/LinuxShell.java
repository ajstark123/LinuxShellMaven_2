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

    private Thread             threadCommand;

    private CurrentDirectory   currentDirectory;

    
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
     *
     *
     */
    public LinuxShell() throws ShellInputOutputException {
        System.out.println( "\nentering ctor");
        
        ShellStandardInputFactory factory = ShellStandardInputFactory.getFactory();
    
        MqEnvProperties.InputType inputType = MqEnvProperties.getEnvPropertyInputOutputType();
        standardInput = factory.getShellStandardInput( inputType );
    
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


        threadCommand = new Thread( this );
        threadCommand.start();
    }


    public void run() {
        int i = 0;
        System.out.println( "\nentring run method");
        boolean continueLooping = true;
    
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        int retryCount = 0;
        while (  continueLooping ) {
            InputOutputData inputOutputData = null;
    
            try {
                //System.out.println( "" + i + " before standardInput.getInput");
                inputOutputData = standardInput.getInput();
                //System.out.println( "" + i + " after standardInput.getInput" + inputOutputData.getData()  );
                
                //++i;
    
                retryCount = 0;
            }
            catch ( ShellInputOutputException excp ) {
                ++retryCount;
                
                if ( retryCount == 3 ) {
                    standardInput.cleanUp();
    
                    if ( standardOutput == null ) {
                        standardOutput.cleanUp();
                    }
    
                    if ( standardError == null ) {
                        standardError.sendError( "Server Is Having Technical Difficulty. It is not accepting additional commands");
                        standardError.cleanUp();
                    }
                  
                    return;
                }
            }
    
            if ( retryCount == 0 ) {
                continueLooping = processInput( inputOutputData );
            }
        }
    
        standardError.sendError( "\n\nGOOD BYE!!!\n\n"  );
    
        standardInput.cleanUp();
        standardOutput.cleanUp();
        standardError.cleanUp();
        
        logger.logInfo( "LinuxShell", "run", "end of method call" );
    }
    
    private boolean processInput( InputOutputData inputOutputData ) {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
        String          inputStr        = inputOutputData.getData();
        String          uuidStr         = inputOutputData.getUuidStr();
    
        if ( standardOutput == null ) {
            ShellStandardOutputFactory ouputFactory = ShellStandardOutputFactory.getFactory();
            
            standardOutput = ouputFactory.getShellStandardOutput( uuidStr );
        }
    
        if ( standardError == null ) {
            ShellStandardErrorFactory  errorFactory = ShellStandardErrorFactory.getFactory();
            standardError = errorFactory.getShellStandardErrort( uuidStr );
        }
    
        logger.logInfo( "LinuxShell", "run", "Command: " + inputStr );
    
        if ( inputStr.compareTo("END") == 0 ) {
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
        } catch (CommandParsingException excp) {
            
            String commandStrBeingParse = excp.getCommandStrBeingParsed();
            standardError.sendError( "Invalid Syntax For Command: " + commandStrBeingParse  );
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
            standardError.sendError( "Unrecognized Command Name: " + commandStrBeingParse  );
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
