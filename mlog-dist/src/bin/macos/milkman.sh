#!/bin/sh
chmod +x jre-macos64/bin/java

./jre-macos64/bin/java -XX:SharedArchiveFile=app-cds.jsa \
	-client \
	-XX:+UseCompressedOops \
	-XX:+UseCompressedClassPointers \
	-cp plugins/*:mlog.jar mlog.MlogApplication
