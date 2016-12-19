package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;

import java.util.*;

/**
 * Created by Albert on 12/19/16.
 */
public class ShellStandardErrorList implements ShellStandardError {
    private String                         uuidStr;
    private ArrayList<ShellStandardError> list;
    
    
    ShellStandardErrorList( String uuidStr ) {
        this.list    = new ArrayList<ShellStandardError>();
        this.uuidStr = uuidStr;
    }
    
    public void add( ShellStandardError stdOut ) {
        list.add(  stdOut );
    }
    
    public void put( InputOutputData outData ) {
        
        Iterator<ShellStandardError> iter = list.iterator();
        
        while (iter.hasNext()) {
            ShellStandardError stdOut = iter.next();
            stdOut.put(outData);
        }
    }
    
    
    public String getUuidStr() {
        return uuidStr;
    }
    
    public void cleanUp() {
        //empty body
    }
}
