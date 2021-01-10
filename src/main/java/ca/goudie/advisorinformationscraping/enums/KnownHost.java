package ca.goudie.advisorinformationscraping.enums;

import ca.goudie.advisorinformationscraping.services.scrapers.BloombergScraper;
import ca.goudie.advisorinformationscraping.services.scrapers.GenericScraper;
import ca.goudie.advisorinformationscraping.services.scrapers.Scraper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * A Known Host is a host that has been deemed of having a sufficient amount of
 * desired information as to require a specialized scraper.
 *
 * A known host does not necessarily have a specialized scraper, as it may not
 * have been implemented yet.
 */
public enum KnownHost {

	BLOOMBERG(KnownHost.BLOOMBERG_HOSTNAME),
	COMPANIES_HOUSE(KnownHost.COMPANIES_HOUSE_HOSTNAME);

	private static final String BLOOMBERG_HOSTNAME = "www.bloomberg.com";
	private static final String COMPANIES_HOUSE_HOSTNAME =
			"find-and-update.company-information.service.gov.uk";

	private static BloombergScraper bloombergScraper;
	private static GenericScraper genericScraper;

	private final String hostname;

	KnownHost(final String hostname) {
		this.hostname = hostname;
	}

	/**
	 * We can't use Autowired on enum fields.
	 * But we can create a static class within the enum.
	 * This way, we can give any autowired fields to the enum.
	 *
	 * Everything in this class has been made private to prevent any manipulation.
	 */
	@Component
	private static class Injector {

		@Autowired
		private BloombergScraper bloombergScraper;

		@Autowired
		private GenericScraper genericScraper;

		private Injector() {}

		@PostConstruct
		private void postConstruct() {
			KnownHost.bloombergScraper = this.bloombergScraper;
			KnownHost.genericScraper = this.genericScraper;
		}

	}

	/**
	 * Returns the enum of the hostname.
	 * Returns null if no such enum exists.
	 *
	 * @param hostname
	 * @return
	 */
	public static KnownHost getEnum(final String hostname) {
		if (StringUtils.isBlank(hostname)) {
			return null;
		}

		final String cleanHostname = hostname.toLowerCase().trim();

		switch (cleanHostname) {
			case KnownHost.BLOOMBERG_HOSTNAME: {
				return KnownHost.BLOOMBERG;
			}
			case KnownHost.COMPANIES_HOUSE_HOSTNAME: {
				return KnownHost.COMPANIES_HOUSE;
			}
			default: {
				return null;
			}
		}
	}

	/**
	 * Returns the specialized scraper associated with this known hostname.
	 *
	 * If the scraper does not exist, then the generic scraper will be returned
	 * instead.
	 *
	 * @return
	 */
	public Scraper getScraper() {
		switch (this) {
			case BLOOMBERG: {
				return this.bloombergScraper;
			}
			default: {
				return this.genericScraper;
			}
		}
	}

}
