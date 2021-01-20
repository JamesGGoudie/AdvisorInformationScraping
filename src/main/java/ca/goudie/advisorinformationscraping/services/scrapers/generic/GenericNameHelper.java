package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.exceptions.UrlParseException;
import ca.goudie.advisorinformationscraping.utils.AisRegexUtils;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class GenericNameHelper {

	/**
	 * Tries to determine the name of the employee from the URL.
	 *
	 * We expect this to be the final segment of the path.
	 *
	 * Will disregard if the final segment contains numbers or specials symbols.
	 *
	 * @param url
	 * @return
	 */
	String findEmployeeNameInUrl(final String url) {
		log.info("Checking URL for Employee Name");

		final String path;

		try {
			path = AisUrlUtils.extractPath(url);
		} catch (UrlParseException e) {
			return null;
		}

		final List<String> pathSegments = AisRegexUtils.findPathSegments(path);

		if (pathSegments.size() == 0) {
			return null;
		}

		final String candidate = pathSegments.get(pathSegments.size() - 1);

		if (!AisRegexUtils.isPossiblyName(candidate)) {
			return null;
		}

		final String[] names = candidate.split("-");

		String out = "";

		for (final String name : names) {
			out += name.substring(0, 1).toUpperCase() + name.substring(1) + " ";
		}

		return out.trim();
	}

}
