package mlog.update;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mlog.utils.VersionLoader;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_={@Inject})
public class UpdateChecker {

	private String currentVersion;
	
	private GithubReleaseChecker releaseChecker = new GithubReleaseChecker("warmuuh", "mlog2");
	
	public void checkForUpdateAsync() {
		new Thread(() -> {
			try {
				releaseChecker.getNewerRelease(currentVersion).ifPresent(newVersion -> {
//					toaster.showToast("New Version available: Mlog " + newVersion, "What's new?", e -> {
//						try {
//							Desktop.getDesktop().browse(new URI("https://github.com/warmuuh/mlog2/blob/master/changelog.md"));
//						} catch (IOException | URISyntaxException e1) {
//							e1.printStackTrace();
//						}
//					});
				});
			} catch (IOException e) {
				log.error("Failed to fetch release information", e);
			}
		}).run();
	}
	
	
	@PostConstruct
	public void loadCurrentVersion() {
    	currentVersion = VersionLoader.loadCurrentVersion();
	}
	
}
