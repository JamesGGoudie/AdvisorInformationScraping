package ca.goudie.advisorinformationscraping.controllers;

import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;
import ca.goudie.advisorinformationscraping.exceptions.SearchingFailedException;
import ca.goudie.advisorinformationscraping.models.common.Firm;
import ca.goudie.advisorinformationscraping.models.common.ScrapeResult;
import ca.goudie.advisorinformationscraping.services.dihelpers.SearchServiceSelector;
import ca.goudie.advisorinformationscraping.services.dihelpers.WebDriverSelector;
import ca.goudie.advisorinformationscraping.services.scrapers.ScraperFacade;
import ca.goudie.advisorinformationscraping.services.search.SearchService;
import ca.goudie.advisorinformationscraping.utils.AisPhoneUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;

@RestController
public class RunController {

	@Autowired
	private ScraperFacade scraper;

	@Autowired
	private SearchServiceSelector searchServiceSelector;

	@Autowired
	private WebDriverSelector webDriverSelector;

	@PostMapping("/run")
	public ScrapeResult run()
			throws SearchingFailedException {
		final WebDriver webDriver = this.webDriverSelector.selectWebDriver();
		final SearchService	searcher =
				this.searchServiceSelector.selectSearchService();

		// String query = "Abaco Asset Management LLP london, UK";
		String query = "Prosser Knowles Associates LTD hartlebury, UK";
		int resultsLimit = 1;

		final Collection<String> links = searcher.search(webDriver,
				query,
				resultsLimit);
		final ScrapeResult out = new ScrapeResult();
		final Collection<Firm> results = new ArrayList<>();

		for (final String link : links) {
			try {
				results.add(this.scraper.scrapeWebsite(webDriver, link));
			} catch (ScrapingFailedException e) {
				e.printStackTrace();
			}
		}

		out.getFirms().addAll(results);

		return out;
	}

}
