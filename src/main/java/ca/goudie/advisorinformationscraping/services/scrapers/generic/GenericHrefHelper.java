package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.exceptions.DomReadException;
import ca.goudie.advisorinformationscraping.exceptions.UrlParseException;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;

@Log4j2
@Service
public class GenericHrefHelper {

	/**
	 * Returns true iff the given anchor contains a non-blank href attribute.
	 *
	 * @param anchor
	 * @return
	 */
	boolean doesHrefExist(final WebElement anchor) throws DomReadException {
		try {
			final String href = anchor.getAttribute("href");

			return StringUtils.isNotBlank(href);
		} catch (StaleElementReferenceException e) {
			throw new DomReadException(e);
		}
	}

	/**
	 * It is possible to have anchors that are missing the hostname.
	 *
	 * These anchors would use the hostname of the current page in a normal
	 * browser.
	 *
	 * Unfortunately, this doesn't work too well with Selenium, so we have to fix
	 * the links.
	 *
	 * @param links
	 * @param pageUrl
	 * @return
	 */
	Collection<String> cleanLinks(
			final Collection<String> links,
			final String pageUrl
	) {
		log.info("Cleaning " + links.size() + " HREFs");

		final String pageAuthority;

		try {
			pageAuthority = AisUrlUtils.removePath(pageUrl);
		} catch (UrlParseException e) {
			// Couldn't clean links; return uncleaned links
			log.error(e);

			return links;
		}

		final Collection<String> fixedLinks = new HashSet<>();

		for (final String link : links) {
			try {
				// If the link is missing the hostname...
				if (!AisUrlUtils.hasAuthority(link)) {
					// Prepend the link with the page authority.
					fixedLinks.add(pageAuthority + link);
				} else {
					// No need to modify the link.
					fixedLinks.add(link);
				}
			} catch (UrlParseException e) {
				log.error(e);
				// Parsing failed, but let's keep the link just in case.
				fixedLinks.add(link);
			}
		}

		return fixedLinks;
	}

	/**
	 * Adds the website's scheme and authority to the given link if it is missing.
	 *
	 * @param link
	 * @param pageUrl
	 * @return
	 */
	String cleanLink(final String link, final String pageUrl) {
		log.info("Cleaning HREF");

		final String pageAuthority;

		try {
			pageAuthority = AisUrlUtils.removePath(pageUrl);
		} catch (UrlParseException e) {
			// Couldn't parse the given link; return it instead.
			log.error(e);

			return link;
		}

		try {
			// If the link is missing the hostname...
			if (!AisUrlUtils.hasAuthority(link)) {
				// Prepend the link with the page authority.
				return pageAuthority + link;
			} else {
				return link;
			}
		} catch (UrlParseException e) {
			// Couldn't parse the given link; return it instead.
			log.error(e);

			return link;
		}
	}

}
