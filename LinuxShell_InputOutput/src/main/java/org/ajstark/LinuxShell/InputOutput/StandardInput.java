package org.ajstark.LinuxShell.InputOutput;

import java.util.ArrayList;

/**
 * Created by Albert on 11/4/16.
 *
 * @version $Id$
 *
 * Rhis interface defines how commands will get standard input from other users/
 */
public interface StandardInput {
    public ArrayList<InputOutputData> get();
}
