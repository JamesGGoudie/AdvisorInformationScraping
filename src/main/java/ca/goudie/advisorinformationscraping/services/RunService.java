package ca.goudie.advisorinformationscraping.services;

import ca.goudie.advisorinformationscraping.constants.ExceptionMessages;
import ca.goudie.advisorinformationscraping.dto.FirmResult;
import ca.goudie.advisorinformationscraping.dto.IFirmInfo;
import ca.goudie.advisorinformationscraping.dto.ScrapeResult;
import ca.goudie.advisorinformationscraping.exceptions.RunCancelException;
import ca.goudie.advisorinformationscraping.exceptions.RunFailureException;
import ca.goudie.advisorinformationscraping.exceptions.ScrapeException;
import ca.goudie.advisorinformationscraping.exceptions.SearchException;
import ca.goudie.advisorinformationscraping.services.scrapers.IScraper;
import ca.goudie.advisorinformationscraping.services.searchers.ISearcher;
import ca.goudie.advisorinformationscraping.services.selectors.ScraperSelector;
import ca.goudie.advisorinformationscraping.services.selectors.SearcherSelector;
import ca.goudie.advisorinformationscraping.services.selectors.WebDriverSelector;
import ca.goudie.advisorinformationscraping.utils.AisCountryUtils;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@Transactional
public class RunService {

	@Autowired
	private BlacklistService blacklistService;

	@Autowired
	private StorageService storageService;

	@Autowired
	private ScraperSelector scraperSelector;

	@Autowired
	private SearcherSelector searcherSelector;

	@Autowired
	private WebDriverSelector webDriverSelector;

	@Autowired
	private ThreadService threadService;

	/**
	 * Searches the internet and scrapes websites for information about the given
	 * firms.
	 *
	 * This method uses async and can be split into separate threads.
	 *
	 * @param givenFirmInfo
	 * @param resultsLimit
	 * @param webBrowserKey
	 * @param searchEngineKey
	 */
	@Async
	public void runThread(
			final Collection<IFirmInfo> givenFirmInfo,
			final Integer resultsLimit,
			final String webBrowserKey,
			final String searchEngineKey
	) {
		WebDriver webDriver = null;

		try {
			webDriver = this.webDriverSelector.selectWebDriver(webBrowserKey);
			final ISearcher searcher =
					this.searcherSelector.selectSearcher(searchEngineKey);
			final Collection<String> blacklist = this.blacklistService.getBlacklist();

			int i = 0;

			for (final IFirmInfo firmInfo : givenFirmInfo) {
				log.info("Processing Firm " + (++i) + " of " + givenFirmInfo.size());

				if (!this.threadService.getIsAllowedToRun()) {
					throw new RunCancelException(ExceptionMessages.APP_CANCELLED);
				}

				this.processQuery(firmInfo,
						webDriver,
						searcher,
						blacklist,
						resultsLimit);
			}
		} catch (RunCancelException e) {
			// This exception was thrown to stop the application.
			log.info("Thread Stopped");
		} catch (SearchException e) {
			// Searching failed; likely that the search engine impl is rubbish.
			log.error(e);
		} catch (Exception e) {
			// Unknown Exception
			log.error(e);
		} finally {
			this.threadService.setIsRunning(false);
			this.threadService.setIsAllowedToRun(true);

			if (webDriver != null) {
				webDriver.quit();
			}

			log.info("Thread Finished");
		}
	}

	/**
	 * If the app is currently running, then this method will shut it down.
	 *
	 * If the app is currently stopped, then this method will ensure that the app
	 * is able to run again.
	 *
	 */
	public void reset() {
		this.threadService.setIsAllowedToRun(!this.threadService.getIsRunning());
	}

	public boolean isRunning() {
		return this.threadService.getIsRunning();
	}

	/**
	 * Stops the app if it is currently running.
	 *
	 * This is done by changing a boolean called "allowed-to-run" that is checked
	 * periodically throughout the scraping process.
	 *
	 * This value will be set back to true once the app has fully shut down,
	 * allowing a new app to run.
	 *
	 * @throws RunFailureException
	 */
	public void prepareThreadManager() throws RunFailureException {
		if (this.isRunning()) {
			log.info("App is Running; Shutting Down Old Run");
			this.reset();
		} else if (!this.threadService.getIsAllowedToRun()) {
			log.info("App is Not Running and is Not Allowed to Run; Fixing");
			this.reset();
		}

		int i = 0;

		while (this.isRunning()) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				log.error(e);
			}

			if (this.isRunning()) {
				log.info("Waiting for Shutdown: " + ++i);
			}

			if (i >= 30) {
				throw new RunFailureException("Shutdown Took too Long");
			}
		}

		this.threadService.setIsRunning(true);
	}

	private ScrapeResult processQuery(
			final IFirmInfo info,
			final WebDriver webDriver,
			final ISearcher searcher,
			final Collection<String> blacklist,
			final int resultsLimit
	) throws SearchException, RunCancelException {
		final String query = this.buildQuery(info);
		final String countryCode = this.determineCountryCode(info);

		log.info("Built Query: '" + query + "'");
		log.info("Using Country Code: '" + countryCode + "'");

		final Collection<String> links = searcher.search(webDriver,
					query,
					resultsLimit,
					blacklist);

		final Collection<FirmResult> firms = new ArrayList<>();

		int i = 0;

		for (final String link : links) {
			log.info("Processing Search Result " + (++i) + " of " + resultsLimit);

			if (!this.threadService.getIsAllowedToRun()) {
				throw new RunCancelException(ExceptionMessages.APP_CANCELLED);
			}

			final IScraper scraper = this.scraperSelector.selectScraper(link);
			final FirmResult firm;

			try {
				firm = scraper.scrapeWebsite(webDriver, link, countryCode);
			} catch (ScrapeException e) {
				// Scraping failed, but may succeed for other links; continue
				log.error(e);

				continue;
			}

			firms.add(firm);
			this.storageService.storeFirmResult(firm, info.getSemarchyId());
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
