

echo " "
echo " "
echo "Starting the Client POJ MQ"
echo " "
echo " "





export LIB_HOME=../lib

#
#  Linux Shell Jars and Client Jars
#
export CLIENT_LIB=$LIB_HOME/LinuxShellClient/LinuxShell_PojClient.jar
export IN_OUT_LIB=$LIB_HOME/LinuxShell/LinuxShell_InputOutput.jar

#
#  Logger jar files
#
export LOGGER_LIB=$LIB_HOME/log4j
export LOG4J_API=$LOGGER_LIB/log4j-api-2.7.jar
export LOG4J_CORE=$LOGGER_LIB/log4j-core-2.7.jar

#
# Logger Configuration
#
export LOGGER_GONFIG_FILE=$LIB_HOME/properties/Logger.properties
export LOGGER_CONFIG="-DLoggerConfigurationFile=$LOGGER_GONFIG_FILE"
export LoggerFileName="../logs/ClientPojMq"

#
#  RABBIT MQ JAR
#
export RABBITMQ_LIB=$LIB_HOME/rabbitmq
export RABBIT_MQ=$RABBITMQ_LIB/amqp-client-4.0.0.jar


#
# slf4j
#
export SLF4_LIB=$LIB_HOME/slf4j
export SLF4=$SLF4_LIB/slf4j-api-1.7.22.jar:$SLF4_LIB/slf4j-simple-1.7.22.jar


#
# Mq Configuration
#
export MQ_GONFIG_FILE=$LIB_HOME/properties/MQ.properties
export MQ_CONFIG="-DMqConfigurationFile=$MQ_GONFIG_FILE"

java -DLoggerFileName=$LoggerFileName $LOGGER_CONFIG -DInputOutputType=MQ $MQ_CONFIG -classpath $CLIENT_LIB:$IN_OUT_LIB:$SLF4:$RABBIT_MQ$SERVER_LIB:$IN_OUT_LIB:$RABBIT_MQ:$LOG4J_API:$LOG4J_CORE   org.ajstark.LinuxShell.ShellClient.PojMqClient
