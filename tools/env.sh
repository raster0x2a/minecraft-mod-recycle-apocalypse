#!/usr/bin/env bash

SCRIPT_PATH="${BASH_SOURCE[0]:-$0}"
PROJECT_ROOT="$(CDPATH= cd -- "$(dirname -- "$SCRIPT_PATH")/.." && pwd)"
export JAVA_HOME="$PROJECT_ROOT/.tools/jdk-25.0.3+9"
export GRADLE_HOME="$PROJECT_ROOT/.tools/gradle-9.5.1"
export GRADLE_USER_HOME="$PROJECT_ROOT/.gradle-home"
export PATH="$JAVA_HOME/bin:$GRADLE_HOME/bin:$PATH"
