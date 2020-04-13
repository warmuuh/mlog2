#!/usr/bin/env sh

BASE_DIR="$(cd "$(dirname "$0")"; pwd)" || exit 2

chmod +x "$BASE_DIR"/jre-linux64/bin/java

"$BASE_DIR"/jre-linux64/bin/java -XX:SharedArchiveFile="$BASE_DIR"/app-cds.jsa \
	-client \
	-XX:+UseCompressedOops \
	-XX:+UseCompressedClassPointers \
	-cp "$BASE_DIR"/plugins/*:"$BASE_DIR"/mlog.jar mlog.MlogApplication
