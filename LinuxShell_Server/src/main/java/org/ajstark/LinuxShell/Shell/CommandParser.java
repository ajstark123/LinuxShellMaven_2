package org.ajstark.LinuxShell.Shell;

import java.util.*;

import org.ajstark.LinuxShell.CommandInfrastructure.*;
import org.ajstark.LinuxShell.CommandInfrastructure.Command;
import org.ajstark.LinuxShell.CommandInfrastructure.EnvironmentVariables;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.ShellInputOutput.*;

/**
 * Created by Albert on 11/5/16.
 *
 * @version $Id$
 *
 * This class will parse a String that contains mutiple coomands that are piped together.
 *
 */
public class CommandParser {
    // this list has Command objects.  The objects are in order of how they appear in the command line String that is
    // being parsed
    private ArrayList<BaseCommand> commandList;

    private String commandListString;

    private EnvironmentVariables envVar;
    
    private ShellStandardOutput shellStandardOutput;
    private ShellStandardError  shellStandardError;
    private String              uuid;
    
    private LinuxShellLogger    logger;
    
    private ThreadGroup  mainThreadGroup;
    
    
    public CommandParser( EnvironmentVariables envVar,
                          InputOutputData       inputOutputData,
                          ThreadGroup           mainThreadGroup ) throws ShellException {
        
        this.mainThreadGroup = mainThreadGroup;
        
        this.logger = LinuxShellLogger.getLogger();

        this.commandListString = inputOutputData.getData();
        this.commandListString = this.commandListString.trim();

        this.commandList = new ArrayList<BaseCommand>();
        this.envVar      = envVar;
        
        this.uuid = inputOutputData.getUuidStr();
    
        this.shellStandardOutput = null;
        this.shellStandardError  = null;
    
        try {
            shellStandardOutput = ShellUtil.createShellStandardOutput( uuid );
            shellStandardError  = ShellUtil.createShellStandardError( uuid );
        }
        catch (ShellException excp) {
            if ( shellStandardOutput != null ) {
                shellStandardOutput.cleanUp();
            }
    
            if ( shellStandardError != null ) {
                shellStandardError.cleanUp();
            }
            
            throw excp;
        }
     }

    public String getCommandString() {

        return commandListString;
    }

    public Command parse()  {

        try {
            // CommandFactory factory = CommandFactory.getInstance();
    
            // check to see if this command s are seperated by semi colon
            ArrayList<String> arrCommandSemiColon = tokenizeByDeliminator(commandListString, ";");
    
            Iterator<String> iterSemiColon = arrCommandSemiColon.iterator();
    
            while (iterSemiColon.hasNext()) {
        
                String commandStr = iterSemiColon.next();
        
        
                int indexOf = commandStr.indexOf('=');
                if (indexOf >= 0) {
                    parseEnvironmentVariable(commandStr);
                }
                else {
                    parsePipedCommands(commandStr);
                }
            }
    
            CommandCollection cmdCol = new CommandCollection(commandListString, commandList, mainThreadGroup);
    
            return cmdCol;
        }
        catch (CommandParsingException excp) {
    
            String commandStrBeingParse = excp.getCommandStrBeingParsed();
            InputOutputData errMsgObj = new InputOutputData( "Invalid Syntax For Command: " + commandStrBeingParse );
    
            shellStandardError.put( errMsgObj  );
            
            logger.logException( "CommandParser", "parse",
                    "Invalid Syntax For Command: " + commandStrBeingParse , excp);
    
            shellStandardOutput.cleanUp();
            shellStandardError.cleanUp();
            
            return null;
        }
        catch (UnknowCommandException excp) {
            String commandStrBeingParse = excp.getCommandStrBeingParsed();
            String commandName = excp.getCommandName();
    
            InputOutputData errMsgObj = new InputOutputData( "Unrecognized Command Name: " + commandStrBeingParse );
            shellStandardError.put( errMsgObj  );
    
            logger.logException( "CommandParser", "CommandParser",
                    "Unrecognized Command Name: " + commandStrBeingParse +
                            " Command Name: " + commandName + " Class Name: " + excp.getClassName(), excp);
    
            shellStandardOutput.cleanUp();
            shellStandardError.cleanUp();
            
            return null;
        }
    }


    
    
    
    
    
    private void parseEnvironmentVariable( String commandStr )
            throws CommandParsingException,
            UnknowCommandException {

        String commandStrEnvVar = "ENV_VAR " + commandStr;

        ArrayList<String> commandParameter = tokenizeByWhiteSpace( commandStrEnvVar );

        CommandFactory factory = CommandFactory.getInstance( mainThreadGroup );

        BaseCommand cmd = factory.getCommand( commandStrEnvVar, commandParameter, uuid) ;
                                              

        cmd.parse( envVar, true );

        commandList.add(cmd);
    }


    private void parsePipedCommands( String commandStr )
            throws CommandParsingException,
                   UnknowCommandException {
        // check to see if this command has any commands pipped together
    
        CommandFactory factory = CommandFactory.getInstance( mainThreadGroup );
    
        ArrayList<String> arrCommandPipe = tokenizeByDeliminator(commandStr, "|");
    
        int         size            = arrCommandPipe.size();
        BaseCommand previousCommand = null;
        
        Iterator<String> iterPipe = arrCommandPipe.iterator();
        while (iterPipe.hasNext()) {

            String command = iterPipe.next();

            ArrayList<String> commandParameter = tokenizeByWhiteSpace(command);
            commandParameter = parseSubstitueEnvVar( commandParameter );
            

            BaseCommand cmd = factory.getCommand(command, commandParameter, uuid );

            if (size != 1) {
                // there are more them one command.  we need to pipe together the commands commands
                StandardInputOutput inOut = new StandardInputOutput();
                cmd.setStandardInput(inOut);

                if (previousCommand != null) {
                    previousCommand.setStandardOutput(inOut);
                }

                cmd.parse( envVar, true );
            }
            else {
                //  there is only one command.  nothing to pipe together end output to the console
    
                ShellStandardOutput standardOutput = null;
                try {
                    standardOutput = ShellUtil.createShellStandardOutput( uuid );
                }
                catch (ShellException excp) {
                    // do nothing
                    standardOutput = null;
                }
                
                cmd.setStandardOutput( standardOutput );

                cmd.parse( envVar, false );
            }

            previousCommand = cmd;

            commandList.add(cmd);
        }
    
        ShellStandardOutput standardOutput = null;
        try {
            standardOutput = ShellUtil.createShellStandardOutput( uuid );
        }
        catch (ShellException excp) {
            // do nothing
            standardOutput = null;
        }
        previousCommand.setStandardOutput( standardOutput );
    }

    private ArrayList<String> parseSubstitueEnvVar( ArrayList<String> commandParameter ) {
        ArrayList<String> parseCommandParameters = new ArrayList<String>();

        Iterator<String> iter = commandParameter.iterator();
        while ( iter.hasNext() ) {
            String key = iter.next();
            key        = key.trim();

            String value = envVar.getEnvironmentVariableValue( key );
            if ( value != null ) {
                parseCommandParameters.add( value );
            }
            else {
                parseCommandParameters.add( key );
            }
        }

        return parseCommandParameters;
    }

    /*
         This method breaks a command list into its individual command for example the following
         command string is split by the | character

         ls -ld * | grep -i CatDog

         woutld be seperated into two strings
         1) ls -ld *
         2) grep -i CatDog
     */
    private ArrayList<String> tokenizeByDeliminator( String commandString, String deliminatorStr ) {

        deliminatorStr = deliminatorStr.trim();

        StringTokenizer strTkn = new StringTokenizer( commandString, deliminatorStr );

        ArrayList<String> arrList = new ArrayList<String>( commandString.length() );

        while(strTkn.hasMoreTokens()) {
            arrList.add( strTkn.nextToken() );
        }

        return arrList;
    }

    /*
         This method breaks a command  into its individual pieces example

         ls -ld *

         woutld be seperated into two strings
         1) ls
         2) -ld
         3) *
     */
    private ArrayList<String> tokenizeByWhiteSpace( String commandString ) {
        StringTokenizer strTkn = new StringTokenizer( commandString  );

        ArrayList<String> arrList = new ArrayList<String>( commandString.length() );

        while(strTkn.hasMoreTokens()) {
            arrList.add( strTkn.nextToken() );
        }

        return arrList;
    }

}
