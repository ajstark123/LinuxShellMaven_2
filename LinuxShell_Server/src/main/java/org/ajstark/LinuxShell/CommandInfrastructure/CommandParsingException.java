package org.ajstark.LinuxShell.CommandInfrastructure;

import java.util.*;

/**
 * Created by Albert on 11/5/16.
 *
 * @version $Id$
 *
 * This exception is thrown if s Command encounters parameters it does not recognize
 */
public class CommandParsingException extends Exception {
    private String            commandUsage;
    private String            commandStrBeingParsed;
    private ArrayList<String> commandStrList;

    private String            message;

    public CommandParsingException( String commandStrBeingParsed, ArrayList<String> commandStrList ) {
        this.commandStrBeingParsed = commandStrBeingParsed;
        this.commandStrList        = commandStrList;
        this.commandUsage          = "";
        this.message               = "";
    }

    public CommandParsingException( String commandUsage, String commandStrBeingParsed, ArrayList<String> commandStrList ) {
        this.commandStrBeingParsed = commandStrBeingParsed;
        this.commandStrList        = commandStrList;
        this.commandUsage          = commandUsage;
        this.message               = "";
    }

    public String getCommandUsage() {

        return commandUsage;
    }

    public String getCommandStrBeingParsed() {

        return commandStrBeingParsed;
    }

    public ArrayList<String> getCommandStrList() {

        return commandStrList;
    }

    public void setMessage( String message ) {

        this.message = message;
    }

    public String getMessage() {
        String returnMessage = ( ( message.compareTo("") == 0 ) ? commandUsage : message );
        String superMessage  = super.getMessage();

        if ( superMessage != null ) {
            returnMessage = returnMessage +  super.getMessage();
        }

        return returnMessage;
    }
}
