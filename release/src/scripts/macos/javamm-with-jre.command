#! /bin/sh

JVM_ARGS="-Xmx512M"
MODULE_PATH="modules:."

jre/bin/java ${JVM_ARGS} --module-path ${MODULE_PATH} -m javamm.cmd/com.revenat.javamm.ide.JavammCMDLauncher $1
