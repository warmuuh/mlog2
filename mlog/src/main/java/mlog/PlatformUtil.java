package mlog;


import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PlatformUtil {

	public static boolean isCurrentDirWritable() {
		return Files.isWritable(new File(".").toPath());
	}
	
	public static String getWritableLocationForFile(String filename) {
		if (Files.isWritable(new File(".").toPath())) {
			return filename;
		} 
		return getOsSpecificAppDataFolder(filename);
	}

//	public static KeyCombination getControlKeyCombination(KeyCode keyCode){
//		KeyCombination.Modifier controlKey = KeyCombination.CONTROL_DOWN;
//		if (SystemUtils.IS_OS_MAC){
//			controlKey = KeyCombination.META_DOWN;
//		}
//		return new KeyCodeCombination(keyCode, controlKey);
//	}

	private static String getOsSpecificAppDataFolder(String filename) {
		if (SystemUtils.IS_OS_WINDOWS) {
			return Paths.get(System.getenv("LOCALAPPDATA"), "Milkman", filename).toString();
		} else if (SystemUtils.IS_OS_MAC) {
			return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "Milkman", filename).toString();
		} else if (SystemUtils.IS_OS_LINUX) {
			return Paths.get(System.getProperty("user.home"), ".milkman", filename).toString();
		}
		
		return filename;
		
	}
	
}
