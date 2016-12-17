package org.ajstark.LinuxShell.ShellInputOutput;

/**
 * Created by Albert on 12/17/16.
 */
public class ShellStandardInputFactory {
    private static ShellStandardInputFactory factory;
    
    public static ShellStandardInputFactory getFactory() {
        factory = new ShellStandardInputFactory();
        return factory;
    }
    
    public ShellStandardInput getShellStandardInput() {
        String inputOutputType = System.getProperty( "InputOutputType" );
        
        if ( inputOutputType == null ) {
            inputOutputType = "null";
        }
        
        switch ( inputOutputType ) {
            case "CONSOLE": {
                return new ShellStandardInputConsole();
            }
            case "MQ": {
                return new ShellStandardInputConsole();
            }
            default: {
                return new ShellStandardInputConsole();
            }
        }
    }
    
}
