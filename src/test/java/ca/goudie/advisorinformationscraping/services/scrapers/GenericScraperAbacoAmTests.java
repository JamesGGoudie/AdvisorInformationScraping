package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;
import ca.goudie.advisorinformationscraping.models.common.FirmResult;
import ca.goudie.advisorinformationscraping.models.common.IndividualResult;
import ca.goudie.advisorinformationscraping.models.common.ScrapeResult;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

		final ScrapeResult r = this.genericScraper.scrapeWebsite(mockDriver,
				abacoUrl);

		verify(mockDriver).get(abacoUrl);

		final FirmResult f = r.getFirm();

		assertEquals("www.abacoam.com", f.getFirmUrl());
		assertEquals("+44 20 3031 9184", f.getPhoneNumber());
		assertEquals("Abaco.AssetManagement@abacoam.com", f.getEmailAddress());
		assertEquals("www.abacoam.com", f.getSource());

		assertNull(f.getAddress());

		final List<IndividualResult> listI = r.getIndividuals();

		assertNotNull(listI);
		assertEquals(0, listI.size());
	}

}
