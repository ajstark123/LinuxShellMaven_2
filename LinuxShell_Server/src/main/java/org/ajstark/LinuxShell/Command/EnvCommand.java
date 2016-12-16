package org.ajstark.LinuxShell.Command;

import org.ajstark.LinuxShell.CommandInfrastructure.EnvironmentVariables;
import org.ajstark.LinuxShell.InputOutput.InputOutputData;
import org.ajstark.LinuxShell.InputOutput.StandardOut;

import org.ajstark.LinuxShell.CommandInfrastructure.BaseCommand;
import org.ajstark.LinuxShell.CommandInfrastructure.CommandParsingException;

import java.util.*;


/**
 * Created by Albert on 11/13/16.
 *
 * @version $Id$
 *
 */
public class EnvCommand extends BaseCommand {

    private EnvironmentVariables envVar;

    public void run() {
        StandardOut standardOut              = getStandardOutput();

        int               lengthOfLongestKey =  envVar.getLengthLongestKey() + 2;
        Set<String>       keySet             = envVar.getEnvironmentVariableKeys();
        Iterator<String>  iter               = keySet.iterator();

        while ( iter.hasNext() ) {
            String key   =  iter.next();
            String value =  envVar.getEnvironmentVariableValue( key );

            StringBuffer strBuf = new StringBuffer( key );
            for ( int i = 0 ;  i < (lengthOfLongestKey - key.length()) ;  ++ i ) {
                strBuf.append( ' ' );
            }

            strBuf.append( " " + value );
            String keyValue = strBuf.toString();

            InputOutputData outputData = new InputOutputData( keyValue );
            standardOut.put( outputData );
        }

        InputOutputData outputData = new InputOutputData(  );
        standardOut.put( outputData );
    }

    public void parse( EnvironmentVariables envVar, boolean stdInFromPipe ) throws CommandParsingException {
        this.envVar = envVar.clone();

        String            commandStrBeingParsed  =  super.getCommandStrBeingParsed();
        ArrayList<String> commandParameter       =  super.getCommandParameters();
    }

    public void processCommandData( InputOutputData data ) {
        // left empty
    }
}
