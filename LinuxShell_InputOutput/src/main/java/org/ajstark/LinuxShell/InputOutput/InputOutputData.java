package org.ajstark.LinuxShell.InputOutput;

/**
 * Created by Albert on 11/4/16.
 *
 * @version $Id$
 *
 */
public class InputOutputData {
    private String  data;
    private boolean lastDataSent;

    public InputOutputData( String  data ) {
        this.data            = data;
        this.lastDataSent    = false;
    }

    public InputOutputData( ) {
        this.data            = "";
        this.lastDataSent    = true;
    }

    public boolean isLastDataSent( ) {
        return lastDataSent;
    }

    public String getData() {
        return data;
    }
}
