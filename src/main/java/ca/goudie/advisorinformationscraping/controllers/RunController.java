package ca.goudie.advisorinformationscraping.controllers;

import ca.goudie.advisorinformationscraping.models.ScrapeResult;
import ca.goudie.advisorinformationscraping.services.dihelpers.SearchServiceSelector;
import ca.goudie.advisorinformationscraping.services.dihelpers.WebDriverSelector;
import ca.goudie.advisorinformationscraping.services.scrapers.ScraperFacade;
import ca.goudie.advisorinformationscraping.services.search.SearchService;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RunController {

	@Autowired
	private ScraperFacade scraper;

	@Autowired
	private SearchServiceSelector searchServiceSelector;

	@Autowired
	private WebDriverSelector webDriverSelector;

	@PostMapping("/run")
	public List<ScrapeResult> run() {
		final SearchService searcher =
				this.searchServiceSelector.selectSearchService();
		final WebDriver webDriver = this.webDriverSelector.selectWebDriver();

		String query = "Abaco Asset Management LLP london, UK";
		int resultsLimit = 3;

		final List<String> links = searcher.search(webDriver, query, resultsLimit);
		final List<ScrapeResult> results = new ArrayList<>();

		for (final String link : links) {
			results.add(this.scraper.scrapeWebsite(webDriver, link));
		}

		return results;
	}

}
