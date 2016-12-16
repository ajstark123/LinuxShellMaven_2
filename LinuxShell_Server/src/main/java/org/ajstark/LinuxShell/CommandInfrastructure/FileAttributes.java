package org.ajstark.LinuxShell.CommandInfrastructure;

import java.util.Date;
import java.io.*;

/**
 * Created by Albert on 11/23/16.
 *
 * @version $Id$
 *
 * This class is responsible for holding information about a file
 *
 */
public class FileAttributes {
    private boolean isDir;
    private boolean isFile;

    private boolean isSymLink;
    private boolean isReadable;
    private boolean isWriteable;
    private boolean isExecutable;
    private boolean isHidden;

    private boolean isExist;

    private long    size;
    private String  name;
    private String  path;
    private String  parentDir;

    private long lastModified;
    private Date lastModifiedDate;

    private String nameToUseFromCmd;



    public FileAttributes( File file ) {
        this.isDir            = file.isDirectory();
        this.isFile           = file.isFile();

        this.isReadable       = file.canRead();
        this.isWriteable      = file.canWrite();
        this.isExecutable     = file.canExecute();
        this.isHidden         = file.isHidden();

        this.isSymLink        = false;  // place holder now

        this.isExist          = file.exists();

        this.lastModified     = file.lastModified();
        this.lastModifiedDate = new Date( this.lastModified );

        this.size             = file.length();
        this.name             = file.getName();
        this.path             = file.getPath();
        this.parentDir        = file.getParent();

        this.nameToUseFromCmd = null;
    }

    public FileAttributes( ) {
        this.isDir            = false;
        this.isFile           = false;

        this.isReadable       = false;
        this.isWriteable      = false;
        this.isExecutable     = false;
        this.isHidden         = false;

        this.isSymLink        = false;  // place holder now

        this.isExist          = false;

        this.lastModified     = new Date().getTime();
        this.lastModifiedDate = new Date( this.lastModified );

        this.size             = 0;
        this.name             = "";
        this.path             = "";
        this.parentDir        = "";

        this.nameToUseFromCmd = "";
    }


    public void setName( String nameToUseFromCmd ) {

        this.nameToUseFromCmd = nameToUseFromCmd;
    }

    public boolean isDir() {
        return isDir;
    }

    public boolean isFile() {
        return isFile;
    }

    public boolean isSymLink() {
        return isSymLink;
    }

    public boolean isReadable() {
        return isReadable;
    }

    public boolean isWriteable() {
        return isWriteable;
    }

    public boolean isExecutable() {
        return isExecutable;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean isExist() {

        return isExist;
    }


    public long getSize() {
        return size;
    }

    public String getName() {

        return name;
    }

    public String getNameToUseFromCmd() {

        return nameToUseFromCmd;
    }



    public String getPath() {
        return path;
    }

    public String getParentDir() {
        return parentDir;
    }

    public long getLastModified() {
        return lastModified;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }
} // end of class FileAttributes
