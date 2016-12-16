package org.ajstark.LinuxShell.Shell;

import org.ajstark.LinuxShell.CommandInfrastructure.*;
import org.ajstark.LinuxShell.CommandInfrastructure.Command;
import org.ajstark.LinuxShell.CommandInfrastructure.EnvironmentVariables;

import java.util.*;


/**
 * Created by Albert on 11/8/16.
 *
 * @version $Id$
 *
 */
public class LinuxShell  implements Runnable {

    private boolean            debug;
    private Thread             threadCommand;

    private CurrentDirectory   currentDirectory;

    private ShellInputConsole  inputConsole;
    
    /**
     * Created by Albert on 11/8/16.
     *
     * Main driver for the LinuxShell
     *
     * @version $Id$
     *
     *
     *
     */
    public LinuxShell() {
        debug = false;

        inputConsole = new ShellInputConsole();

        Properties envVariables     = new Properties();
        currentDirectory = new CurrentDirectory();

        Properties        systemProperties = System.getProperties();

        String value = systemProperties.getProperty( "user.name" );
        if ( value != null ) {
            envVariables.put( "$USER",    value );
            envVariables.put( "$LOGNAME", value );
        }

        value = systemProperties.getProperty( "user.home" );
        if ( value != null ) {
            envVariables.put( "$HOME", value );
            envVariables.put( "$PWD",  value );
        }

        value = systemProperties.getProperty( "os.name" );
        if ( value != null ) {
            envVariables.put( "$OS", value );
        }


        threadCommand = new Thread( this );
        threadCommand.start();
    }


    public void run() {

        Thread shellThread = Thread.currentThread();
        boolean continueLooping = true;

        while (  continueLooping ) {
            String inputStr = inputConsole.getInputFromTerminal();

            if ( inputStr.compareTo("END") == 0 ) {
                continueLooping = false;
            }

            if  ( (inputStr != null) && (inputStr.compareTo("") != 0)  && (inputStr.compareTo("END") != 0) ) {
                Command cmd = parseInputStr( inputStr );

                if ( cmd != null ) {
                    cmd.execute();
                    waitForCmdToFinish(cmd);
                }
            }

            currentDirectory.addCmdToHistory( inputStr );
        }

        System.out.println( "\n\nGOOD BYE!!!\n\n" );
        System.out.flush();
    }


    /*
     *  returns a Command object  if the input string was parsed correctly.  otherwise null
     */
    private Command parseInputStr( String inputString ) {

        Command cmd = null;
        try {
            CommandParser cmdParser = new CommandParser(currentDirectory, inputString);

            cmd = cmdParser.parse();
        } catch (CommandParsingException excp) {
            System.out.println("Invalid Syntax For Command: " + excp.getCommandStrBeingParsed());

            String msg = excp.getMessage();
            if (msg != null) {
                msg = msg.trim();
                if ( msg.compareTo("") != 0 ) {
                    System.out.println( excp.getMessage() );
                }
            }

            if (debug) {
                System.out.println("\n\nParams\n");
                ArrayList<String> cmdList = excp.getCommandStrList();

                Iterator<String> iter = cmdList.iterator();
                while (iter.hasNext()) {
                    String cmdStr = iter.next();
                    System.out.println("    " + cmdStr);
                }

                System.out.println("");
                System.out.println("");
                excp.printStackTrace();
            }

            return null;
        } catch (UnknowCommandException excp) {
            System.out.println("Unrecognized Command Name: " + excp.getCommandStrBeingParsed());

            String msg = excp.getMessage();
            if (msg != null) {
                msg = msg.trim();
                if ( msg.compareTo("") != 0 ) {
                    System.out.println( excp.getMessage());
                }
            }

            if (debug) {
                System.out.println("Command Name:                " + excp.getCommandName());
                System.out.println("Class Name:                  " + excp.getClassName());
                System.out.println("");

                System.out.println("");
                System.out.println("");
                excp.printStackTrace();
            }

            return null;
        }

        return cmd;
    }

    private void waitForCmdToFinish( Command cmd ) {
        try {
            boolean continueProcessing = true;

            while (continueProcessing) {
                synchronized (cmd) {
                    Thread threadCommand = cmd.getThreadCommand();
                    threadCommand.join();


                    if (threadCommand.isAlive()) {
                        threadCommand.join();
                    } else {
                        continueProcessing = false;
                    }
                }
            }
        } catch (Exception excp) {
            System.out.println("Exception:                   " + excp.getClass().getName());
            System.out.println("Get Message:                 " + excp.getMessage());

            System.out.println("");
            System.out.println("");
            excp.printStackTrace();

        }
    }

    public Thread getThreadCommand() {

        return threadCommand;
    }


    public static String getVersion() {
        String version = "$Id$";
        
        return version;
    }
    
    

}
