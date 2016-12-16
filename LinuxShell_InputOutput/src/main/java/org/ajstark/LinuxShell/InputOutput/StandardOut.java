package org.ajstark.LinuxShell.InputOutput;

/**
 * Created by Albert on 11/4/16.
 *
 * @version $Id$
 *
 * This interface defines the mechanish that commands send standard output date to user and other commands
 */
public interface StandardOut {
    public void put( InputOutputData outData );
}
