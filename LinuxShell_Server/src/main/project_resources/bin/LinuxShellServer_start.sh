

echo " "
echo " "
echo "Starting the Linux Shell Server"
echo " "
echo " "

export LIB_HOME=../lib
export SERVER_LIB=$LIB_HOME/LinuxShell/LinuxShell_Server.jar
export IN_OUT_LIB=$LIB_HOME/LinuxShell/LinuxShell_InputOutput.jar

export LOGGER_LIB=$LIB_HOME/Logger

export LOGS_DIR=../logs

java -classpath $SERVER_LIB:$IN_OUT_LIB:$LOGGER_LIB  org.ajstark.LinuxShell.Shell.LinuxShellServer

