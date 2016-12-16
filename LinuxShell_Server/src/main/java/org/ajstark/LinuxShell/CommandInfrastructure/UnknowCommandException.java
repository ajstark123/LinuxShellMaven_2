package org.ajstark.LinuxShell.CommandInfrastructure;

/**
 * Created by Albert on 11/5/16.
 *
 * This Exception  is thrown when the an unknown command name is encounter by CommandParser
 *
 * @version $Id$
 *
 */
public class UnknowCommandException extends Exception {
    private String commandStrBeingParsed;
    private String commandName;
    private String className;

    public UnknowCommandException( String commandStrBeingParsed ) {
        this.commandStrBeingParsed = commandStrBeingParsed;
    }

    public UnknowCommandException( String commandStrBeingParsed, String commandName, String className ) {
        this.commandStrBeingParsed = commandStrBeingParsed;
        this.commandName           = commandName;
        this.className             = className;
    }

    public String getCommandStrBeingParsed() {
        return commandStrBeingParsed;
    }


    public String getCommandName() {
        return commandName;
    }

    public String getClassName() {
        return className;
    }
}
