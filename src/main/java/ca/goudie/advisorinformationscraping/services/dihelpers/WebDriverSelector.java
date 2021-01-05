package ca.goudie.advisorinformationscraping.services.dihelpers;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class WebDriverSelector {

	@Autowired
	@Qualifier("chromeWebDriver")
	private WebDriver chromeWebDriver;

	public WebDriver selectWebDriver() {
		return this.chromeWebDriver;
	}

	public WebDriver selectWebDriver(final String key) {
		return this.chromeWebDriver;
	}

}
