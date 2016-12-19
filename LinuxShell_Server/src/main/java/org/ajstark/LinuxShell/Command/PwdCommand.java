package org.ajstark.LinuxShell.Command;

import org.ajstark.LinuxShell.CommandInfrastructure.EnvironmentVariables;
import org.ajstark.LinuxShell.InputOutput.StandardOut;

import java.util.ArrayList;
import org.ajstark.LinuxShell.InputOutput.InputOutputData;
import org.ajstark.LinuxShell.CommandInfrastructure.BaseCommand;
import org.ajstark.LinuxShell.CommandInfrastructure.CommandParsingException;
import org.ajstark.LinuxShell.ShellInputOutput.*;

/**
 * Created by Albert on 11/6/16.
 *
 * @version $Id$
 *
 */
public class PwdCommand extends BaseCommand {
    private EnvironmentVariables envVar;

    public void run() {

        String      currentDirectoryName = envVar.getCurrentDirectoryName();
        StandardOut outPut               = getStandardOutput();

        if ( outPut != null ) {
            InputOutputData data = new InputOutputData( currentDirectoryName );
            outPut.put( data );

            InputOutputData lastDataSent = new InputOutputData( );
            outPut.put( lastDataSent );
        }
    
        ShellStandardError stdErr    = super.getShellStandardError();
        InputOutputData    errMsgObj = new InputOutputData(  );
        if ( stdErr != null ) {
            stdErr.put(errMsgObj);
        }
        
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
