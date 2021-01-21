package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.dto.EmployeeResult;
import ca.goudie.advisorinformationscraping.dto.FirmResult;
import ca.goudie.advisorinformationscraping.dto.specialized.bloomberg.IBloombergEmployee;
import ca.goudie.advisorinformationscraping.dto.specialized.bloomberg.IBloombergOrganization;
import ca.goudie.advisorinformationscraping.exceptions.ScrapeException;
import ca.goudie.advisorinformationscraping.utils.json.AisJsonUtils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * https://www.bloomberg.com
 */
@Log4j2
@Service
public class BloombergScraper implements IScraper {

	/**
	 * The value to use in the 'Source' column of the results.
	 */
	private static final String BLOOMBERG_SOURCE = "Bloomberg";

	private static final String JSON_NOT_FOUND_MSG =
			"Could not find Bloomberg organization JSON.";

	private static final String CAPTCHA_TITLE = "Bloomberg - Are you a robot?";
	private static final String CRITICAL_COOKIE = "_px2";
	private static final int MAX_COOKIE_WAIT_TIME = 10;
	private static final int POST_COOKIE_WAIT_TIME = 2;

	@Override
	public FirmResult scrapeWebsite(
			final WebDriver driver, final String url, final String countryCode
	) throws ScrapeException {
		driver.get(url);

		this.checkAndCircumventCaptcha(driver, url);
		IBloombergOrganization org = this.extractOrganizationData(driver);

		return this.buildFirmResult(org);
	}

	/**
	 * Bloomberg has some kind of system to detect bots and stop them with a
	 * captcha, but I think that I've found a way around it.
	 * <p>
	 * From my understanding, we require a specific cookie to bypass the captcha.
	 * This cookie comes from a request fired when the captcha screen finishes
	 * loading. Once we have the cookie, if we re-navigate to the given URL, we
	 * should be able to reach the page as intended.
	 * <p>
	 * This isn't fool proof, obviously, but it's the best that I could come up
	 * with.
	 *
	 * @param driver
	 *
	 * @throws ScrapeException
	 */
	private void checkAndCircumventCaptcha(
			final WebDriver driver, final String url
	) throws ScrapeException {
		// Check the title to see if we were stopped by the captcha.
		if (driver.getTitle().equals(BloombergScraper.CAPTCHA_TITLE)) {
			log.info("Bloomberg - Blocked by Captcha");

			// To bypass the captcha, we need a cookie from a pending request.
			// Wait until we have the cookie or until a enough time has passed.
			for (int i = 0; i < BloombergScraper.MAX_COOKIE_WAIT_TIME; ++i) {
				if (driver.manage().getCookieNamed(BloombergScraper.CRITICAL_COOKIE) !=
						null) {
					break;
				}

				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					throw new ScrapeException(e);
				}
			}

			// For some reason, we need to wait some more after we have the cookie
			// before we re-navigate.
			try {
				TimeUnit.SECONDS.sleep(BloombergScraper.POST_COOKIE_WAIT_TIME);
			} catch (InterruptedException e) {
				throw new ScrapeException(e);
			}

			// Now that we have the cookie, we need to re-navigate to the website.
			// The captcha screen has a different URL, so refreshing wouldn't work.
			driver.get(url);

			if (driver.getTitle().equals(BloombergScraper.CAPTCHA_TITLE)) {
				log.info("Bloomberg - Blocked by Captcha Again");

				// Got caught by the Captcha again.
				throw new ScrapeException("Could not beat Bloomberg Captcha");
			}

			log.info("Bloomberg - Circumvented Captcha");
		}
	}

	/**
	 * The Bloomberg DOM contains all of the organization in a convenient JSON.
	 * This method extracts the JSON and converts it to a Java class.
	 *
	 * @param driver
	 * @return
	 * @throws ScrapeException
	 */
	private IBloombergOrganization extractOrganizationData(
			final WebDriver driver
	) throws ScrapeException {
		final List<WebElement> scripts;

		try {
			scripts = driver.findElements(By.xpath("/html/head/script"));
		} catch (StaleElementReferenceException e) {
			throw new ScrapeException(BloombergScraper.JSON_NOT_FOUND_MSG);
		}

		for (final WebElement script : scripts) {
			final String type;

			try {
				type = script.getAttribute("type");
			} catch (StaleElementReferenceException e) {
				// The element is not present in the DOM; continue
				continue;
			}

			if (StringUtils.isNotBlank(type) &&
					type.equals("application/ld+json")) {
				final String jsonStr;

				try {
					jsonStr = script.getAttribute("innerText");
				} catch (StaleElementReferenceException e) {
					continue;
				}

				if (AisJsonUtils.isBloombergOrganizationJson(jsonStr)) {
					return AisJsonUtils.parseBloombergJson(jsonStr);
				}
			}
		}

		throw new ScrapeException(BloombergScraper.JSON_NOT_FOUND_MSG);
	}

	private FirmResult buildFirmResult(final IBloombergOrganization org) {
		final FirmResult firm = new FirmResult();

		firm.getAddresses().add(this.cleanAddress(org.getAddress()));
		firm.setFirmUrl(org.getUrl());
		firm.getPhones().add(org.getTelephone());
		firm.getEmployees().addAll(this.buildEmployees(org));

		firm.setSource(BloombergScraper.BLOOMBERG_SOURCE);

		return firm;
	}

	private Collection<EmployeeResult> buildEmployees(
			final IBloombergOrganization org
	) {
		final Collection<EmployeeResult> employees = new ArrayList<>();

		for (final IBloombergEmployee bloombergEmployee : org.getEmployees()) {
			final EmployeeResult employee = new EmployeeResult();

			employee.setName(bloombergEmployee.getName());
			employee.setTitle(bloombergEmployee.getTitle());

			employee.setSource(BloombergScraper.BLOOMBERG_SOURCE);

			employees.add(employee);
		}

		return employees;
	}

	/**
	 * The address in the JSON object contains newline characters that need to be
	 * removed.
	 *
	 * @param address
	 * @return
	 */
	private String cleanAddress(final String address) {
		return address.replace("\n", " ");
	}

}
