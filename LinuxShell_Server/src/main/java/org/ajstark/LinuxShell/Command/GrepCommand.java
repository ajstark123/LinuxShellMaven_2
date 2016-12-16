package org.ajstark.LinuxShell.Command;

import org.ajstark.LinuxShell.CommandInfrastructure.*;
import org.ajstark.LinuxShell.InputOutput.*;

import java.io.*;
import java.util.*;

/**
 * Created by Albert on 11/5/16.
 *
 * @version $Id$
 *
 */
public class GrepCommand extends ParametersFollowedByFileNameCommand {

    private class LineInfo {
        String line;
        long   lineNumber;

        LineInfo( String line, long lineNumber ) {
            this.line       = line;
            this.lineNumber = lineNumber;
        }

        String getLine() {
            return line;
        }

        long getLineNumber() {
            return lineNumber;
        }
    }

    String pattern;

    boolean stdInFromPipe;

    private long lineNumber;

    boolean printOnlyCountOflines;
    long    countOfLines;

    boolean ignoreCase;
    boolean listFileNamesOnly;
    boolean printLineNumbers;

    boolean printLinesNoMatch;

    boolean printLineButNoFileName;


    ArrayList<String> fileNameList;
    HashSet<String>   fileNameSet;

    HashMap<String,ArrayList<LineInfo> > lineMap;

    public GrepCommand() {
        fileNameList = new ArrayList<String>();

        fileNameSet  = new HashSet<String>();

        lineMap = new HashMap<String, ArrayList<LineInfo> >();

        printOnlyCountOflines = false;
        countOfLines          = 0;

        lineNumber = 0;

        ignoreCase             = false;
        listFileNamesOnly      = false;
        printLineNumbers       = false;
        printLinesNoMatch      = false;
        printLineButNoFileName = false;

        stdInFromPipe          = false;
    }


    public void run() {

        if ( stdInFromPipe ) {
            processDataFromStardInput();
        }
        else {
            // process files
            ArrayList<FileAttributes>  fileAttrList =  super.getFileAttrtList();


            Iterator<FileAttributes>  iter = fileAttrList.iterator();
            while ( iter.hasNext() ) {
                FileAttributes fileAttr =  iter.next();

                grepFile(  fileAttr );
            }
        }

        printOutput();
    }


    public void processCommandData( InputOutputData data ) {
        if ( ! data.isLastDataSent()) {
            String line = data.getData();

            FileAttributes fileAttr = new FileAttributes();

            grepLine( line, fileAttr, lineNumber );
            ++lineNumber;
        }
    }

    public void parse( EnvironmentVariables envVar, boolean stdInFromPipe ) throws CommandParsingException {
        super.parse( envVar, stdInFromPipe );

        this.stdInFromPipe = stdInFromPipe;

        ArrayList<String>         commandParameter =  super.getCommandParameters();

        ArrayList<String>         fileDirNameList  = super.getFileDirNameList();
        ArrayList<FileAttributes> fileAttrList     = super.getFileAttrtList();

        if ( super.isDirectoryFound() ) {
            String commandStrBeingParsed           =  super.getCommandStrBeingParsed();

            CommandParsingException parseException = new CommandParsingException( commandStrBeingParsed, commandParameter );
            parseException.setMessage( "can not run grep on a directory.  grep can only be run on files" );

            throw parseException;
        }


        switch( fileAttrList.size() ) {
            case 0: {
                String commandStrBeingParsed           =  super.getCommandStrBeingParsed();

                CommandParsingException parseException = new CommandParsingException( commandStrBeingParsed, commandParameter );
                parseException.setMessage( "no search pattern" );

                throw parseException;
            }
            case 1: {
                FileAttributes fileAttr = fileAttrList.get( 0 );

                pattern = fileAttr.getNameToUseFromCmd();

                fileDirNameList.remove( 0 );
                fileAttrList.remove( 0 );
                break;
            }
            default: {
                FileAttributes fileAttr = fileAttrList.get( 0 );

                pattern = fileAttr.getNameToUseFromCmd();

                fileDirNameList.remove( 0 );
                fileAttrList.remove( 0 );
                break;
            }
        }

        if ( stdInFromPipe && (fileAttrList.size() > 0) ) {
            String commandStrBeingParsed           =  super.getCommandStrBeingParsed();

            CommandParsingException parseException = new CommandParsingException( commandStrBeingParsed, commandParameter );
            parseException.setMessage( "Grep is part of pipe. Can not parse files when part of a pipe." );

            throw parseException;
        }
    }


    protected void parseCommandLineParameter(char[] charArr, ArrayList<String> commandParameter) throws CommandParsingException {

        if ( charArr.length == 1 ) {
            // oops no command line options after the -
            // degenerate
            String commandStrBeingParsed           =  super.getCommandStrBeingParsed();
            CommandParsingException parseException = new CommandParsingException( commandStrBeingParsed, commandParameter );
            parseException.setMessage( "found a - but had no trailling command line paramaters." );
            throw parseException;
        }

        for ( int i = 1; i < charArr.length; ++i) {
            switch ( charArr[ i ] ) {
                case 'c' : {
                    printOnlyCountOflines = true;
                    break;
                }
                case 'i' : {
                    ignoreCase           = true;
                    break;
                }
                case 'l' : {
                    listFileNamesOnly     = true;
                    break;
                }
                case 'n' : {
                    printLineNumbers = true;
                    break;
                }
                case 'h' : {
                    printLineButNoFileName = true;
                    break;
                }
                case 'v' : {
                    printLinesNoMatch = true;
                    break;
                }
            }
        }
    }

    private void grepFile( FileAttributes fileAttr ) {
        String fileName = fileAttr.getPath();
        File file = new File( fileName );

        if ( file.exists() && file.isFile()) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader buffReader = new BufferedReader(inputStreamReader, 4096);

                long lineNumber = 0;
                String line = buffReader.readLine();
                while (  line != null ) {
                    grepLine( line, fileAttr,lineNumber ) ;

                    line = buffReader.readLine();
                    ++lineNumber;
                }

                fileInputStream.close();
            }
            catch ( Exception excp ) {
                StandardOut      stdErr     = super.getStandardError();
                String           errMsg     = "Error reading or closing file: " + fileName + "\n" + excp.getMessage();
                InputOutputData  errOutData = new InputOutputData( errMsg );
                stdErr.put( errOutData );
            }
            finally {
                try {
                    fileInputStream.close();
                } catch (Exception excp) {
                    StandardOut      stdErr     = super.getStandardError();
                    String           errMsg     = "Error closing file: " + fileName + "\n" + excp.getMessage();
                    InputOutputData  errOutData = new InputOutputData( errMsg );
                    stdErr.put( errOutData );
                }
            }
        }
        else {
            StandardOut     stdOut = getStandardOutput();

            String          outStr  = fileName + " No such file";
            InputOutputData data    = new InputOutputData( outStr );
            stdOut.put( data );
        }
    }


    private void grepLine( String line, FileAttributes fileAttr, long lineNumber ) {
        String origLine = line;

        if ( ignoreCase ) {
            line    = line.toUpperCase();
            pattern = pattern.toUpperCase();
        }

        int foundLoc = line.indexOf( pattern );

        if ( foundLoc >= 0 ) {
            // found a match of pattern in the line
            if ( ! printLinesNoMatch ) {
                // -v option was not specified so we are interested in patches.
                calcGrepStatistics(  line,  fileAttr, lineNumber, origLine );
            }
        }
        else {
            if (printLinesNoMatch) {
                // -v option was  specified so we are interested l;ines that do not match the pattern
                calcGrepStatistics(  line,  fileAttr, lineNumber, origLine );
            }
        }

    }

    private void calcGrepStatistics( String line, FileAttributes fileAttr, long lineNumber, String origLine ) {

        String fileName = fileAttr.getName();

        String fileUsedByCmd = fileAttr.getNameToUseFromCmd();
        if ( (fileUsedByCmd.length() > 0) && (fileUsedByCmd.charAt( 0 ) == '/') ) {
            fileName = fileAttr.getPath();
        }

        fileNameSet.add( fileName );
        ++countOfLines;

        if ( (!printOnlyCountOflines)  &&  (!listFileNamesOnly) ) {
            // we need to print the lines

            // lineMap = new HashMap<String, ArrayList<LineInfo> >();
            LineInfo info = new LineInfo( origLine, lineNumber );

            ArrayList<LineInfo> lineInfoList = lineMap.get( fileName );
            if ( lineInfoList == null ) {
                lineInfoList = new ArrayList<LineInfo>();
                lineMap.put( fileName, lineInfoList );
            }

            lineInfoList.add( info );
        }
    }

    private void printOutput() {
        StandardOut     stdOut = getStandardOutput();
        InputOutputData data;

        if ( printOnlyCountOflines ) {
            String outStr = "" + countOfLines + " match the pattern";
            data          = new InputOutputData( outStr );
            stdOut.put( data );

            data = new InputOutputData();
            stdOut.put( data );

            return;
        }

        if ( listFileNamesOnly ) {
            Iterator<String> iter = fileNameSet.iterator();
            while ( iter.hasNext() ) {
                String fileName = iter.next();
                data            = new InputOutputData( fileName );
                stdOut.put( data );
            }

            data = new InputOutputData();
            stdOut.put( data );

            return;
        }

        Set<String>      keySet  = lineMap.keySet();
        Iterator<String> keyIter = keySet.iterator();
        while ( keyIter.hasNext() ) {
            String               fileName     = keyIter.next();
            ArrayList<LineInfo>  lineInfoList = lineMap.get( fileName ) ;
            Iterator<LineInfo>   lineInfoIter = lineInfoList.iterator();

            while ( lineInfoIter.hasNext() ) {
                LineInfo lineInfo = lineInfoIter.next();

                StringBuffer buffer = new StringBuffer();

                if ( !printLineButNoFileName ) {
                    if (fileName.compareTo("") != 0 ) {

                        buffer.append(fileName);
                        buffer.append(":  ");
                    }
                }

                if ( printLineNumbers ) {
                    buffer.append( lineInfo.getLineNumber() );
                    buffer.append( "  " );
                }

                buffer.append( lineInfo.getLine() );

                String lineData = buffer.toString();
                data            = new InputOutputData( lineData );
                stdOut.put( data );

            }
        }

        data = new InputOutputData();
        stdOut.put( data );
    }
}
