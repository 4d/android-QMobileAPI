#!/bin/bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cd .

SOURCE_DIR=$(find . -maxdepth 1 -type d -name 'qmobile*' -print -quit)

clean=1
if [ "$#" -gt "0" ]; then
    clean=$1
fi

echo "☕️ Check Java"
has=$(which java)
if [ "$?" -ne "0" ]; then
  >&2 echo "❌ no java, install it"
  exit 1
fi

if [ -d "/Applications/Android Studio.app/Contents/jbr/Contents/Home" ]; then
  export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
fi

java -version
echo "ℹ️ java 11 required"
# TODO: exit if not good version?


echo ""
echo "🤖 Check Android SDK"
if [ -z "$ANDROID_HOME" ];then
  # for mac only
  if [ -d "$HOME/Library/Android/sdk" ]; then
    export ANDROID_HOME=$HOME/Library/Android/sdk
  elif [ -d "$HOME/Android/Sdk" ]; then
    export ANDROID_HOME=$HOME/Android/Sdk
  # else Windows: %LOCALAPPDATA%\Android\sdk
  else
    >&2 echo "❌ no ANDROID_HOME defined"
    exit 2
  fi

  export ANDROID_SDK_ROOT=$ANDROID_HOME
  export ANDROID_PREFS_ROOT=$HOME
  export ANDROID_SDK_HOME=$ANDROID_PREFS_ROOT
  export ANDROID_USER_HOME=$ANDROID_PREFS_ROOT/.android
  export PATH=$PATH:$ANDROID_HOME/platform-tools/
  export PATH=$PATH:$ANDROID_HOME/tools/
  export PATH=$PATH:$ANDROID_HOME/tools/bin/
  export PATH=$PATH:$ANDROID_AVD_HOME
fi

if [ "$clean" -eq "1" ]; then
  echo ""
  echo "🧹 Clean"
  ./gradlew clean --console=rich
fi
echo ""
echo "⚙️ Assemble"
./gradlew assemble --console=rich
status=$?

if [ "$status" == "0" ];then
  echo ""
  echo "🎉 Ouput generated in $SOURCE_DIR/build/outputs/aar"
  ls $SOURCE_DIR/build/outputs/aar
else
  exit $status
fi