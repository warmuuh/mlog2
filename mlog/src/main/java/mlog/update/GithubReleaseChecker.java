package mlog.update;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * checks the releases of the given repository if there is a newer version than
 * provided
 */
@RequiredArgsConstructor
public class GithubReleaseChecker {

	private final String owner;
	private final String repository;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class ReleaseResponse {
		@JsonProperty("tag_name")
		private String tagName;
	}

	public Optional<String> getNewerRelease(String releaseToCheck) throws IOException {
		ReleaseResponse response = fetchLatestRelease();
		return Optional.of(response.getTagName())
				.filter(rel -> compareVersions(releaseToCheck, rel) < 0);
	}

	private ReleaseResponse fetchLatestRelease()
			throws IOException {
		String url = "https://api.github.com/repos/" + owner + "/" + repository + "/releases/latest";
		ObjectMapper mapper = new ObjectMapper();
		ReleaseResponse response = mapper.readValue(new URL(url).openStream(), ReleaseResponse.class);
		return response;
	}

	//https://stackoverflow.com/a/27891752
	public static int compareVersions(String version1, String version2) {

		String[] levels1 = version1.split("\\.");
		String[] levels2 = version2.split("\\.");

		int length = Math.max(levels1.length, levels2.length);
		for (int i = 0; i < length; i++) {
			Integer v1 = i < levels1.length ? tryParse(levels1[i], 0) : 0;
			Integer v2 = i < levels2.length ? tryParse(levels2[i], 0) : 0;
			int compare = v1.compareTo(v2);
			if (compare != 0) {
				return compare;
			}
		}

		return 0;
	}
	
	private static int tryParse(String value, int defaultVal) {
	    try {
	        return Integer.parseInt(value);
	    } catch (NumberFormatException e) {
	        return defaultVal;
	    }
	}

}
