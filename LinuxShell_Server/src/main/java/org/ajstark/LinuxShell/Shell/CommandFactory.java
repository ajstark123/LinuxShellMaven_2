package org.ajstark.LinuxShell.Shell;

import org.ajstark.LinuxShell.CommandInfrastructure.BaseCommand;
import org.ajstark.LinuxShell.CommandInfrastructure.UnknowCommandException;
import org.ajstark.LinuxShell.ShellInputOutput.*;

import java.util.*;

/**
 * Created by Albert on 11/5/16.
 *
 * @version $Id$
 *
 * This is a singelton Object responcible for creating Commandd Objects
 */
class CommandFactory {
    private static CommandFactory factory;

    private HashMap<String, String> map;

    // prevents other objects from instaintiating  CommandFactory Objects
    private CommandFactory() {

        map = new HashMap<String, String>();

        map.put( "LS",        "org.ajstark.LinuxShell.Command.LsCommand" );
        map.put( "GREP",      "org.ajstark.LinuxShell.Command.GrepCommand" );
        map.put( "PWD",       "org.ajstark.LinuxShell.Command.PwdCommand" );
        map.put( "CD",        "org.ajstark.LinuxShell.Command.CdCommand" );
        map.put( "ENV_VAR",   "org.ajstark.LinuxShell.Command.SetEnvVarCommand" );
        map.put( "ENV",       "org.ajstark.LinuxShell.Command.EnvCommand" );
        map.put( "HISTORY",   "org.ajstark.LinuxShell.Command.HistoryCommand" );
    }


    static CommandFactory getInstance() {
        if (factory == null) {
            factory = new CommandFactory();
        }

        return factory;
    }

    BaseCommand getCommand(String commandStrBeingParsed, ArrayList<String> commandStrList, String uuid )
            throws UnknowCommandException {

        if (commandStrList.isEmpty()) {
            throw new UnknowCommandException( commandStrBeingParsed );
       }

        String commandName = commandStrList.get(0);
        commandName = commandName.trim();
        commandName = commandName.toUpperCase();
        String className = map.get(commandName);

        if (className == null) {
            throw new UnknowCommandException( commandStrBeingParsed, commandName, "Unknown Command"  );
        }

        BaseCommand cmd;
        try {
            Class cmdObj = Class.forName(className);

            cmd = (BaseCommand) cmdObj.newInstance();
    
            ShellStandardError shellStandardError = null;
            try {
                 shellStandardError  = ShellUtil.createShellStandardError( uuid );
            }
            catch (ShellException excp) {
                // do nothing
                shellStandardError = null;
            }
            
            cmd.setShellStandardError(  shellStandardError  );
            
        } catch (Exception excp) {
            UnknowCommandException missingCmdExcp = new UnknowCommandException( commandStrBeingParsed, commandName,
                                                                                className );
            missingCmdExcp.initCause(excp);

            throw missingCmdExcp;
        }

        cmd.setCommandStrBeingParsed(commandStrBeingParsed);
        cmd.setCommandParameters(commandStrList);

        return cmd;
    }
}
