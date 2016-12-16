package org.ajstark.LinuxShell.CommandInfrastructure;

/**
 * Created by Albert on 11/5/16.
 *
 * @version $Id$
 *
 */
public interface Command extends Runnable {


    public void execute();

    public String getCommandStrBeingParsed();

    public Thread getThreadCommand();

}
