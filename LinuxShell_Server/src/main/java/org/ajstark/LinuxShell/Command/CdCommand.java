package org.ajstark.LinuxShell.Command;

import org.ajstark.LinuxShell.CommandInfrastructure.*;
import org.ajstark.LinuxShell.CommandInfrastructure.EnvironmentVariables;
import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.ShellInputOutput.*;

import java.util.*;
import java.io.*;



/**
 * Created by Albert on 11/6/16.
 *
 * @version $Id$
 *
 *  Used to change the current directory
 */
public class CdCommand extends ParametersFollowedByFileNameCommand {

    public void run() {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        Thread threadCommand = Thread.currentThread();
        String tempThreadName = threadCommand.getName();
        logger.logInfo( "CdCommand", "run", "Start of thread: " + tempThreadName );
    
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
    
        logger.logInfo( "CdCommand", "run", "End of thread: " + tempThreadName );
    }

    public void parse( EnvironmentVariables envVar, boolean stdInFromPipe ) throws CommandParsingException {
        String            commandStrBeingParsed  =  super.getCommandStrBeingParsed();

        super.parse( envVar, stdInFromPipe );

        String currentDirectoryName;

        ArrayList<String> commandParameter       =  super.getCommandParameters();
        int               size                   = commandParameter.size();


        switch( size ) {
            case 1: {
                // change to the home directory
                currentDirectoryName = envVar.getEnvironmentVariableValue( "$HOME" );
                break;
            }
            case 2: {
                ArrayList<FileAttributes> fileAttrList     = super.getFileAttrtList();

                if ( fileAttrList.size() > 1 ) {
                    CommandParsingException parseException = new CommandParsingException( commandStrBeingParsed, commandParameter );
                    parseException.setMessage( "multiple file/directory names enter.  The cd can have only one directory name" );
                    throw parseException;
                }

                FileAttributes fileAttr = fileAttrList.get( 0 ); // skip over the command name

                boolean exists      = fileAttr.isExist();
                boolean isDirectory = fileAttr.isDir();

                if ( ! exists ) {
                    CommandParsingException parseException = new CommandParsingException( commandStrBeingParsed, commandParameter );
                    parseException.setMessage( fileAttr.getName() + " does not exist" );
                    throw parseException;
                }

                if ( ! isDirectory ) {
                    CommandParsingException parseException = new CommandParsingException( commandStrBeingParsed, commandParameter );
                    parseException.setMessage( fileAttr.getName() + " is not a directory" );
                    throw parseException;
                }

                currentDirectoryName = fileAttr.getPath();

                break;
            }
            default: {
                throw new CommandParsingException( "usage\n    cd\n    cd directory-name",
                                                   commandStrBeingParsed, commandParameter );
            }
        }

        envVar.setCurrentDirectoryName( currentDirectoryName );
    }


    public void processCommandData( InputOutputData data ) {
        // left empty
    }

    protected  void parseCommandLineParameter( char[] charArr, ArrayList<String> commandParameter ) throws CommandParsingException {
        //empty body
        return;
    }
}
