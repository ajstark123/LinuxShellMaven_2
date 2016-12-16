package org.ajstark.LinuxShell.Command;

import org.ajstark.LinuxShell.CommandInfrastructure.BaseCommand;
import org.ajstark.LinuxShell.CommandInfrastructure.CommandParsingException;
import org.ajstark.LinuxShell.CommandInfrastructure.EnvironmentVariables;
import org.ajstark.LinuxShell.InputOutput.InputOutputData;
import org.ajstark.LinuxShell.InputOutput.StandardOut;

import java.util.*;

/**
 * Created by Albert on 11/14/16.
 *
 * @version $Id$
 *
 */
public class HistoryCommand extends BaseCommand {

    private EnvironmentVariables envVar;

    public void run() {

        StandardOut outPut               = getStandardOutput();

        int i = 1;
        Iterator <String> iter = envVar.getHistoryListIter();
        while ( iter.hasNext() ) {
            String cmdStr = iter.next();


            String countStr = "" + i;

            StringBuffer strBuf = new StringBuffer();
            for ( int j = 0; (5 - countStr.length()) > j ; ++j ) {
                strBuf.append( ' ' );
            }
            strBuf.append( countStr );
            strBuf.append( "  " );
            strBuf.append( cmdStr );

            String historyStr = strBuf.toString();

            if (outPut != null) {
                InputOutputData data = new InputOutputData( historyStr );
                outPut.put(data);
            }

            ++i;
        }
        InputOutputData lastDataSent = new InputOutputData();
        outPut.put(lastDataSent);
    }

    public void parse( EnvironmentVariables envVar, boolean stdInFromPipe ) throws CommandParsingException {

        this.envVar = envVar.clone();

        String            commandStrBeingParsed  =  super.getCommandStrBeingParsed();
        ArrayList<String> commandParameter       =  super.getCommandParameters();

        if ( commandParameter.size() == 1 ) {
            return;
        }

        if ( commandParameter.size() > 1 ) {
            throw new CommandParsingException( "usage\n    history",
                    commandStrBeingParsed, commandParameter );
        }
    }


    public void processCommandData( InputOutputData data ) {
        // left empty
    }

}
