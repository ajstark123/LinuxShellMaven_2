package org.ajstark.LinuxShell.CommandInfrastructure;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Albert on 11/25/16.
 *
 * @version $Id$
 *
 * This abstrace class encampusulates the knowledge to parse commands that have
 * a list of commande line parameters followed by a list of files
 *
 *  While parsing the command line this class
 *      1) will substitute an Environment Variable Keys for their values
 *      2) will remove single and double quotes
 *      3) will do wildcard expansion
 *
 * for example
 * ls -l  /?sers/Albert/Documents/Development/IdeaProjects/LinuxShell/src/org/ajstark/*
 * ls -rl "$IN_OUT_DIR/StandardInput.java" G*
 *
 * COMMAND_DIR=/Users/Albert/Documents/Development/IdeaProjects/LinuxShell/src/org/ajstark/LinuxShell/Command/Grep*  ; cd $COMMAND_DIR ; pwd
 *
 */
public abstract class ParametersFollowedByFileNameCommand extends BaseCommand {
    private EnvironmentVariables envVar;

    boolean directoryFound;

    private ArrayList<String>  fileDirName;

    private ArrayList<FileAttributes>  fileAttrList;


    protected abstract void parseCommandLineParameter( char[] charArr, ArrayList<String> commandParameter ) throws CommandParsingException;


    public ParametersFollowedByFileNameCommand()  {
        envVar                = null;
        directoryFound        = false;

        fileDirName           = new ArrayList<String>();
        fileAttrList          = new ArrayList<FileAttributes>();
    }


    public void parse( EnvironmentVariables envVar, boolean stdInFromPipe ) throws CommandParsingException {
        this.envVar = envVar.clone();

        ArrayList<String> commandParameter       =  super.getCommandParameters();

        boolean firstTime = true;
        Iterator<String> iter = commandParameter.iterator();
        while ( iter.hasNext() ) {
            String param = iter.next();
            param = param.trim();

            if ( ! firstTime ) {
                // skip over the command namei.e. ls
                if (!param.isEmpty()) {
                    char[] charArr = param.toCharArray();

                    if (charArr[0] == '-') {
                        parseCommandLineParameter(charArr, commandParameter);
                    } else {
                        parseFileNames(param);
                    }
                }
            }

            firstTime = false;
        }
    }


    private void parseFileNames( String param ) {
        param = envVar.subsituteEnvVarValueForEnvValName( param );

        // removes any single and double quotes from the string
        param = FileHelper.removeSingeQuoteDoubleQuote( param );

        parseExpandWildCard( param );
    }


    private void parseExpandWildCard( String fileName ) {

        boolean hasWildCards = FileHelper.hasWildcard( fileName );

        if ( !hasWildCards ) {
            // has no wild cards just process the file
            String fileNameToEvaluate = fileName;

            if ( fileName.charAt(0) != '/' ) {
                // we need to append the current working directory to the file name
                fileNameToEvaluate = envVar.getCurrentDirectoryName() + "/" + fileName;
            }

            File file     = new File( fileNameToEvaluate );
            FileAttributes fileAttr = new FileAttributes( file );
            fileAttr.setName( fileName );

            if ( fileAttr.isDir() ) {
                directoryFound = true;
            }

            boolean inList = fileDirName.contains( fileAttr.getPath() );
            if (  !inList ) {
                fileAttrList.add(fileAttr);
                fileDirName.add(fileAttr.getPath());
            }
        }
        else {
            // process any wild cards in the file names
            ArrayList<FileAttributes> expandedFileNameList = FileHelper.processWildCardsinFileName(envVar, fileName);

            Iterator<FileAttributes> expandedIter = expandedFileNameList.iterator();
            while (expandedIter.hasNext()) {
                FileAttributes fileAttr = expandedIter.next();
                fileAttr.setName( fileName );

                if (fileAttr.isDir() ) {
                    directoryFound = true;
                }

                boolean inList = fileDirName.contains( fileAttr.getPath() );
                if (  !inList ) {
                    fileAttrList.add(fileAttr);
                    fileDirName.add(fileAttr.getPath());
                }
            }
        }
    }


    protected ArrayList<String> getFileDirNameList() {

        return fileDirName;
    }

    protected ArrayList<FileAttributes> getFileAttrtList() {

        return fileAttrList;
    }


    protected EnvironmentVariables getEnvVar() {

        return envVar;
    }

    protected boolean isDirectoryFound() {

        return directoryFound;
    }

}
