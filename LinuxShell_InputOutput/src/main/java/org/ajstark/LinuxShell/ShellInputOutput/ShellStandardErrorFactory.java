package org.ajstark.LinuxShell.ShellInputOutput;

/**
 * Created by Albert on 12/17/16.
 */
public class ShellStandardErrorFactory {
    
    private static ShellStandardErrorFactory factory;
    
    public static ShellStandardErrorFactory getFactory() {
        factory = new ShellStandardErrorFactory();
        return factory;
    }
    
    public ShellStandardError getShellStandardErrort( String uuidStr ) {
        String inputOutputType = System.getProperty( "InputOutputType" );
        
        switch ( inputOutputType ) {
            case "CONSOLE": {
                return new ShellStandardErrorConsole( uuidStr);
            }
            case "MQ": {
                return new ShellStandardErrorConsole( uuidStr );
            }
            default: {
                return new ShellStandardErrorConsole( uuidStr );
            }
        }
    }
    
}
