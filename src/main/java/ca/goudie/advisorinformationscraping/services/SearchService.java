package ca.goudie.advisorinformationscraping.services;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

	public String search() {
		System.setProperty("webdriver.chrome.driver",
				"C:/Program Files/Selenium/chromedriver.exe");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless",
				"--disable-gpu",
				"--window-size=1920,1200",
				"--ignore-certificate-errors",
				"--silent");
		WebDriver driver = new ChromeDriver(options);

		driver.get("https://www.google.ca");

		return driver.getPageSource();
	}

}
