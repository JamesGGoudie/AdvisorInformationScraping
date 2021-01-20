package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.exceptions.ScrapeException;
import ca.goudie.advisorinformationscraping.dto.FirmResult;
import ca.goudie.advisorinformationscraping.services.scrapers.generic.GenericScraper;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class GenericScraperAbacoTests {

	@Autowired
	private GenericScraper genericScraper;

	@Autowired
	@Qualifier("chromeWebDriver")
	private WebDriver chromeWebDriver;

	@Test
	public void shouldScrapeAbaco() throws ScrapeException {
		final String landingPageUrl = "http://www.abacoam.com/";
		final String countryCode = "GB";

		WebDriver mockDriver = spy(this.chromeWebDriver);

		final FirmResult aFirm = this.genericScraper.scrapeWebsite(mockDriver,
				landingPageUrl,
				countryCode);

		verify(mockDriver).get(landingPageUrl);

		final FirmResult eFirm = new FirmResult();
		eFirm.setFirmUrl("www.abacoam.com");
		eFirm.setSource("www.abacoam.com");
		eFirm.getPhones().add("+44 20 3031 9184");
		eFirm.getEmails().add("Abaco.AssetManagement@abacoam.com");

		assertEquals(eFirm, aFirm);
	}

}
