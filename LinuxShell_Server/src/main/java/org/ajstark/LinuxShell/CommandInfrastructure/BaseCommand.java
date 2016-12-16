package org.ajstark.LinuxShell.CommandInfrastructure;

import org.ajstark.LinuxShell.InputOutput.*;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Albert on 11/5/16.
 *
 * @version $Id$
 *
 * The base command from which all other commands are inherited from
 *
 * The BaseCommand implements the run method (i.e. defined in the Runnable interface).
 * The Run interface will read all the the input from StandardInput interface. The child
 * class is responsible for creating an implementation of the abstract void processCommandData( InputOutputData data )
 * method to process the data - as it sees fit.
 *
 * The child class needs to create an implemation of the abstract void parse() method.  the chile class is responcible
 * for parsing the the synatax for the command it is implementating
 *
 */
public abstract class BaseCommand implements Command {
    private StandardInput input;
    private StandardOut   output;
    private StandardOut   error;


    private String commandStrBeingParsed;

    private ArrayList<String> commandStrList;

    private Thread threadCommand;

    public abstract void parse( EnvironmentVariables envVar, boolean stdInFromPipe ) throws CommandParsingException;

    public abstract void processCommandData( InputOutputData data );


    protected BaseCommand () {
        threadCommand = null;
        error = (StandardOut) StandardOutConsole.getInstance();
    }


    public void execute() {
        threadCommand = new Thread( this );
        threadCommand.start();
    }


    public void run() {
        processDataFromStardInput();
    }

    /*
     *  Deals with the multithreading aspect of reading data from standard input  (i.e. from another command
     *  piping data into this command)
     *
     *  This  is exposed to the child classes. Some times they need to process piped data and other times need
     *  to process data from files (i.e.  non piped data)
     *
     */
    protected final void processDataFromStardInput() {
        StandardInput standardInput          = getStandardInput();
        boolean     continueProcessing       = true;

        while ( continueProcessing ) {
            if (standardInput != null) {
                synchronized (standardInput) {
                    try {
                        standardInput.wait();
                        ArrayList<InputOutputData> dataArr = standardInput.get();
                        Iterator<InputOutputData> iter = dataArr.iterator();

                        while (iter.hasNext()) {
                            InputOutputData data = iter.next();

                            processCommandData(data);

                            if (data.isLastDataSent()) {
                                // this is the last data item to be piped to this command.
                                // we need to stop the looping and let the thread die.
                                continueProcessing = false;
                            }

                        }
                    } catch (InterruptedException excp) {
                        continueProcessing = false;
                    }
                }
            }
        }
    }


    public void setStandardInput( StandardInput input ) {
        //
        this.input = input;
    }

    StandardInput getStandardInput() {
        //
        return input;
    }


    public void setStandardOutput( StandardOut output ) {
        //
        this.output = output;
    }

    public StandardOut getStandardOutput() {
        //
        return output;
    }

    public StandardOut getStandardError() {
        //
        return error;
    }

    public void setCommandStrBeingParsed( String commandStrBeingParsed ) {
        this.commandStrBeingParsed  = commandStrBeingParsed;
    }

    public String getCommandStrBeingParsed() {

        return commandStrBeingParsed;
    }

    public void setCommandParameters( ArrayList<String> commandStrList ) {

        this.commandStrList = commandStrList;
    }

    public ArrayList<String> getCommandParameters() {

        return commandStrList;
    }

    public Thread getThreadCommand() {

        return threadCommand;
    }

}
