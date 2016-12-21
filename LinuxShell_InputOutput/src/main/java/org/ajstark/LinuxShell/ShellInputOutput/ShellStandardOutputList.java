package org.ajstark.LinuxShell.ShellInputOutput;

import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.Logger.*;
import org.ajstark.LinuxShell.MQ.*;

import java.util.*;

/**
 * Created by Albert on 12/19/16.
 */
public class ShellStandardOutputList implements ShellStandardOutput {
    private String                         uuidStr;
    private ArrayList<ShellStandardOutput> list;
    
    
    ShellStandardOutputList( String uuidStr ) {
        this.list    = new ArrayList<ShellStandardOutput>();
        this.uuidStr = uuidStr;
    }
    
    public void add( ShellStandardOutput stdOut ) {
        list.add(  stdOut );
    }
         
    
    public String getUuidStr() {
        return uuidStr;
    }
    
    public void put( InputOutputData outData ){
        String outStr = outData.getData();
        
        Iterator<ShellStandardOutput> iter = list.iterator();
    
        while (iter.hasNext()) {
            ShellStandardOutput stdOut = iter.next();
            stdOut.put( outData );
        }
    
    }
    
    public void cleanUp() {
        Iterator<ShellStandardOutput> iter = list.iterator();
    
        while (iter.hasNext()) {
            ShellStandardOutput stdOut = iter.next();
            stdOut.cleanUp();
        }
    }
}
