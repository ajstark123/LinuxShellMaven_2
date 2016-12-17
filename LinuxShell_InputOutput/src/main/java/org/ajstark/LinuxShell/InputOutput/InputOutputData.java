package org.ajstark.LinuxShell.InputOutput;

import java.io.*;

/**
 * Created by Albert on 11/4/16.
 *
 * @version $Id$
 *
 */
public class InputOutputData implements Serializable {
    private String  data;
    private boolean lastDataSent;
    private String  uuidStr;

    public InputOutputData( String  data ) {
        this.data            = data;
        this.lastDataSent    = false;
        this.uuidStr         = "CONSOLE";
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
    
    public void setUuidStr( String uuidStr ) {
        this.uuidStr = uuidStr;
    }
    
    public String getUuidStr() {
        return uuidStr;
    }
}
