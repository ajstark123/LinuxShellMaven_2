package org.ajstark.LinuxShell.CommandInfrastructure;

// import java.nio.file.Path;
// import java.nio.file.Paths;

import java.util.*;


/**
 * Created by Albert on 11/6/16.
 *
 * @version $Id$
 *
 */
public class CurrentDirectory implements EnvironmentVariables {

    private String currentDirectoryName;
    private Properties envVariables;
    private int lengthLongestKey;

    private ArrayList<String> historyList;

    public CurrentDirectory() {
        //
        //  This code get the current directory from thee OS
        //
        // Path currentRelativePath = Paths.get("");
        // currentDirectoryName = currentRelativePath.toAbsolutePath().toString();
        // envVariables         = new Properties();
        //

        historyList = new ArrayList<String>();

        envVariables = new Properties();
        Properties systemProperties = System.getProperties();

        String value = systemProperties.getProperty("os.name");
        if (value != null) {
            envVariables.put("$OS", value);
            lengthLongestKey = 3;
        }


        Map<String, String> env = System.getenv();
        value = env.get("PATH");
        if (value != null) {
            envVariables.put("$PATH", value);
            lengthLongestKey = 5;
        }

        value = systemProperties.getProperty("user.home");
        if (value != null) {
            envVariables.put("$HOME", value);
            envVariables.put("$PWD", value);
            currentDirectoryName = value;
            lengthLongestKey = 5;
        }

        value = systemProperties.getProperty("user.name");
        if (value != null) {
            envVariables.put("$USER", value);
            envVariables.put("$LOGNAME", value);
            lengthLongestKey = 8;
        }


    }

    private CurrentDirectory(String currentDirectoryName, Properties envVariables, ArrayList<String> historyList) {
        this.lengthLongestKey = 0;
        this.currentDirectoryName = currentDirectoryName;
        this.envVariables = new Properties();
        this.historyList = (ArrayList<String>) historyList.clone();

        cloneProperties(envVariables);
    }


    public synchronized String getCurrentDirectoryName() {

        return currentDirectoryName;
    }


    public synchronized void setCurrentDirectoryName(String currentDirectoryName) {

        this.currentDirectoryName = currentDirectoryName;

        if (lengthLongestKey < 4) {
            lengthLongestKey = 4;
        }

        envVariables.setProperty("$PWD", currentDirectoryName);
    }

    public synchronized String getEnvironmentVariableValue(String key) {

        return envVariables.getProperty(key);
    }

    public synchronized void setEnvironmentVariableValue(String key, String value) {
        if (key != null) {
            key = key.trim();

            if ("".compareTo(key) != 0) {
                if (lengthLongestKey < key.length()) {
                    lengthLongestKey = key.length();
                }

                envVariables.setProperty(key, value);
            }
        }
    }

    public EnvironmentVariables clone() {
        CurrentDirectory cloneObj = new CurrentDirectory(this.currentDirectoryName, this.envVariables, this.historyList);

        return cloneObj;
    }

    public Set<String> getEnvironmentVariableKeys() {

        return envVariables.stringPropertyNames();
    }

    private void cloneProperties(Properties systemProperties) {
        Set<String> keySet = systemProperties.stringPropertyNames();
        Iterator<String> iter = keySet.iterator();

        while (iter.hasNext()) {
            String key = iter.next();
            String value = systemProperties.getProperty(key);

            if (lengthLongestKey < key.length()) {
                lengthLongestKey = key.length();
            }

            envVariables.put(key, value);
        }
    }

    public int getLengthLongestKey() {
        return lengthLongestKey;
    }

    public void addCmdToHistory(String cmdStr) {

        historyList.add(cmdStr);
    }

    public Iterator<String> getHistoryListIter() {

        return historyList.iterator();
    }


    public String subsituteEnvVarValueForEnvValName(String str) {
        // remove any leading and trailing spaces
        str = str.trim();

        // strings quoted with a single quote ie 'how now brown $ANIMINAL'
        // we can not do a sustition of env var
        int previousStartLocation = 0;
        int indexOf               = str.indexOf( '\'' );
        if ( indexOf >= 0 ) {
            StringBuffer buf = new StringBuffer();

            // need to process around the single quotes
            while (indexOf >= 0) {

                if ( indexOf > 0) {
                    String subStr = str.substring(previousStartLocation, indexOf );
                    subStr = substituteEnvVar(subStr);
                    buf.append(subStr);
                }

                // find the next single quote
                int    firstQuote = indexOf;
                indexOf           = str.indexOf( '\'', firstQuote + 1 );
                String subStr     = str.substring( firstQuote, indexOf +1 );
                buf.append( subStr );

                str     = str.substring( indexOf + 1 );
                indexOf = str.indexOf( '\'' );
            }

            str = buf.toString();
        }
        else {
            // no single quotes do the substition - no worries
            str = substituteEnvVar( str );
        }

        return str;
    }


    private String substituteEnvVar( String str ) {
        Set<Map.Entry<Object,Object>> entrySet = envVariables.entrySet();

        Iterator<Map.Entry<Object,Object>> iter = entrySet.iterator();
        while ( iter.hasNext() )  {
            Map.Entry<Object,Object> mapEntry = iter.next();

            String key   = "\\" + (String) mapEntry.getKey();
            String value = (String) mapEntry.getValue();

            str = str.replaceAll( key, value );
        }

        return str;
    }


}
