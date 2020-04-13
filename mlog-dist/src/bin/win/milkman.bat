@start .\jre-win64\bin\javaw.exe ^
	-XX:SharedArchiveFile=app-cds.jsa ^
	-client ^
	-XX:+UseCompressedOops ^
	-XX:+UseCompressedClassPointers ^
	-cp plugins\*;mlog.jar mlog.MlogApplication
	