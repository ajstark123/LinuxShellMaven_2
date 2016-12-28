package org.ajstark.LinuxShell.Shell;

import org.ajstark.LinuxShell.CommandInfrastructure.BaseCommand;
import org.ajstark.LinuxShell.CommandInfrastructure.Command;
import org.ajstark.LinuxShell.Logger.*;


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

    private ArrayList<Command> commandList;

    private Thread      threadCommand;
    private ThreadGroup mainThreadGroup;
    private ThreadGroup collectionThreadGroup;
    private String      threadName;
    private boolean     calledFromMainLinuxShell;
    
    CommandCollection( String commandListString, ArrayList<Command> commandList, ThreadGroup mainThreadGroup  ) {
        this.commandListString = commandListString;
        this.commandList       = commandList;
        this.mainThreadGroup   = mainThreadGroup;
        this.calledFromMainLinuxShell = false;
    }
    
    CommandCollection( String commandListString, ArrayList<Command> commandList, ThreadGroup mainThreadGroup, boolean     calledFromMainLinuxShell  ) {
        this.commandListString = commandListString;
        this.commandList       = commandList;
        this.mainThreadGroup   = mainThreadGroup;
        this.calledFromMainLinuxShell = calledFromMainLinuxShell;
    }

    public void execute() {
        
        long tempCount = getCount();
        
        String tempThreadName = "CommandCollection: " + tempCount;
        setThreadName( tempThreadName );
    
        collectionThreadGroup = new ThreadGroup( mainThreadGroup,"Group " + tempThreadName );
        
        threadCommand = new Thread( collectionThreadGroup, this, threadName + " " + commandListString);
        threadCommand.start();
        
        if ( ! calledFromMainLinuxShell ) {
            try {
                threadCommand.join();
            }
            catch ( InterruptedException excp ) {
                // do nothing
            }
        }
        
    }


    public void run() {
    
        Command previousCommand = null;
        int i = 1;
        Iterator<Command>  iter = commandList.iterator();
        while ( iter.hasNext() ) {
            Command cmd =  iter.next();

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
            
            if (className.compareTo( "OutputCommand") == 0) {
                // need to force the starting of OutputCommand first
                threadCommand.yield();
            }
            
            previousCommand = cmd;
            ++i;
        }
    
        Thread childThread = previousCommand.getThreadCommand();
        try {
            childThread.join();
        }
        catch ( InterruptedException excp ) {
            // do nothing
        }

        
        /*
        try {
            threadCommand.yield();
            threadCommand.sleep(3000);
        }
        catch ( InterruptedException excp ) {
            // do nothing
        }
        */
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
