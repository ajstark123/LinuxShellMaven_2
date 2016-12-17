package org.ajstark.LinuxShell.ShellInputOutput;

/**
 * Created by Albert on 12/17/16.
 */
public class ShellStandardOutputFactory {
    private static ShellStandardOutputFactory factory;
    
    public static ShellStandardOutputFactory getFactory() {
        factory = new ShellStandardOutputFactory();
        return factory;
    }
    
    public ShellStandardOutput getShellStandardOutput( String uuidStr ) {
        String inputOutputType = System.getProperty( "InputOutputType" );
        
        switch ( inputOutputType ) {
            case "CONSOLE": {
                return new ShellStandardOutputConsole( uuidStr);
            }
            case "MQ": {
                return new ShellStandardOutputConsole( uuidStr );
            }
            default: {
                return new ShellStandardOutputConsole(uuidStr );
            }
        }
    }
    
}
