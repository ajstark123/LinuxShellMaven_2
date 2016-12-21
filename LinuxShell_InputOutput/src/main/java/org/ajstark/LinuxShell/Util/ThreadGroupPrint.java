package org.ajstark.LinuxShell.Util;

/**
 * Created by Albert on 12/20/16.
 */
public class ThreadGroupPrint {
    
    /**
     * This method is strictly for debuging.  It should be call if the LinuxServShel or clients are not terminateing cleanly
     * at the end of main.
     */
    public static void printThreadgroup(  ThreadGroup group ) {
        Thread[] listThreads = new Thread[1000];
        
        int  count = group.enumerate( listThreads, true );
        System.err.println( "\n\ncount: " + count );
        System.err.println( "\n\nlistThreads.length: " + listThreads.length );
    
        int i = 0;
        while ( i < count ) {
            Thread  thread = listThreads[ i ];
        
            if ( thread != null ) {
                System.err.println( "          i: " + i  );
                System.err.println( "       name: " + thread.getName()  );
                System.err.println( "      Alive: " + thread.isAlive()  );
                System.err.println( "     Daemon: " + thread.isDaemon()  );
                System.err.println( "Interrupted: " + thread.isInterrupted() );
                System.err.println( "      State: " + thread.getState()  + "\n" );
                System.err.flush();
            }
            else {
                System.out.println( "          i: " + i  + " is null");
                System.out.flush();
            }
            
            ++i;
        }
    }
    
    
}
