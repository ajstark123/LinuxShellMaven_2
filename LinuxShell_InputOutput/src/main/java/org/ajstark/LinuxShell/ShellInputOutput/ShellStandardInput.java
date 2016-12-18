package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.InputOutput.*;

/**
 * Created by Albert on 12/17/16.
 */
public interface ShellStandardInput {
    
    public InputOutputData getInput() throws ShellInputOutputException;
    
    public void cleanUp();
}
