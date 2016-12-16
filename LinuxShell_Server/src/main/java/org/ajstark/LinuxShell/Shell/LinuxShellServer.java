package org.ajstark.LinuxShell.Shell;


/**
 * Created by Albert on 11/4/16.
 *
 * @version $Id$
 *
 */
public class LinuxShellServer {

    public static void main(String[] args) {


        System.out.println( "LinuxShell Version: " + LinuxShell.getVersion() );
        System.out.println( "TestDrive  Version: " + LinuxShellServer.getVersion() );
        
 
        
        Thread mainThread = Thread.currentThread();


        // CommandParser cmdParser = new CommandParser( "  ls -ld * | grep -i Roscoe  ");
        // CommandParser cmdParser = new CommandParser( "  cd /Users/Albert/Documents/Development/IdeaProjects/LinuxShell/src/org/ajstark/LinuxShell/Command ; pwd; ls -ld * | grep -i Command  ");
        // CommandParser cmdParser = new CommandParser( "  cd /Users/Albert/Documents/Development/IdeaProjects/LinuxShell/src1  ");
        // CommandParser cmdParser = new CommandParser( "  cd /Users/Albert/Documents/Development/IdeaProjects/LinuxShell/LinuxShell.iml  ");

        try {

            LinuxShell shell = new LinuxShell();

            boolean     continueProcessing       = true;

            while ( continueProcessing ) {
                synchronized (shell) {
                    Thread shellThread = shell.getThreadCommand();
                    if ( shellThread.isAlive() ) {
                        shellThread.join();
                    }
                    else {
                        continueProcessing = false;
                    }
                }
            }

        }
        catch( Exception excp ) {
            System.out.println( "Exception:                   " + excp.getClass().getName() );
            System.out.println( "Get Message:                 " + excp. getMessage() );

            System.out.println( "" );
            System.out.println( "" );
            excp.printStackTrace();

        }

    }
    
    public static String getVersion() {
        String version = "$Id$";
        
        return version;
    }
    
}
