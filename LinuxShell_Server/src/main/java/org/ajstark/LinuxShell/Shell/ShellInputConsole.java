package org.ajstark.LinuxShell.Shell;

import org.ajstark.LinuxShell.InputOutput.InputOutputData;
import org.ajstark.LinuxShell.InputOutput.StandardInput;

import java.util.*;
import java.io.*;

/**
 * Created by Albert on 11/13/16.
 *
 * @version $Id$
 *
 */
public class ShellInputConsole {
    private InputStreamReader  inputReader;
    private BufferedReader     bufferedReader;

    public ShellInputConsole() {
        inputReader    =  new InputStreamReader(System.in);
        bufferedReader =  new BufferedReader(inputReader);

    }


    public String getInputFromTerminal() {

        String inputFromTerminal =  " ";

        try {
            System.out.print( ">>  " );
            inputFromTerminal = bufferedReader.readLine();
            inputFromTerminal = inputFromTerminal.trim();
        }
        catch (java.io.IOException excp) {
            System.out.println( "IOException reading input from the terminal" );
            System.out.println( "Get Message:                 " + excp. getMessage() );

            System.out.println( "" );
            System.out.println( "" );
            excp.printStackTrace();
            System.out.print( "\n" );
            return "";
        }

        return inputFromTerminal;
    }
}
