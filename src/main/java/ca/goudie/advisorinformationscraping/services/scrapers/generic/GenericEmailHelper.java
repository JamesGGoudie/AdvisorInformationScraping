package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.utils.AisRegexUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class GenericEmailHelper {

	/**
	 * Returns true iff the anchor is an email anchor.
	 *
	 * This is determined by checking if the href value starts with 'mailto:'
	 *
	 * @param anchor
	 * @return
	 * @throws StaleElementReferenceException
	 */
	boolean isEmailAnchor(
			final WebElement anchor
	) throws StaleElementReferenceException {
		final String href = anchor.getAttribute("href");

		return StringUtils.isNotBlank(href) &&
				href.toLowerCase().startsWith("mailto:");
	}

	/**
	 * Finds all email anchors in the given context.
	 *
	 * @param context
	 * @return
	 */
	Collection<WebElement> findEmailAnchors(final SearchContext context) {
		final List<WebElement> anchors = context.findElements(By.tagName("a"));
		final Collection<WebElement> out = new HashSet<>();

		for (final WebElement anchor : anchors) {
			try {
				if (this.isEmailAnchor(anchor)) {
					out.add(anchor);
				}
			} catch (StaleElementReferenceException e) {
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
		final Collection<WebElement> anchors = this.findEmailAnchors(context);
		final Collection<String> out = new HashSet<>();

		for (final WebElement anchor : anchors) {
			final String href = anchor.getAttribute("href");

			// Check the innerText for an email that is displayed to the user.
			final String innerText = anchor.getAttribute("innerText");
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
