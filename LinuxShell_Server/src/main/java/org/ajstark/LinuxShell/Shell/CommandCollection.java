package org.ajstark.LinuxShell.Shell;

import org.ajstark.LinuxShell.CommandInfrastructure.BaseCommand;
import org.ajstark.LinuxShell.CommandInfrastructure.Command;


import java.util.*;


/**
 * Created by Albert on 11/8/16.
 *
 * @version $Id$
 *
 */
public class CommandCollection implements Command {

    private String commandListString;

    private ArrayList<BaseCommand> commandList;

    private Thread threadCommand;

    CommandCollection( String commandListString, ArrayList<BaseCommand> commandList ) {
        this.commandListString = commandListString;
        this.commandList       = commandList;
    }


    public void execute() {
        threadCommand = new Thread( this );
        threadCommand.start();
    }


    public void run() {
        ListIterator<BaseCommand>  iter = commandList.listIterator( commandList.size() );
        while ( iter.hasPrevious() ) {
            Command cmd =  iter.previous();

            cmd.execute();
        }


        iter = commandList.listIterator(commandList.size());
        while (iter.hasPrevious()) {
            BaseCommand cmd =  iter.previous();

            Thread threadCommand = cmd.getThreadCommand();

            boolean threadIsAlive = threadCommand.isAlive();

            if ( threadIsAlive ) {
                try {
                    threadCommand.join();
                }
                catch ( InterruptedException excp ) {
                    //
                    // exception waiting for therad to end
                    //
                    System.out.println( "CommandCollection::run InterruptedException waiting for thread to end: \n" +
                            cmd.getCommandStrBeingParsed() +  "\nmessage " + excp.getMessage() );
                }
            }
        }

    }


    public String getCommandStrBeingParsed() {

        return commandListString;
    }

    public Thread getThreadCommand() {

        return threadCommand;
    }

}
