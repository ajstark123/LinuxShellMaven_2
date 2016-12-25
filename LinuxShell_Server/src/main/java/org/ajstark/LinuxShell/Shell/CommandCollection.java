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
    private static long                 count = 0;
    
    private String commandListString;

    private ArrayList<BaseCommand> commandList;

    private Thread      threadCommand;
    private ThreadGroup mainThreadGroup;
    private ThreadGroup collectionThreadGroup;
    private String      threadName;

    CommandCollection( String commandListString, ArrayList<BaseCommand> commandList, ThreadGroup mainThreadGroup  ) {
        this.commandListString = commandListString;
        this.commandList       = commandList;
        this.mainThreadGroup   = mainThreadGroup;
    }


    public void execute() {
        
        long tempCount = getCount();
        
        String tempThreadName = "CommandCollection: " + tempCount;
        setThreadName( tempThreadName );
    
        collectionThreadGroup = new ThreadGroup( mainThreadGroup,"Group " + tempThreadName );
        
        threadCommand = new Thread( collectionThreadGroup, this, threadName + " " + commandListString);
        threadCommand.start();
    }


    public void run() {
        int i = 1;
        ListIterator<BaseCommand>  iter = commandList.listIterator( commandList.size() );
        while ( iter.hasPrevious() ) {
            Command cmd =  iter.previous();

            Class  classObj  = cmd.getClass();
            String className = classObj.getName();
            
            int index = className.lastIndexOf( '.' );
            if ( index != -1 ) {
                className = className.substring( index + 1);
            }
            
            
            String childThreadName = threadName + " child thread: " + i + " class name: " + className;
            
            cmd.setThreadName( childThreadName );
            cmd.setParentThreadGroup( collectionThreadGroup );
            
            cmd.execute( );
            ++i;
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
    
    
    
    private synchronized long getCount() {
        if ( Long.MAX_VALUE == count ) {
            count = 0;
        }
        
        long tempCount = count;
        ++count;
        
        return tempCount;
    }
    
    public void setThreadName( String threadName ) {
        this.threadName = threadName;
    }
    
    public void setParentThreadGroup( ThreadGroup group ) {
        // do nothing
    }

}
