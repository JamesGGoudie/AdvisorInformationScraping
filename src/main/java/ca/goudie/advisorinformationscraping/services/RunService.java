package ca.goudie.advisorinformationscraping.services;

import ca.goudie.advisorinformationscraping.dto.FirmResult;
import ca.goudie.advisorinformationscraping.dto.IFirmInfo;
import ca.goudie.advisorinformationscraping.dto.ScrapeResult;
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
import java.util.concurrent.Future;

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

	public void run(
			final Collection<IFirmInfo> allFirmInfo
	) {
		final WebDriver webDriver = this.webDriverSelector.selectWebDriver();
		final ISearcher searcher = this.searcherSelector.selectSearcher();
		final Collection<String> blacklist = this.blacklistService.getBlacklist();

		final int resultsLimit = 5;

		final Collection<ScrapeResult> out = new ArrayList<>();

		for (final IFirmInfo firmInfo : allFirmInfo) {
			if (Thread.currentThread().isInterrupted()) {
				log.info("====================");
				log.info("====================");
				log.info("====================");
				log.info("====================");
				log.info("Thread Interrupted - B");
				log.info("====================");
				log.info("====================");
				log.info("====================");
				log.info("====================");
				break;
			} else {
				log.info("====================");
				log.info(firmInfo.getName());
				log.info("====================");
			}

			try {
				out.add(
						this.processQuery(
								firmInfo, webDriver, searcher, blacklist, resultsLimit));
			} catch (SearchException e) {
				continue;
			}
		}

		// return new AsyncResult<>(null);
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


		log.info("====================");
		log.info("====================");
		log.info("====================");
		log.info("====================");
		log.info(links.size());
		log.info("====================");
		log.info("====================");
		log.info("====================");
		log.info("====================");

		final Collection<FirmResult> firms = new ArrayList<>();

		int i = 0;

		for (final String link : links) {
			log.info("--------------------");
			log.info(link);
			log.info("--------------------");

			System.out.println("A - " + i);
			if (Thread.currentThread().isInterrupted()) {
				log.info("====================");
				log.info("====================");
				log.info("====================");
				log.info("====================");
				log.info("Thread Interrupted - A");
				log.info("====================");
				log.info("====================");
				log.info("====================");
				log.info("====================");
				break;
			} else {
				log.info("====================");
				log.info(link);
				log.info("====================");
			}

			System.out.println("B - " + i);

			final IScraper scraper = this.scraperSelector.selectScraper(link);
			final FirmResult firm;

			System.out.println("C - " + i);

			try {
				firm = scraper.scrapeWebsite(webDriver, link, countryCode);
				System.out.println("D.1 - " + i);

			} catch (ScrapeException e) {
				System.out.println("D.2 - " + i);

				continue;
			}

			System.out.println("E - " + i);

			firms.add(firm);
			this.storageService.storeFirmResult(firm, info.getSemarchyId());

			System.out.println("F - " + i);

			++i;
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
