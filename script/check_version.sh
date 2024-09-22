JAVA_VERSION_FULL=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
JAVA_VERSION_MAJOR=$(echo $JAVA_VERSION_FULL | awk -F '.' '{sub("^$", "0", $2); print $1}')
if [[ "$JAVA_VERSION_MAJOR" < "21" ]]; then
  echo "Error: Java >= 21 is required to run Zephyr, you have version $JAVA_VERSION_FULL"
  exit 1
fi