
JVM_ARGS="-Xmx512M"
MODULE_PATH="lib:modules:."

ADD_MODULES="--add-modules javafx.controls,javafx.graphics,javafx.fxml"

dir=${0%/*}
if [ "$dir" = "$0" ]; then
  dir="."
fi
cd "$dir"

jre/bin/java ${JVM_ARGS} --module-path ${MODULE_PATH} ${ADD_MODULES} -m javamm.ide/com.revenat.javamm.ide.JavammIDELauncher
