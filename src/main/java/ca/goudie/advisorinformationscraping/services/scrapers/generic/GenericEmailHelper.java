package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.exceptions.DomReadException;
import ca.goudie.advisorinformationscraping.utils.AisRegexUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Log4j2
@Service
public class GenericEmailHelper {

	/**
	 * Returns true iff the anchor is an email anchor.
	 *
	 * This is determined by checking if the href value starts with 'mailto:'
	 *
	 * @param anchor
	 * @return
	 * @throws DomReadException
	 */
	boolean isEmailAnchor(
			final WebElement anchor
	) throws DomReadException {
		try {
			final String href = anchor.getAttribute("href");

			return StringUtils.isNotBlank(href) &&
					href.toLowerCase().startsWith("mailto:");
		} catch (StaleElementReferenceException e) {
			throw new DomReadException(e);
		}
	}

	/**
	 * Finds all email anchors in the given context.
	 *
	 * @param context
	 * @return
	 */
	Collection<WebElement> findEmailAnchors(final SearchContext context) {
		final Collection<WebElement> out = new HashSet<>();
		final List<WebElement> anchors;

		try {
			anchors = context.findElements(By.tagName("a"));
		} catch (StaleElementReferenceException e) {
			return out;
		}

		for (final WebElement anchor : anchors) {
			try {
				if (this.isEmailAnchor(anchor)) {
					out.add(anchor);
				}
			} catch (DomReadException e) {
				continue;
			}
		}

		return out;
	}

	/**
	 * Anchor tags can be used to open a mail app to send an email. This is done
	 * by starting the anchor href value with 'mailto:'
	 * <p>
	 * Search the page for these anchors and return the first email address.
	 *
	 * @param context
	 *
	 * @return
	 */
	Collection<String> findEmailsByAnchor(final SearchContext context) {
		log.info("Searching for Emails by Anchor");

		final Collection<WebElement> anchors = this.findEmailAnchors(context);
		final Collection<String> out = new HashSet<>();

		for (final WebElement anchor : anchors) {
			final String href;
			final String innerText;

			try {
				href = anchor.getAttribute("href");

				// Check the innerText for an email that is displayed to the user.
				innerText = anchor.getAttribute("innerText");
			} catch (StaleElementReferenceException e) {
				continue;
			}

			final String innerEmail = AisRegexUtils.findFirstEmail(innerText);

			if (StringUtils.isNotBlank(innerEmail)) {
				out.add(innerEmail);

				continue;
			}

			// We could not find an email in the innerText
			// Check what is after the mailto: instead and see if that is valid.

			final String hrefText = href.substring(7);
			final String hrefEmail = AisRegexUtils.findFirstEmail(hrefText);

			if (StringUtils.isNotBlank(hrefEmail)) {
				out.add(hrefEmail);
			}
		}

		return out;
	}

}
