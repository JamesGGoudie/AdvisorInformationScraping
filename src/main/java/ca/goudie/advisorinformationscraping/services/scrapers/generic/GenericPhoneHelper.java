package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.utils.AisPhoneUtils;
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
public class GenericPhoneHelper {

	/**
	 * Finds all phone numbers in the current page.
	 *
	 * @param element
	 * @return
	 */
	Collection<String> findPhones(final WebElement element) {
		final Collection<String> out = new HashSet<>();

		out.addAll(this.findPhonesByAnchor(element));
		out.addAll(AisPhoneUtils.findPhones(element.getAttribute("innerHTML")));

		return out;
	}

	/**
	 * Returns true iff the given anchor represents a phone number.
	 *
	 * This is done by checking if the beginning of the href value of an anchor
	 * starts with 'tel:'
	 *
	 * @param anchor
	 * @return
	 */
	boolean isPhoneAnchor(final WebElement anchor) {
		final String href = anchor.getAttribute("href");

		return StringUtils.isNotBlank(href) &&
				href.toLowerCase().startsWith("tel:");
	}

	/**
	 * Searches the page for any anchor tags that are indicated to contain
	 * telephone numbers.
	 *
	 * An anchor contains a phone number if the href value starts with 'tel:'
	 *
	 * @param context
	 * @return
	 */
	private Collection<String> findPhonesByAnchor(final SearchContext context) {
		final Collection<WebElement> anchors = this.findPhoneAnchors(context);
		final Collection<String> out = new HashSet<>();

		for (final WebElement anchor : anchors) {
			final String href = anchor.getAttribute("href");

			// Check the value in the href first since it is more likely to be
			// accurate.
			// This is because numbers displayed to the user may have letters in
			// place of numbers.
			final String hrefText = href.substring(4);
			final String hrefPhone = AisPhoneUtils.findFirstPhone(hrefText);

			if (StringUtils.isNotBlank(hrefPhone)) {
				out.add(hrefPhone);

				continue;
			}

			// Take whatever is displayed to the user instead.
			final String innerPhone = anchor.getAttribute("innerText");

			if (StringUtils.isNotBlank(innerPhone)) {
				out.add(innerPhone);
			}
		}

		return out;
	}

	/**
	 * Returns all anchors that represent phone numbers.
	 *
	 * @param context
	 * @return
	 */
	private Collection<WebElement> findPhoneAnchors(final SearchContext context) {
		final List<WebElement> anchors = context.findElements(By.tagName("a"));
		final Collection<WebElement> out = new HashSet<>();

		for (final WebElement anchor : anchors) {
			try {
				if (this.isPhoneAnchor(anchor)) {
					out.add(anchor);
				}
			} catch (StaleElementReferenceException e) {
				continue;
			}
		}

		return out;
	}

}
