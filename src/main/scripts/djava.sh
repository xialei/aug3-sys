#!/bin/sh
# ==============================================================================
#  Used to launch Java classes with the APP Environment Setup
# ==============================================================================

. `dirname $0`/setenv.sh

# check for colorer
COLORER=$DIRNAME/colorer.pl
if [ "x$1" != "x--nocolor" -a -x $COLORER ] ; then
    echo Running with colorized output. Give --nocolor as first argument to disable.
    exec $COLORER --jboss $0 --nocolor $*
else
    shift;
fi

# for jgroups, we need to make sure we are running in
# IPv4 compatibility mode, otherwise we have problems.
export JAVAOPTS="$JAVAOPTS -Djava.net.preferIPv4Stack=true"

# add application specific jar files in classpath
if [ "x$APP_CLASSPATH" != "x" ] ; then
   DJAVA_CLASSPATH=$APP_CLASSPATH:$DJAVA_CLASSPATH
fi

# adjust paths for cygwin
if $cygwin ; then
    DJAVA_CLASSPATH=`cygpath --path --windows "$DJAVA_CLASSPATH"`
    APP_HOME=`cygpath --path --windows "$APP_HOME"`
    DEV_HOME=`cygpath --path --windows "$DEV_HOME"`
    JNI_LIB=`cygpath --path --windows "$LD_LIBRARY_PATH"`
fi

# set the path so javac is available at runtime
# Add native libraries in lib to Win PATH
export PATH="$LD_LIBRARY_PATH:$JAVA_HOME/bin:$PATH"

# set the class path.  don't use java command line option because the command
# get too long and is hard for scripting or debugging
export CLASSPATH=$DJAVA_CLASSPATH 

#
# check if the process uses local config server or remote
#
if [ "x$APPLOCALCFG" = "x" ] ; then
   APPLOCALCFG=true 
fi

exec $JAVA_HOME/jre/bin/java $JAVAOPTS -Dfile.encoding=UTF8 -Dapp.localcfg=$APPLOCALCFG -Djava.library.path="$JNI_LIB" $@
