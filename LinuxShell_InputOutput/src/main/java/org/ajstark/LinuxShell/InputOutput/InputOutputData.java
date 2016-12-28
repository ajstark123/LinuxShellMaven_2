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
    private boolean prompt;

    public InputOutputData( String  data ) {
        this.data            = data;
        this.lastDataSent    = false;
        this.uuidStr         = "";
        this.prompt          = false;
    }
    
    public InputOutputData( String  data, boolean prompt ) {
        this.data            = data;
        this.lastDataSent    = false;
        this.uuidStr         = "";
        this.prompt          = prompt;
    }

    public InputOutputData( ) {
        this.data            = "";
        this.lastDataSent    = true;
        this.uuidStr         = "";
        this.prompt          = false;
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
    
    public boolean isPrompt() {
        return prompt;
    }
}
