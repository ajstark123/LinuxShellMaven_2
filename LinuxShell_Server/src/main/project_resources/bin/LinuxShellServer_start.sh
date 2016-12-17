

echo " "
echo " "
echo "Starting the Linux Shell Server"
echo " "
echo " "

export LIB_HOME=../lib
export SERVER_LIB=$LIB_HOME/LinuxShell/LinuxShell_Server.jar
export IN_OUT_LIB=$LIB_HOME/LinuxShell/LinuxShell_InputOutput.jar

export LOGGER_LIB=$LIB_HOME/log4j
export LOG4J_API=$LOGGER_LIB/log4j-api-2.7.jar
export LOG4J_CORE=$LOGGER_LIB/log4j-core-2.7.jar

export LOGS_DIR=../logs

export LOGGER_GONFIG_FILE=$LIB_HOME/properties/Logger.properties

# -DLoggerConfigurationFile=/Users/Albert/Documents/Development/build/LinuxShellBuild/LinuxShell_Dist-1.0-SNAPSHOT/lib/properties/Logger.properties
# -Dlog4j.configuration=/Users/Albert/Documents/Development/build/LinuxShellBuild/LinuxShell_Dist-1.0-SNAPSHOT/lib/properties/Logger.properties

# export LOGGER_CONFIG="-DLoggerConfigurationFile=$LOGGER_GONFIG_FILE -Dlog4j.configurationFile=$LOGGER_GONFIG_FILE"
export LOGGER_CONFIG="-DLoggerConfigurationFile=$LOGGER_GONFIG_FILE"


java $LOGGER_CONFIG -DInputOutputType=CONSOLE -classpath $SERVER_LIB:$IN_OUT_LIB:$LOG4J_API:$LOG4J_CORE  org.ajstark.LinuxShell.Shell.LinuxShellServer


