package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;

import java.io.*;

/**
 * Created by Albert on 12/17/16.
 */
public class ShellStandardInputConsole implements ShellStandardInput {
    
    private InputStreamReader inputReader;
    private BufferedReader    bufferedReader;
    
    ShellStandardInputConsole() {
        inputReader    =  new InputStreamReader(System.in);
        bufferedReader =  new BufferedReader(inputReader);
        
    }
    
    public InputOutputData getInput() {
        LinuxShellLogger logger = LinuxShellLogger.getLogger();
    
    
        String inputFromTerminal =  " ";
        
        try {
            System.out.print( ">>  " );
            inputFromTerminal = bufferedReader.readLine();
            inputFromTerminal = inputFromTerminal.trim();
        }
        catch (java.io.IOException excp) {
    
            logger.logException( "ShellStandardInputConsole", "getInput",
                    "IOException reading input from the terminal" , excp);
            
            String message = excp. getMessage();
            logger.logError( "ShellStandardInputConsole", "getInput", message );
                   
            System.out.println( "IOException reading input from the terminal" );
            System.out.println( "Get Message:                 " + message );
            
            System.out.println( "" );
            System.out.println( "" );
            excp.printStackTrace();
            System.out.print( "\n" );
            inputFromTerminal = "";
        }
    
        InputOutputData inOutDate = new InputOutputData( inputFromTerminal );
        
        return inOutDate;
    }
}

