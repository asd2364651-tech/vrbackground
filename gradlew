#!/bin/sh

APP_HOME="$(cd "$(dirname "$0")" && pwd)"

# 使用系统的 Java，不强制使用 VS Code JDK
# JAVA_HOME="/Users/yangwenbo/.vscode/extensions/redhat.java-1.52.0-darwin-arm64/jre/21.0.9-macosx-aarch64"
# export JAVA_HOME

GRADLE_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Download gradle wrapper jar if needed
if [ ! -f "$GRADLE_JAR" ]; then
    mkdir -p "$(dirname "$GRADLE_JAR")"
    curl -L https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar -o "$GRADLE_JAR"
fi

# Download gradle distribution if needed
GRADLE_HOME="$HOME/.gradle/wrapper/dists/gradle-8.10"
if [ ! -d "$GRADLE_HOME" ]; then
    mkdir -p "$(dirname "$GRADLE_HOME")"
    curl -L https://services.gradle.org/distributions/gradle-8.10-bin.zip -o /tmp/gradle-8.10.zip
    unzip -q /tmp/gradle-8.10.zip -d "$HOME/.gradle/wrapper/dists/"
    rm /tmp/gradle-8.10.zip
fi

export PATH="$GRADLE_HOME/bin:$PATH"

# Run gradle with TLS settings
exec gradle \
    -Dorg.gradle.jvmargs="-Xmx2048m -Dhttps.protocols=TLSv1.2,TLSv1.3 -Djdk.tls.client.protocols=TLSv1.2,TLSv1.3" \
    "$@"