package ca.goudie.advisorinformationscraping.services.dihelpers;

import ca.goudie.advisorinformationscraping.enums.KnownHost;
import ca.goudie.advisorinformationscraping.services.scrapers.GenericScraper;
import ca.goudie.advisorinformationscraping.services.scrapers.Scraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScraperSelector {

	@Autowired
	private GenericScraper genericScraper;

	public Scraper selectScraper(final String link) {
		final KnownHost host = KnownHost.getEnum(this.extractHostname(
				link));

		// If the host enum is null...
		if (host == null) {
			// ...then the link doesn't correspond to any known hosts.
			// Use the generic scraper to compensate.
			return this.genericScraper;
		} else {
			return host.getScraper();
		}
	}

	/**
	 * Extracts the hostname from the given link.
	 *
	 * This is done by checking for the existence of the protocol and file and
	 * removing them if they are present.
	 *
	 * @param link
	 * @return
	 */
	private String extractHostname(final String link) {
		String out = link;

		final int protocolStart = out.indexOf("://");

		// If the protocol string exists in the link...
		if (protocolStart != -1) {
			// ...then remove the protocol from the link.
			final int protocolEnd = protocolStart + 3;

			out = out.substring(protocolEnd);
		}

		// If the link still contains a '/' character...
		if (out.contains("/")) {
			// Remove the '/' and everything after it.
			// All that remains is the hostname.
			out = out.substring(0, out.indexOf("/"));
		}

		return out;
	}

}
