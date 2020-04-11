package mlog;


import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MlogApplication {

	public static void main(String[] args) throws IOException, InterruptedException {
		long vmStartTime = ManagementFactory.getRuntimeMXBean().getUptime();
		log.info("VM Startup time: {} ms", vmStartTime);


		FlatDarkLaf.install();


		MainModule module = new MainModule();
		module.start();

		module.getMainWindow().setVisible(true);

		log.info("App Startup time: {} ms", ManagementFactory.getRuntimeMXBean().getUptime() - vmStartTime);
	}

}
