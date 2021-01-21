package ca.goudie.advisorinformationscraping.services;

import ca.goudie.advisorinformationscraping.constants.ExceptionMessages;
import ca.goudie.advisorinformationscraping.controllers.RunController;
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
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
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

	@Async
	public void run(
			final Collection<IFirmInfo> allFirmInfo
	) throws RunFailureException {
		this.prepareThreadManager();

		WebDriver webDriver = null;

		try {
			webDriver = this.webDriverSelector.selectWebDriver();

			final ISearcher searcher = this.searcherSelector.selectSearcher();
			final Collection<String> blacklist = this.blacklistService.getBlacklist();

			final int resultsLimit = 5;

			final Collection<ScrapeResult> out = new ArrayList<>();

			for (final IFirmInfo firmInfo : allFirmInfo) {
				try {
					if (!this.threadService.getIsAllowedToRun()) {
						throw new RunCancelException(ExceptionMessages.APP_CANCELLED);
					}

					try {
						out.add(this.processQuery(firmInfo,
								webDriver,
								searcher,
								blacklist,
								resultsLimit));
					} catch (SearchException e) {
						continue;
					}
				} catch (RunCancelException e) {
					throw e;
				} catch (Exception e) {
					// Unknown exception
					log.error(e);

					continue;
				}
			}
		} catch (RunCancelException e) {
			log.info("Run Stopped");
		} catch (Exception e) {
			// Unknown Exception
			log.error(e);
		} finally {
			this.threadService.setIsRunning(false);
			this.threadService.setIsAllowedToRun(true);

			if (webDriver != null) {
				webDriver.quit();
			}
		}
	}

	public void cancel() {
		if (this.threadService.getIsRunning()) {
			this.threadService.setIsAllowedToRun(false);
		}
	}

	public boolean isRunning() {
		return this.threadService.getIsRunning();
	}

	private void prepareThreadManager() throws RunFailureException {
		if (this.isRunning()) {
			log.info("Application is Running; Shutting Down Old Run");
			this.cancel();
		}

		int i = 0;

		while (this.isRunning()) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				log.error(e);
			} finally {
				++i;
			}

			if (this.isRunning()) {
				log.info("Waiting for Shutdown: " + i);
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

		final Collection<String> links = searcher.search(webDriver,
					query,
					resultsLimit,
					blacklist);

		final Collection<FirmResult> firms = new ArrayList<>();

		for (final String link : links) {
			try {
				if (!this.threadService.getIsAllowedToRun()) {
					throw new RunCancelException(ExceptionMessages.APP_CANCELLED);
				}

				final IScraper scraper = this.scraperSelector.selectScraper(link);
				final FirmResult firm;

				try {
					firm = scraper.scrapeWebsite(webDriver, link, countryCode);
				} catch (ScrapeException e) {
					continue;
				}

				firms.add(firm);
				this.storageService.storeFirmResult(firm, info.getSemarchyId());
			} catch (RunCancelException e) {
				throw e;
			}	catch (Exception e) {
				// Unknown exception
				log.error(e);

				continue;
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
