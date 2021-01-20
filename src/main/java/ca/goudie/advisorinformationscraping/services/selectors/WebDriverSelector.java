package ca.goudie.advisorinformationscraping.services.selectors;

import ca.goudie.advisorinformationscraping.logging.LoggingWebDriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebDriverSelector {

	@Value("${selenium.chrome-driver-location}")
	private String chromeDriverLocation;

	private static final String CHROME_KEY = "C";

	/**
	 * Selects the default web driver.
	 *
	 * @return
	 */
	public WebDriver selectWebDriver() {
		return this.getDefault();
	}

	/**
	 * Selects a web driver using the given key.
	 *
	 * @param key
	 * @return
	 */
	public WebDriver selectWebDriver(final String key) {
		switch (key) {
			default: {
				return this.getDefault();
			}
		}
	}

	private WebDriver getDefault() {
		return new LoggingWebDriver(this.buildChromeWebDriver());
	}

	private WebDriver buildChromeWebDriver() {
		// Need to tell Selenium where the chromedriver file is.
		System.setProperty("webdriver.chrome.driver", this.chromeDriverLocation);

		final ChromeOptions options = new ChromeOptions();
		// By setting Chrome to headless, we don't need GPU resources.
		options.setHeadless(true);

		return new ChromeDriver(options);
	}

}
