package ca.goudie.advisorinformationscraping.controllers;

import ca.goudie.advisorinformationscraping.exceptions.ScrapeException;
import ca.goudie.advisorinformationscraping.exceptions.SearchException;
import ca.goudie.advisorinformationscraping.models.common.Firm;
import ca.goudie.advisorinformationscraping.models.common.ScrapeResult;
import ca.goudie.advisorinformationscraping.services.dihelpers.SearchServiceSelector;
import ca.goudie.advisorinformationscraping.services.dihelpers.WebDriverSelector;
import ca.goudie.advisorinformationscraping.services.scrapers.ScraperFacade;
import ca.goudie.advisorinformationscraping.services.search.SearchService;
import ca.goudie.advisorinformationscraping.utils.AisCountryUtils;
import org.apache.commons.lang3.StringUtils;
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
			throws SearchException {
		final WebDriver webDriver = this.webDriverSelector.selectWebDriver();
		final SearchService	searcher =
				this.searchServiceSelector.selectSearchService();

		/*
		final String firm = "Abaco Asset Management LLP";
		final String city = "London";
		final String country = "UK";
		final String firm = "Prosser Knowles Associates LTD";
		final String city = "Hartlebury";
		final String country = "UK";
		*/
		final String firm = "Wren Sterling Financial Planning LTD";
		final String city = "";
		final String country = "";

		String query = firm;

		if (StringUtils.isNotBlank(city)) {
			if (StringUtils.isNotBlank(country)) {
				query += " " + city + ", " + country;
			} else {
				query += " " + city;
			}
		} else if (StringUtils.isNotBlank(country)) {
			query += " " + country;
		}

		final String countryCode = AisCountryUtils.findCountryCode(country);

		int resultsLimit = 1;

		final Collection<String> links = searcher.search(webDriver,
				query,
				resultsLimit);
		final ScrapeResult out = new ScrapeResult();
		final Collection<Firm> results = new ArrayList<>();

		for (final String link : links) {
			try {
				results.add(this.scraper.scrapeWebsite(webDriver, link, countryCode));
			} catch (ScrapeException e) {
				e.printStackTrace();
			}
		}

		out.getFirms().addAll(results);

		return out;
	}

}
