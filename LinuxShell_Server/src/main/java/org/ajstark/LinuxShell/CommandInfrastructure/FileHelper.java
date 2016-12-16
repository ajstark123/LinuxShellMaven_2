package org.ajstark.LinuxShell.CommandInfrastructure;

import java.util.*;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Albert on 11/20/16.
 *
 * @version $Id$
 *
 * This interface contains static methods with method definitions.  The static methods are to help process file
 * names for Commands.
 *
 */
public interface FileHelper {

    /**
     * This function is responsible for removing single and double quotes.
     *
     * @param  str the string that we need to remove the quotes from
     *
     * @return a str with the double and single quotes removed
     *
     */
    public static String removeSingeQuoteDoubleQuote( String str ) {
        char[]       charArr = str.toCharArray();
        StringBuffer buff    = new StringBuffer();

        for ( int i = 0; i < charArr.length ; ++i ) {
            if ( charArr[i] != '\'' &&  charArr[i] != '\"' ) {
                buff.append( charArr[i] );
            }
        }

        return buff.toString();
    }
    
    /**
     * This function is responsible for returning an ArrayList of FileAttributes objects that match the wildcard
     * pattern in the fileName parameters.
     *
     * @param envVar the object that contains the environment variable information.
     * @param fileName contains a filename that has wild cards in it.
     *
     * @return an ArrayList of FileAttributes objects that match the wildcard
     * pattern in the fileName parameters.
     *
     */
    public static ArrayList<FileAttributes> processWildCardsinFileName( EnvironmentVariables envVar, String fileName ) {
        fileName = removeSingeQuoteDoubleQuote(fileName);
        fileName = envVar.subsituteEnvVarValueForEnvValName( fileName );

        char firstChar =  fileName.charAt( 0 );
        if ( firstChar != '/' ) {
            // we need to add the current working directory to  the file name
            fileName = envVar.getCurrentDirectoryName() + "/" + fileName;
        }

        ArrayList<FileAttributes> fileNameList = new ArrayList<FileAttributes>();

        getFilesRecursive( "/", fileName, fileNameList );


        return fileNameList;
    }
    
    
    /**
     * This function is recursive function.  It helps processWildCardsinFileName method find all the files that match
     * the wild carded filename.
     *
     * It breaks the file name into it sub-piecies and compares it to the directory/file hierarchy.  It keep calling
     * it self until it finds all the matches for the right most piece of the file/directory name
     *
     * @param dirName  this is the directory name where we need to match the contents of the directory against the wild carded filename.
     * @param fileName contains a filename that has wild cards in it.
     * @param pieceFileNameList  is an ArrayList of FileAttribute objects.  This is where we store all the files/directories that match the filename
     *
     */
    static void getFilesRecursive( String dirName, String fileName, ArrayList<FileAttributes> pieceFileNameList ) {

        int     indexOf       = fileName.indexOf( '/', 1 );
        String  dirPiece;
        String  fileNamePiece;

        if ( indexOf >= 0 ) {
            dirPiece      = fileName.substring( 0, indexOf );
            fileNamePiece = fileName.substring( indexOf  );
        }
        else {
            dirPiece = fileName;
            fileNamePiece = null;
        }

        File              file              = new File( dirName );
        File[]            fileArr           = file.listFiles();

        PathMatcher matcher;
        if ( dirName.compareTo("/") == 0 ) {
            matcher = FileSystems.getDefault().getPathMatcher("glob:" + "*" + dirPiece);
        }
        else {
            matcher = FileSystems.getDefault().getPathMatcher("glob:" + "*" + dirName + dirPiece);
        }

        for ( int i = 0;  i <  fileArr.length; ++i ) {
            String  pathName = fileArr[i].getPath();

            Path    path     = FileSystems.getDefault().getPath( pathName );

            if ( matcher.matches( path ) ) {
                if ( fileNamePiece != null ) {
                    getFilesRecursive(pathName, fileNamePiece, pieceFileNameList);
                }
                else {
                    FileAttributes fileAttr = new FileAttributes( fileArr[i] );
                    pieceFileNameList.add( fileAttr );
                }
            }
        }
    }
    
    
    /**
     * This function returns true if the fileName has a wildcard character in it
     *
     * @param  fileName the fileName that we need to check for wild cards
     *
     * @return  true if the fileName has a wildcard character in it
     *
     */

    public static boolean hasWildcard( String fileName ) {
        int indexOfAstericks    = fileName.indexOf( '*' );
        int indexofQuestionMark = fileName.indexOf( '?' );
        int indexofRightBrace   = fileName.indexOf( '[' );

        if ( (indexOfAstericks != -1) || (indexofQuestionMark != -1) || (indexofRightBrace != -1) ) {
            return true;
        }

        return false;
    }


}
