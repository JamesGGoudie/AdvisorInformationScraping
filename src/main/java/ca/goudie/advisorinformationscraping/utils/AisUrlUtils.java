package ca.goudie.advisorinformationscraping.utils;

import ca.goudie.advisorinformationscraping.exceptions.UrlParseException;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class AisUrlUtils {

	public static String extractHostname(final String url)
			throws UrlParseException {
		try {
			final URI uri = new URI(url);

			return uri.getHost();
		} catch (URISyntaxException e) {
			throw new UrlParseException(e);
		}
	}

	public static boolean hasAuthority(
			final String url
	) throws UrlParseException {
		try {
			final URI uri = new URI(url);

			return StringUtils.isNotBlank(uri.getAuthority());
		} catch (URISyntaxException e) {
			throw new UrlParseException(e);
		}
	}

	public static String extractPath(final String url) throws UrlParseException {
		try {
			final URI uri = new URI(url);

			return uri.getPath();
		} catch (URISyntaxException e) {
			throw new UrlParseException(e);
		}
	}

	public static String removePath(final String url) throws UrlParseException {
		try {
			final URI uri = new URI(url);

			String out = "";

			if (StringUtils.isNotBlank(uri.getScheme())) {
				out += uri.getScheme() + ":";
			}

			if (StringUtils.isNotBlank(uri.getAuthority())) {
				out += "//" + uri.getAuthority();
			}

			return out;
		} catch (URISyntaxException e) {
			throw new UrlParseException(e);
		}
	}

	/**
	 * Formats the given URL into a presentable source.
	 *
	 * The result is the hostname, the port (if it exists), and the path.
	 * The trailing '/' is removed, if present.
	 *
	 * @param url
	 *
	 * @return
	 *
	 * @throws URISyntaxException
	 */
	public static String formatSource(final String url)
			throws UrlParseException {
		try {
			final URI uri = new URI(url);

			String out = uri.getHost();

			if (uri.getPort() != -1) {
				out += ":" + uri.getPort();
			}

			out += uri.getPath();

			if (out.endsWith("/")) {
				out = out.substring(0, out.length() - 1);
			}

			return out;
		} catch (URISyntaxException e) {
			throw new UrlParseException(e);
		}
	}

}
