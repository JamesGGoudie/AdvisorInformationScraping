package ca.goudie.advisorinformationscraping.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {

	@Bean
	public WebDriver chromeWebDriver() {
		// Need to tell Selenium where the chromedriver file is.
		System.setProperty("webdriver.chrome.driver",
				"C:/Program Files (x86)/Selenium/chromedriver.exe");

		final ChromeOptions options = new ChromeOptions();
		// By setting Chrome to headless, we don't need GPU resources.
		options.setHeadless(true);

		return new ChromeDriver(options);
	}

}
