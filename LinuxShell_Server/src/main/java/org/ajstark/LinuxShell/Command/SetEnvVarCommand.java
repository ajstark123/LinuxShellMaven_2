package org.ajstark.LinuxShell.Command;


import org.ajstark.LinuxShell.CommandInfrastructure.*;
import org.ajstark.LinuxShell.CommandInfrastructure.EnvironmentVariables;
import org.ajstark.LinuxShell.InputOutput.*;

import java.util.*;


/**
 * Created by Albert on 11/11/16.
 *
 * @version $Id$
 *
 */
public class SetEnvVarCommand extends BaseCommand {

    public void run() {

        // empty body
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
