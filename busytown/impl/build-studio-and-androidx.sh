#!/bin/bash
set -e

echo "Starting $0 at $(date)"

androidxArguments="$*"

WORKING_DIR="$(pwd)"
SCRIPTS_DIR="$(cd $(dirname $0)/.. && pwd)"
cd "$SCRIPTS_DIR/../../.."
echo "Script running from $(pwd)"

# Resolve JDK folders for host OS
STUDIO_JDK="linux"
PREBUILT_JDK="linux-x86"
if [[ $OSTYPE == darwin* ]]; then
  STUDIO_JDK="mac/Contents/Home"
  PREBUILT_JDK="darwin-x86"
fi

# resolve dirs
export OUT_DIR=$(pwd)/out

if [ -z "$DIST_DIR" ]; then
  DIST_DIR="$OUT_DIR/dist"
fi
mkdir -p "$DIST_DIR"

export DIST_DIR="$DIST_DIR"

# resolve GRADLE_USER_HOME
export GRADLE_USER_HOME="$DIST_DIR/gradle"
mkdir -p "$GRADLE_USER_HOME"

if [ "$STUDIO_DIR" == "" ]; then
  STUDIO_DIR="$WORKING_DIR"
else
  STUDIO_DIR="$(cd $STUDIO_DIR && pwd)"
fi

TOOLS_DIR=$STUDIO_DIR/tools
gw="$TOOLS_DIR/gradlew -Dorg.gradle.jvmargs=-Xmx24g"

function buildStudio() {
  STUDIO_BUILD_LOG="$OUT_DIR/studio.log"
  if JAVA_HOME="$STUDIO_DIR/prebuilts/studio/jdk/jdk11/$STUDIO_JDK" $gw -p $TOOLS_DIR publishLocal --stacktrace --no-daemon > "$STUDIO_BUILD_LOG" 2>&1; then
    echo built studio successfully
  else
    cat "$STUDIO_BUILD_LOG" >&2
    echo failed to build studio
    return 1
  fi

  # stop any remaining Gradle daemons, b/205883835
  JAVA_HOME="$STUDIO_DIR/prebuilts/studio/jdk/jdk11/$STUDIO_JDK" $gw -p $TOOLS_DIR --stop
}

function zipStudio() {
  cd "$STUDIO_DIR/out/"
  zip -qr "$DIST_DIR/tools.zip" repo
  cd -
}

buildStudio
zipStudio

# list java processes to check for any running kotlin daemons, b/201504768
function listJavaProcesses() {
  echo "All java processes:"
  ps -ef | grep /java || true
}
listJavaProcesses

# kill kotlin compile daemons in hopes of addressing memory problems, b/201504768
function killKotlinDaemons() {
  ps -ef | grep -i java.*kotlin-daemon-embeddable.*org.jetbrains.kotlin.daemon.KotlinCompileDaemon | grep -v grep | awk '{print $2}' | xargs --no-run-if-empty kill || true
}
killKotlinDaemons

listJavaProcesses

# Mac grep doesn't support -P, so use perl version of `grep -oP "(?<=buildVersion = ).*"`
export LINT_VERSION=`perl -nle'print $& while m{(?<=baseVersion = ).*}g' $TOOLS_DIR/buildSrc/base/version.properties`
export GRADLE_PLUGIN_VERSION=`perl -nle'print $& while m{(?<=buildVersion = ).*}g' $TOOLS_DIR/buildSrc/base/version.properties`
export GRADLE_PLUGIN_REPO="$STUDIO_DIR/out/repo:$STUDIO_DIR/prebuilts/tools/common/m2/repository"
export JAVA_HOME="$(pwd)/prebuilts/jdk/jdk11/$PREBUILT_JDK/"
export JAVA_TOOLS_JAR="$(pwd)/prebuilts/jdk/jdk8/$PREBUILT_JDK/lib/tools.jar"
export LINT_PRINT_STACKTRACE=true

function buildAndroidx() {
  LOG_PROCESSOR="$SCRIPTS_DIR/../development/build_log_processor.sh"
  properties="-Pandroidx.summarizeStderr --no-daemon"
  "$LOG_PROCESSOR" $gw $properties -p frameworks/support $androidxArguments --profile \
    --dependency-verification=off # building against tip of tree of AGP that potentially pulls in new dependencies
  $SCRIPTS_DIR/impl/parse_profile_htmls.sh

  # zip build scan
  scanZip="$DIST_DIR/scan.zip"
  rm -f "$scanZip"
  cd "$GRADLE_USER_HOME/build-scan-data"
  zip -q -r "$scanZip" .
  cd -
}

buildAndroidx
echo "Completing $0 at $(date)"
