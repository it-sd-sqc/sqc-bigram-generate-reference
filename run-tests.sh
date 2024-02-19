#!/usr/bin/env bash

# Specify the correct PATH delimiter based on platform.
WHERE="$(uname -s)"
DELIM=":"

case "$(uname -s)" in
  CYGWIN*|MINGW*|MINGW32*|MSYS*)
    DELIM=";"
  ;;
esac


if javac -d out/production/sqc-bigram-generate/ src/edu/cvtc/bigram/Generate.java && javac -d out/test/sqc-bigram-generate/ -cp "lib/*${DELIM}out/production/sqc-bigram-generate" test/edu/cvtc/bigram/*.java; then
  java -jar ./lib/junit-platform-console-standalone-1.10.2.jar execute --class-path "lib/slf4j-api-2.0.9.jar${DELIM}lib/slf4j-nop-2.0.9.jar${DELIM}lib/sqlite-jdbc-3.44.1.0.jar${DELIM}out/production/sqc-bigram-generate${DELIM}out/test/sqc-bigram-generate" --scan-classpath --details flat --disable-banner 
fi

