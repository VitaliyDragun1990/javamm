#! /bin/bash

JVM_ARGS="-Xmx512M"
MODULE_PATH="modules:."

${JAVA_HOME}/bin/java ${JVM_ARGS} --module-path ${MODULE_PATH} -m javamm.cmd/com.revenat.javamm.cmd.JavammCMDLauncher $1