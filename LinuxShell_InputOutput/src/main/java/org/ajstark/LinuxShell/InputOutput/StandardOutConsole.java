package org.ajstark.LinuxShell.InputOutput;

import java.io.*;

/**
 * Created by Albert on 11/6/16.
 *
 * @version $Id$
 *
 */
public class StandardOutConsole implements StandardOut {

    private static StandardOutConsole standardOutConsoleObj = null;

    private StandardOutConsole() {

       // empty
    }

    public static StandardOutConsole getInstance() {
        if ( standardOutConsoleObj == null ) {
            standardOutConsoleObj = new StandardOutConsole();
        }

        return standardOutConsoleObj;
    }

    public void put( InputOutputData outData ) {

        if ( ! outData.isLastDataSent() ) {
            String data = outData.getData();

            System.out.println( data );
            System.out.flush();
        }
    }

}
