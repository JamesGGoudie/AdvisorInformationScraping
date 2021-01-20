package ca.goudie.advisorinformationscraping.services;

import ca.goudie.advisorinformationscraping.dto.FirmResult;
import ca.goudie.advisorinformationscraping.dto.IFirmInfo;
import ca.goudie.advisorinformationscraping.dto.ScrapeResult;
import ca.goudie.advisorinformationscraping.exceptions.ScrapeException;
import ca.goudie.advisorinformationscraping.exceptions.SearchException;
import ca.goudie.advisorinformationscraping.services.scrapers.IScraper;
import ca.goudie.advisorinformationscraping.services.searchers.ISearcher;
import ca.goudie.advisorinformationscraping.services.selectors.ScraperSelector;
import ca.goudie.advisorinformationscraping.services.selectors.SearchServiceSelector;
import ca.goudie.advisorinformationscraping.services.selectors.WebDriverSelector;
import ca.goudie.advisorinformationscraping.utils.AisCountryUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class RunService {

	@Autowired
	private BlacklistService blacklistService;

	@Autowired
	private ScraperSelector scraperSelector;

	@Autowired
	private SearchServiceSelector searchServiceSelector;

	@Autowired
	private WebDriverSelector webDriverSelector;

	public Collection<ScrapeResult> run(
			final Collection<IFirmInfo> allFirmInfo
	) {
		final WebDriver webDriver = this.webDriverSelector.selectWebDriver();
		final ISearcher searcher = this.searchServiceSelector.selectSearcher();
		final Collection<String> blacklist = this.blacklistService.getBlacklist();

		final int resultsLimit = 1;

		final Collection<ScrapeResult> out = new ArrayList<>();

		for (final IFirmInfo firmInfo : allFirmInfo) {
			try {
				out.add(
						this.processQuery(
								firmInfo, webDriver, searcher, blacklist, resultsLimit));
			} catch (SearchException e) {
				continue;
			}
		}

		return out;
	}

	private ScrapeResult processQuery(
			final IFirmInfo info,
			final WebDriver webDriver,
			final ISearcher searcher,
			final Collection<String> blacklist,
			final int resultsLimit
	) throws SearchException {
		final String query = this.buildQuery(info);
		final String countryCode = this.determineCountryCode(info);

		final Collection<String> links = searcher.search(webDriver,
					query,
					resultsLimit,
					blacklist);

		final Collection<FirmResult> firms = new ArrayList<>();

		for (final String link : links) {
			final IScraper scraper = this.scraperSelector.selectScraper(link);

			try {
				firms.add(scraper.scrapeWebsite(webDriver, link, countryCode));
			} catch (ScrapeException e) {
				e.printStackTrace();
			}
		}

		final ScrapeResult out = new ScrapeResult();
		out.getFirms().addAll(firms);

		return out;
	}

	private String buildQuery(final IFirmInfo info) {
		String query = info.getName();

		if (StringUtils.isNotBlank(info.getCity())) {
			if (StringUtils.isNotBlank(info.getRegion())) {
				query += " " + info.getCity() + ", " + info.getRegion();
			} else {
				query += " " + info.getCity();
			}
		} else if (StringUtils.isNotBlank(info.getRegion())) {
			query += " " + info.getRegion();
		}

		return query;
	}

	private String determineCountryCode(final IFirmInfo info) {
		if (BooleanUtils.isTrue(info.getIsUsa())) {
			return AisCountryUtils.findUsaCode();
		}

		return AisCountryUtils.findCountryCode(info.getRegion());
	}

}
