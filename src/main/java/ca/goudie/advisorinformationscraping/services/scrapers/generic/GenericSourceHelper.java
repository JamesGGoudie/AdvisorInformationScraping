package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.exceptions.UrlParseException;
import ca.goudie.advisorinformationscraping.models.common.Firm;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class GenericSourceHelper {

	/**
	 * Formats the given URL into something that can be presented by removing
	 * unnecessary information such as query parameters and scheme.
	 *
	 * @param url
	 */
	String formatSource(final String url) {
		try {
			return AisUrlUtils.formatSource(url);
		} catch (UrlParseException e) {
			// Formatting the source failed.
			// Use the unchanged url as the source.
			return url;
		}
	}

	/**
	 * Compares the email address of the firm to the URL that was used as the
	 * source of the information collected.
	 *
	 * If they match, then the URL is likely the URL for the firm.
	 * The URL will then by saved in the given firm object.
	 *
	 * If the email address is not present, then no changes will be made.
	 *
	 * @param firm
	 * @param url
	 */
	void compareFirmEmailAndSource(
			final Firm firm,
			final String url
	) {
		final Collection<String> firmEmails = firm.getEmails();

		for (final String firmEmail : firmEmails) {
			if (StringUtils.isBlank(firmEmail)) {
				return;
			}

			// The email host is everything after the '@' character.
			final String emailHost = firmEmail.substring(firmEmail.indexOf('@') + 1);

			final String sourceHost;

			try {
				sourceHost = AisUrlUtils.extractHostname(url);
			} catch (UrlParseException e) {
				// Parsing failed, but there are other urls to try.
				continue;
			}

			// If the source host ends with the email host...
			if (sourceHost.endsWith(emailHost)) {
				// ...then the source is probably the firms own website.
				// We only check the end of the source host to account for internal
				// sites.
				firm.setFirmUrl(sourceHost);

				break;
			}
		}
	}

}
