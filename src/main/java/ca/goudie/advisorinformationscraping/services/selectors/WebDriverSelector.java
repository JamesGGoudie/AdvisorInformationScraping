package ca.goudie.advisorinformationscraping.services.selectors;

import ca.goudie.advisorinformationscraping.logging.LoggingWebDriver;

import org.openqa.selenium.WebDriver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class WebDriverSelector {

	private static final String CHROME_KEY = "C";

	@Autowired
	@Qualifier("chromeWebDriver")
	private WebDriver chromeWebDriver;

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
		return new LoggingWebDriver(this.chromeWebDriver);
	}

}
