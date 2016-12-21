package org.ajstark.LinuxShell.Shell;

import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.ShellInputOutput.*;

/**
 * Created by Albert on 12/21/16.
 */
public class ShellUtil {
    
    static ShellStandardOutput createShellStandardOutput( String uuid ) throws ShellException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
        
        try {
            ShellStandardOutputFactory ouputFactory = ShellStandardOutputFactory.getFactory();
            return ouputFactory.getShellStandardOutput(uuid);
        }
        catch (ShellInputOutputException excp) {
            logger.logException( "CommandParser", "createShellStandardOutput",
                    "Cannot create StandardOut" , excp );
            
            ShellException  newExcp = new ShellException( "Cannot create StandardOut: "  );
            throw newExcp;
        }
    }
    
    
    static  ShellStandardError createShellStandardError( String uuid) throws ShellException {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();

        try {
            ShellStandardErrorFactory errorFactory = ShellStandardErrorFactory.getFactory();
            return errorFactory.getShellStandardError(uuid);
        }
        catch (ShellInputOutputException excp) {
            logger.logException( "CommandParser", "createShellStandardError",
                    "Cannot create StandardErr", excp );
            
            ShellException  newExcp = new ShellException( "Cannot create StandardOut: "  );
            throw newExcp;
        }
    }
}
