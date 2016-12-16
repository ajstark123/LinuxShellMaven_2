package org.ajstark.LinuxShell.Command;

import org.ajstark.LinuxShell.InputOutput.*;
import org.ajstark.LinuxShell.CommandInfrastructure.*;

import java.text.*;
import java.util.*;
import java.io.*;

/**
 * Created by Albert on 11/5/16.
 *
 * @version $Id$
 *
 */
public class LsCommand extends ParametersFollowedByFileNameCommand {
    private boolean longFormat;
    private boolean timeFormat;
    private boolean reverseOrder;
    private boolean showHiddenFiles;


    private String  commandStrBeingParsed;




    private class FileAttributesComarator implements Comparator<FileAttributes> {
        private boolean timeFormat;
        private boolean reverseOrder;

        public FileAttributesComarator(  ) {
            this.timeFormat   = false;
            this.reverseOrder = false;
        }


        public int compare( FileAttributes fileAttr1, FileAttributes fileAttr2 ) {
            if ( timeFormat ) {
                if ( reverseOrder ) {
                    // this command line parameter was set was set -tr i.e. ls -tr or ls -t -r
                    // oldest files should be first
                    return (fileAttr1.getLastModified() < fileAttr2.getLastModified()) ? -1   : 1 ;
                }
                else {
                    // this command line parameter was set was set -t i.e.  ls -t
                    // newest first
                    return (fileAttr1.getLastModified() < fileAttr2.getLastModified()) ? 1 : -1 ;
                }
            }

            // need to compare file sizes
            if ( reverseOrder ) {
                // this command line parameter was set was set -r i.e. ls -r
                // largest files files should be last
                return (fileAttr1.getSize() < fileAttr2.getSize()) ? -1   : 1 ;
            }
            else {
                // list files in alpabetical order
                return fileAttr1.getName().compareTo(fileAttr2.getName())  ;
            }

        }
    }  // end of class FileAttributesComarator


    private FileAttributesComarator    fileAttrComparator;
    private ArrayList<FileAttributes>  fileAttrList;

    public LsCommand()  {
        longFormat            = false;
        timeFormat            = false;
        showHiddenFiles       = false;

        commandStrBeingParsed = "";

        fileAttrComparator = new FileAttributesComarator() ;
        fileAttrList       = new ArrayList<FileAttributes>();
    }





    public void run() {
        StandardOut standardOut              = getStandardOutput();

        processFilesNames( standardOut );

        InputOutputData outputData = new InputOutputData(  );
        standardOut.put( outputData );
    }



    protected void parseCommandLineParameter(char[] charArr, ArrayList<String> commandParameter) throws CommandParsingException {

        if ( charArr.length == 1 ) {
            // oops no command line options after the -
            // degenerate
            CommandParsingException parseException = new CommandParsingException( commandStrBeingParsed, commandParameter );
            parseException.setMessage( "found a - but had no trailling command line paramaters." );
            throw parseException;
        }

        for ( int i = 1; i < charArr.length; ++i) {
            switch ( charArr[ i ] ) {
                case 'l' : {
                    longFormat = true;
                    break;
                }
                case 't' : {
                    longFormat                = true;
                    timeFormat                = true;
                    fileAttrComparator.timeFormat = true;
                    break;
                }
                case 'r' : {
                    longFormat                  = true;
                    reverseOrder                = true;
                    fileAttrComparator.reverseOrder = true;
                    break;
                }
                case 'd' : {
                    // we are not doing a recursive directory listing of files
                    // we will accept a d command line argument because it is used by a lot of old time unix
                    // guys.  But it is no-op
                    break;
                }
                case 'a' : {
                    showHiddenFiles = true;
                    break;
                }
            }
        }
    }


    private void processAllFilesInCurrentWorkingDir( StandardOut standardOut ) {
        EnvironmentVariables envVar = super.getEnvVar();
        String      getCurrentDirectoryName  = envVar.getCurrentDirectoryName();

        File dirFile = new File(getCurrentDirectoryName);


        ArrayList<FileAttributes> fileAttrList = new ArrayList<FileAttributes>();
        File[]                    allFilesArr  = dirFile.listFiles();

        for ( int i = 0;  i < allFilesArr.length ; ++i ) {
            FileAttributes fileAttr = processFileToFileAttribute( allFilesArr[i] );
            fileAttrList.add( fileAttr );
        }

        fileAttrList.sort( fileAttrComparator );

        for ( int i = 0;  i < fileAttrList.size() ; ++i ) {
            processFile(standardOut, fileAttrList.get( i ), false );
        }
    }


    private FileAttributes processFileToFileAttribute( File file ) {
        FileAttributes fileAttr   =  new FileAttributes( file );

        return fileAttr;
    }


    private void processFile( StandardOut standardOut, FileAttributes fileAttr, boolean usePathInsteadOfFileName ) {

        if (!showHiddenFiles && fileAttr.isHidden()  ) {
            // the -a command line parameter is not set and this file is hidden
            // therefore do not dispaly it
            return;
        }

        StringBuffer fileInfoBuff = new StringBuffer();

        if ( longFormat ) {

             if (fileAttr.isDir() ) {
                fileInfoBuff.append('D');
            }
            else {
                fileInfoBuff.append('-');
            }

            if (fileAttr.isReadable() ) {
                fileInfoBuff.append('R');
            }
            else {
                fileInfoBuff.append('-');
            }

            if (fileAttr.isWriteable() ) {
                fileInfoBuff.append('W');
            }
            else {
                fileInfoBuff.append('-');
            }

            if (fileAttr.isExecutable() ) {
                fileInfoBuff.append('X');
            }
            else {
                fileInfoBuff.append('-');
            }

            fileInfoBuff.append(' ');
            String sizeStr = "" + fileAttr.getSize();
            for ( int i = 0; (20 - sizeStr.length() > i) ; ++i ) {
                fileInfoBuff.append(' ');
            }
            fileInfoBuff.append( fileAttr.getSize() );

            fileInfoBuff.append(' ');

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm:ss");
            fileInfoBuff.append(dateFormat.format( fileAttr.getLastModifiedDate() ));

            fileInfoBuff.append(' ');
        }

        if ( usePathInsteadOfFileName ) {
            fileInfoBuff.append( fileAttr.getPath() );
        }
        else {
            fileInfoBuff.append(fileAttr.getName());
        }

        InputOutputData outputData = new InputOutputData( fileInfoBuff.toString() );
        standardOut.put( outputData );
    }


    private void processFilesNames( StandardOut standardOut ) {

        ArrayList<FileAttributes> fileAttrList = super.getFileAttrtList();

        Iterator<FileAttributes> iter = fileAttrList.iterator();
        while ( iter.hasNext() ) {
            FileAttributes   fileAttr = iter.next();

            String nameFromTheCmd = fileAttr.getNameToUseFromCmd();
            if (fileAttr.isDir() || fileAttr.isFile()) {
                if (nameFromTheCmd.charAt(0) == '/') {
                    processFile(standardOut, fileAttr, true);
                } else {
                    processFile(standardOut, fileAttr, false);
                }
            }
            else {
                InputOutputData outputData = new InputOutputData(fileAttr.getPath() + ": No such file or directory");
                standardOut.put(outputData);
            }
        }
    }

    public void processCommandData( InputOutputData data ) {
        // left empty
    }
}
