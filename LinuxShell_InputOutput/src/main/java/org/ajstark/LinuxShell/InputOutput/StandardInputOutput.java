package org.ajstark.LinuxShell.InputOutput;

import java.util.ArrayList;

/**
 * Created by Albert on 11/4/16.
 *
 * @version $Id$
 *
 */
public class StandardInputOutput implements StandardInput, StandardOut {
    private ArrayList<InputOutputData> inOutList;

    public StandardInputOutput() {

        inOutList = new ArrayList<InputOutputData>();
    }


    public synchronized  ArrayList<InputOutputData> get() {
        if ( inOutList.isEmpty() ) {
            return null;
        }

        ArrayList<InputOutputData> returnList = inOutList;
        inOutList = new ArrayList<InputOutputData>();

        return returnList;
    }


    public synchronized  void put( InputOutputData outData ) {

        inOutList.add( outData ) ;
        notifyAll();
    }


}
