package org.ajstark.LinuxShell.Shell;

import org.ajstark.LinuxShell.CommandInfrastructure.*;
import org.ajstark.LinuxShell.CommandInfrastructure.Command;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.ShellInputOutput.*;

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
    public LinuxShell() {
        ShellStandardInputFactory factory = ShellStandardInputFactory.getFactory();
        standardInput = factory.getShellStandardInput();
    
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

        boolean continueLooping = true;
    
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        ShellStandardOutputFactory ouputFactory = ShellStandardOutputFactory.getFactory();
        ShellStandardErrorFactory  errorFactory = ShellStandardErrorFactory.getFactory();
        
    
        while (  continueLooping ) {
            InputOutputData inputOutputData = standardInput.getInput();
            String          inputStr        = inputOutputData.getData();
            String          uuidStr         = inputOutputData.getUuidStr();
            
            if ( standardOutput == null ) {
                standardOutput = ouputFactory.getShellStandardOutput( uuidStr );
            }
    
            if ( standardError == null ) {
                standardError = errorFactory.getShellStandardErrort( uuidStr );
            }
            
            logger.logInfo( "LinuxShell", "run", "Command: " + inputStr );

            if ( inputStr.compareTo("END") == 0 ) {
                continueLooping = false;
            }

            if  ( (inputStr != null) && (inputStr.compareTo("") != 0)  && (inputStr.compareTo("END") != 0) ) {
                Command cmd = parseInputStr( inputStr );

                if ( cmd != null ) {
                    cmd.execute();
                    waitForCmdToFinish(cmd);
                }
            }

            currentDirectory.addCmdToHistory( inputStr );
        }
    
        standardError.sendError( "\n\nGOOD BYE!!!\n\n"  );
        
        logger.logInfo( "LinuxShell", "run", "end of method call" );
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
