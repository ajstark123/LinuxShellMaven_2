package org.ajstark.LinuxShell.CommandInfrastructure;

import java.util.*;

/**
 * Created by Albert on 11/11/16.
 *
 * @version $Id$
 *
 */
public interface EnvironmentVariables {

    public String getEnvironmentVariableValue( String key );

    public void setEnvironmentVariableValue( String key, String value );

    public String getCurrentDirectoryName() ;

    public void setCurrentDirectoryName( String currentDirectoryName );

    public EnvironmentVariables  clone();

    public Set<String> getEnvironmentVariableKeys();

    public int getLengthLongestKey();

    public Iterator<String> getHistoryListIter();

    public String subsituteEnvVarValueForEnvValName( String str );

}
