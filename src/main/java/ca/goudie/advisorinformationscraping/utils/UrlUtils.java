package ca.goudie.advisorinformationscraping.utils;

import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlUtils {

	public static String extractHostname(final String url)
			throws ScrapingFailedException {
		try {
			final URI uri = new URI(url);

			return uri.getHost();
		} catch (URISyntaxException e) {
			throw new ScrapingFailedException(e);
		}
	}

}
