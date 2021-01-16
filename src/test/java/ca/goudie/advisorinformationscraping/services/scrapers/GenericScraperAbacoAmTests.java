package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;
import ca.goudie.advisorinformationscraping.models.common.Firm;
import ca.goudie.advisorinformationscraping.models.common.Employee;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class GenericScraperAbacoAmTests {

	@Autowired
	private GenericScraper genericScraper;

	@Autowired
	@Qualifier("chromeWebDriver")
	private WebDriver chromeWebDriver;

	@Test
	public void shouldScrapeAbacoAm() throws ScrapingFailedException {
		final String abacoUrl = "http://www.abacoam.com/";

		WebDriver mockDriver = spy(this.chromeWebDriver);

		final Firm f = this.genericScraper.scrapeWebsite(mockDriver,
				abacoUrl);

		verify(mockDriver).get(abacoUrl);

		assertEquals("www.abacoam.com", f.getFirmUrl());
		assertEquals("www.abacoam.com", f.getSource());

		final Collection<String> ePhones = new HashSet<>();
		ePhones.add("+44 20 3031 9184");

		assertEquals(ePhones, f.getPhones());

		final Collection<String> eEmails = new HashSet<>();
		eEmails.add("Abaco.AssetManagement@abacoam.com");

		assertEquals(eEmails, f.getEmails());

		final Collection<String> aAddresses = f.getAddresses();

		assertNotNull(aAddresses);
		assertEquals(0, aAddresses.size());

		final Collection<Employee> aEmployees = f.getEmployees();

		assertNotNull(aEmployees);
		assertEquals(0, aEmployees.size());
	}

}
