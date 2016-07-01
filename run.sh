#!/bin/sh
cd `dirname $0`
PWD=`pwd`
PRGNAME=`basename $PWD`

# Setup environment
export JAVA_HOME=$PWD/../jdk1.8
export CATALINA_HOME=$PWD/../apache-tomcat-9

# Compile
export CLASSPATH=$CATALINA_HOME/lib/servlet-api.jar
$JAVA_HOME/bin/javac \
  -d $PWD/WebContent/WEB-INF/classes \
  $PWD/src/example/HelloServlet.java \
  || { echo "Compile failed"; exit 1; }

# Deploy
ln -sf $PWD/WebContent/ $CATALINA_HOME/webapps/$PRGNAME

# Run on background
#$CATALINA_HOME/bin/shutdown.sh
#$CATALINA_HOME/bin/startup.sh

# Run on terminal
$CATALINA_HOME/bin/catalina.sh run
