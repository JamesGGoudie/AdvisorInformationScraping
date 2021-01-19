package ca.goudie.advisorinformationscraping.services.selectors;

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
		return this.selectWebDriver(null);
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
				return this.chromeWebDriver;
			}
		}
	}

}
