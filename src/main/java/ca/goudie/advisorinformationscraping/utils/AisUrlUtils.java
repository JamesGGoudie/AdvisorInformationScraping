package ca.goudie.advisorinformationscraping.utils;

import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;

import java.net.URI;
import java.net.URISyntaxException;

public class AisUrlUtils {

	public static String extractHostname(final String url)
			throws URISyntaxException {
		final URI uri = new URI(url);

		return uri.getHost();
	}

}
