package org.ajstark.LinuxShell.Command;


import org.ajstark.LinuxShell.CommandInfrastructure.*;
import org.ajstark.LinuxShell.CommandInfrastructure.EnvironmentVariables;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.ShellInputOutput.*;

import java.util.*;


/**
 * Created by Albert on 11/11/16.
 *
 * @version $Id$
 *
 */
public class SetEnvVarCommand extends BaseCommand {

    public void run() {
        LinuxShellLogger logger         = LinuxShellLogger.getLogger();
        Thread           threadCommand  = Thread.currentThread();
        String           tempThreadName = threadCommand.getName();
        logger.logInfo( "SetEnvVarCommand", "run", "Start of thread: " + tempThreadName );
    
        StandardOut     stdOut = getStandardOutput();
        InputOutputData data    = new InputOutputData(  );
        if ( stdOut != null ) {
            stdOut.put(data);
        }
        
        ShellStandardError stdErr    = super.getShellStandardError();
        InputOutputData    errMsgObj = new InputOutputData(  );
        if ( stdErr != null ) {
            stdErr.put(errMsgObj);
        }
    
        logger.logInfo( "SetEnvVarCommand", "run", "End of thread: " + tempThreadName );
    }

    public void parse( EnvironmentVariables envVar, boolean stdInFromPipe ) throws CommandParsingException {
        String            commandStrBeingParsed  =  super.getCommandStrBeingParsed();
        ArrayList<String> commandParameter       =  super.getCommandParameters();

        int    indexOf = commandStrBeingParsed.indexOf( '=' );
        if ( indexOf == -1 ) {
            CommandParsingException parseException = new CommandParsingException( commandStrBeingParsed, commandParameter );

            throw parseException;
        }

        // strip off equal
        String envVarValue =  commandStrBeingParsed.substring( indexOf + 1 );
        envVarValue = envVarValue.trim();

        indexOf = envVarValue.indexOf( " " );
        if ( indexOf != -1  ) {
            // white space in the middle of the value
            // deformed env var assignment
            CommandParsingException parseException = new CommandParsingException( commandStrBeingParsed, commandParameter );

            throw parseException;
        }


        String envVarName  = "" ;
        envVarValue = "";
        switch ( commandParameter.size() )  {
            case 2: {
                String keyValue = commandParameter.get(1);
                indexOf = keyValue.indexOf('=');

                envVarName = "$" + keyValue.substring(0, indexOf);
                envVarName = envVarName.trim();

                envVarValue = keyValue.substring(indexOf + 1);
                envVarValue = envVarValue.trim();

                break;
            }
            case 3: {
                envVarName = "$" + commandParameter.get(1);
                // the replace method did not work
                //                 envVarName.replace( '=', ' ' );
                // forced to punt
                indexOf = envVarName.indexOf( '=' );
                if (indexOf != -1 ) {
                    envVarName = envVarName.substring(0, indexOf);
                }
                envVarName = envVarName.trim();

                envVarValue = commandParameter.get(2);
                indexOf = envVarValue.indexOf( '=' );
                if (indexOf != -1 ) {
                    envVarValue = envVarValue.substring(1);
                }
                envVarValue = envVarValue.trim();

                break;
            }
            case 4: {
                envVarName  = "$" + commandParameter.get( 1 );
                envVarName = envVarName.trim();

                envVarValue = commandParameter.get( 3 );
                envVarValue = envVarValue.trim() ;

                break;
            }
            default: {
                CommandParsingException parseException = new CommandParsingException( commandStrBeingParsed, commandParameter );

                throw parseException;
            }
        }

        envVarValue =  substitueEnvironmentVariableValueForKey( envVar, envVarValue );

        envVar.setEnvironmentVariableValue( envVarName, envVarValue );
    }


    private String substitueEnvironmentVariableValueForKey( EnvironmentVariables envVar, String envVarValue ) {
        Set<String>       keySet = envVar.getEnvironmentVariableKeys();
        Iterator<String>  iter   = keySet.iterator();

        while ( iter.hasNext() )  {
            // Key is the environment varaibale name that we are looking for in envVarValue
            // value will replace key in envVarValue
            String key   =  iter.next();
            String value =  envVar.getEnvironmentVariableValue( key );

            int indexOf = envVarValue.indexOf( key );
            while ( indexOf >= 0 ) {
                int length = key.length();

                if ( indexOf == 0 ) {
                    String subStr = envVarValue.substring( length );
                    envVarValue = value + subStr;
                }
                else {
                    String  frontSubString = envVarValue.substring( 0, indexOf );
                    String  backSubString  = envVarValue.substring( indexOf + length );

                    envVarValue = frontSubString + value + backSubString;
                }

                indexOf = envVarValue.indexOf( key );
            }
        }

        return envVarValue;
    }


    public void processCommandData( InputOutputData data ) {
        // left empty
    }
}
