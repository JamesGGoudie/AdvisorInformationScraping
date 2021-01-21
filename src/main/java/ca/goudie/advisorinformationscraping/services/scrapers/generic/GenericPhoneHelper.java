package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.exceptions.DomReadException;
import ca.goudie.advisorinformationscraping.utils.AisPhoneUtils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Log4j2
@Service
public class GenericPhoneHelper {

	/**
	 * Finds all phone numbers in the current page.
	 *
	 * @param element
	 * @param countryCode
	 * @return
	 */
	Collection<String> findPhones(
			final WebElement element,
			final String countryCode
	) {
		final Collection<String> out = new HashSet<>();

		out.addAll(this.findPhonesByAnchor(element, countryCode));

		try {
			final String innerHtml = element.getAttribute("innerHTML");
			out.addAll(AisPhoneUtils.findPhones(innerHtml, countryCode));
		} catch (StaleElementReferenceException e) {
			// Couldn't get innerHTML, but we may have still gotten anchor phones.
			log.error(e);
		}

		return out;
	}

	/**
	 * Finds all phone numbers in the current page.
	 *
	 * @param driver
	 * @param countryCode
	 * @return
	 */
	Collection<String> findPhones(
			final WebDriver driver,
			final String countryCode
	) {
		log.info("Searching for Phones");

		final Collection<String> out = new HashSet<>();

		out.addAll(this.findPhonesByAnchor(driver, countryCode));

		try {
			final String innerHtml = driver.getPageSource();
			out.addAll(AisPhoneUtils.findPhones(innerHtml, countryCode));
		} catch (StaleElementReferenceException e) {
			// Couldn't get innerHTML, but we may have still gotten anchor phones.
			log.error(e);
		}

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
	 * @throws DomReadException
	 */
	boolean isPhoneAnchor(
			final WebElement anchor
	) throws DomReadException {
		try {
			final String href = anchor.getAttribute("href");

			return StringUtils.isNotBlank(href) &&
					href.toLowerCase().startsWith("tel:");
		} catch (StaleElementReferenceException e) {
			throw new DomReadException(e);
		}
	}

	/**
	 * Searches the page for any anchor tags that are indicated to contain
	 * telephone numbers.
	 *
	 * An anchor contains a phone number if the href value starts with 'tel:'
	 *
	 * @param context
	 * @param countryCode
	 * @return
	 */
	private Collection<String> findPhonesByAnchor(
			final SearchContext context,
			final String countryCode
	) {
		final Collection<WebElement> anchors = this.findPhoneAnchors(context);
		final Collection<String> out = new HashSet<>();

		for (final WebElement anchor : anchors) {
			final String href;


			try {
				href = anchor.getAttribute("href");
			} catch (StaleElementReferenceException e) {
				// Anchor is stale; try next one
				log.error(e);

				continue;
			}

			// Check the value in the href first since it is more likely to be
			// accurate.
			// This is because numbers displayed to the user may have letters in
			// place of numbers.
			final String hrefText = href.substring(4);
			final String hrefPhone = AisPhoneUtils.findFirstPhone(
					hrefText, countryCode);

			if (StringUtils.isNotBlank(hrefPhone)) {
				out.add(hrefPhone);

				continue;
			}

			// We were unable to parse the HREF phone value.
			// Try to parse the value displayed to the user instead.

			final String innerText;

			try {
				innerText = anchor.getAttribute("innerText");
			} catch (StaleElementReferenceException e) {
				// Anchor is stale; try next one
				log.error(e);

				continue;
			}

			if (StringUtils.isBlank(innerText)) {
				continue;
			}

			final String innerPhone = AisPhoneUtils.findFirstPhone(
					innerText, countryCode);

			if (StringUtils.isNotBlank(innerPhone)) {
				out.add(innerPhone);
			} else {
				// The text value was not a valid phone number.
				// This could be caused from letters replacing the numbers.
				// To be safe, let's trust the innerText blindly.
				out.add(innerText);
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
		final Collection<WebElement> out = new HashSet<>();
		final List<WebElement> anchors;

		try {
			anchors = context.findElements(By.tagName("a"));
		} catch (StaleElementReferenceException e) {
			// Anchor is stale; try next one
			log.error(e);

			return out;
		}

		for (final WebElement anchor : anchors) {
			try {
				if (this.isPhoneAnchor(anchor)) {
					out.add(anchor);
				}
			} catch (DomReadException e) {
				// Couldn't read DOM; try next anchor
				log.error(e);

				continue;
			}
		}

		return out;
	}

}
