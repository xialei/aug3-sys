#!/bin/bash
# ==============================================================================
#  Sourced by other files to setup the environment
# ==============================================================================

# OS specific support (must be 'true' or 'false').
cygwin=false;
linux=fales
aix=false
platform=windows

case "`uname`" in
    CYGWIN*) 
        cygwin=true 
        platform="windows"
        jdk=jdk1.6.0_21
    ;;
    Linux*)
        if [ `uname -i` = "x86_64" ]
        then
             platform="linux_x86_64"
              jdk=jdk1.6.0_21
        else
             platform="linux"
              jdk=jdk1.6.0_21
        fi 
        linux=true
    ;; 
esac

if $cygwin ; then
    SUDO=""
else
    SUDO="sudo "
fi
DIRNAME=`(cd \`dirname $0\`; pwd )`
PROGNAME=`basename $0`

DEV_HOME=`(cd $DIRNAME/../.. ; pwd )`
export DEV_HOME

APP_HOME=`(cd $DIRNAME/.. ; pwd )`
export APP_HOME


#=========================================================
# set up 'THIRDPARTY_LIB_DIR' dynamically
# on developer's build, thirdparty/lib lives under 'DEV_HOME'
# , it's under 'APP_HOME'.
#=========================================================
if [ "x$THIRDPARTY_LIB_DIR" = "x" ]; then
   if [ -d $APP_HOME/thirdparty/lib ]; then
       THIRDPARTY_LIB_DIR=$APP_HOME/thirdparty/lib
   else
     if [ -d $DEV_HOME/thirdparty/lib ]; then
       THIRDPARTY_LIB_DIR=$DEV_HOME/thirdparty/lib
     else 
       echo ERROR: 'THIRDPARTY_LIB_DIR' is not initialized.
       exit 1;
     fi
     
   fi
fi


if [ "x$JAVA_HOME" = "x" ]; then
    JAVA_HOME=@JAVAHOME@

    if ! [ -d $JAVA_HOME ]; then
        JAVA_HOME=$APP_HOME/jdk/$platform/$jdk
    fi
fi

export JAVA_HOME

if ! [ -d $JAVA_HOME ] ; then
    echo "JAVA_HOME is not not valid !! path=$JAVA_HOME";
fi

# set the classpath
DJAVA_CLASSPATH=@DJAVACLASSPATH@

# Check for SUN(tm) JVM w/ HotSpot support
HOTSPOT=`$JAVA_HOME/bin/java -version 2>&1 | grep HotSpot`"x"
if [ "$HOTSPOT" != "x" ]; then
       HOTSPOT="-server"
else
       HOTSPOT=""
fi
export HOTSPOT

# Set the paths for native libraries 
if [ "x$OPTIT_LIB_PATH" = "x" ]; then
   LD_LIBRARY_PATH=$APP_HOME/lib:@RAPIDS_LIBDIR@:$LD_LIBRARY_PATH
else
   LD_LIBRARY_PATH=$APP_HOME/lib:@RAPIDS_LIBDIR@:$OPTIT_LIB_PATH:$LD_LIBRARY_PATH
fi

JNI_LIB=$LD_LIBRARY_PATH
export PATH=$PATH:$APP_HOME/servers/gams:$APP_HOME/servers/openssl

# set the class path.  don't use java command line option because the command
# get too long and is hard for scripting or debugging
export CLASSPATH=$DJAVA_CLASSPATH 

export PATH=$PATH
 
