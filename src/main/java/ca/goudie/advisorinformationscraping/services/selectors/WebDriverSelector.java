package ca.goudie.advisorinformationscraping.services.selectors;

import ca.goudie.advisorinformationscraping.constants.WebBrowserConstants;
import ca.goudie.advisorinformationscraping.logging.LoggingWebDriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class WebDriverSelector {

	@Value("${selenium.chrome-driver-location}")
	private String chromeDriverLocation;

	@Value("${selenium.chrome-location}")
	private String chromeLocation;

	@Value("${selenium.firefox-driver-location}")
	private String firefoxDriverLocation;

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
	 * If the key is not recognized, returns the default web driver.
	 *
	 * @param key
	 * @return
	 */
	public WebDriver selectWebDriver(final String key) {
		switch (key) {
			case WebBrowserConstants.CHROMIUM: {
				return this.buildChromeWebDriver();
			}
			case WebBrowserConstants.FIREFOX: {
				return this.buildFirefoxDriver();
			}
			default: {
				log.info("Unknown Web Browser: " + key + "; Using Default");
				return this.getDefault();
			}
		}
	}

	private WebDriver getDefault() {
		return this.buildFirefoxDriver();
	}

	private WebDriver buildFirefoxDriver() {
		// Need to tell Selenium where the chromedriver file is.
		System.setProperty("webdriver.gecko.driver", this.firefoxDriverLocation);

		final FirefoxOptions options = new FirefoxOptions();
		options.setHeadless(true);
		// options.setBinary(this.firefoxLocation);

		return new LoggingWebDriver(new FirefoxDriver(options));
	}

	private WebDriver buildChromeWebDriver() {
		// Need to tell Selenium where the chromedriver file is.
		System.setProperty("webdriver.chrome.driver", this.chromeDriverLocation);

		final ChromeOptions options = new ChromeOptions();
		options.setHeadless(true);
		options.setBinary(this.chromeLocation);

		return new LoggingWebDriver(new ChromeDriver(options));
	}

}
