package org.ajstark.LinuxShell.ShellClient;

/**
 * Created by Albert on 12/19/16.
 */
public abstract class MQClientBase implements Runnable {
    
    public abstract void run();
    
    public abstract Thread getThreadCommand();
    
}
