package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.InputOutput.*;

/**
 * Created by Albert on 12/17/16.
 */
public interface ShellStandardError extends StandardOut {
    public String getUuidStr();
    
    public void cleanUp();
}
