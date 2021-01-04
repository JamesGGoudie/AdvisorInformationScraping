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
		System.setProperty("webdriver.chrome.driver",
				"C:/Program Files/Selenium/chromedriver.exe");

		final ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless",
				"--disable-gpu",
				"--window-size=1920,1200",
				"--ignore-certificate-errors",
				"--silent");

		return new ChromeDriver(options);
	}

}
