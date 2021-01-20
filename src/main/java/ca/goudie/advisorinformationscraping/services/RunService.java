package ca.goudie.advisorinformationscraping.services;

import ca.goudie.advisorinformationscraping.dto.EmployeeResult;
import ca.goudie.advisorinformationscraping.dto.FirmResult;
import ca.goudie.advisorinformationscraping.dto.IFirmInfo;
import ca.goudie.advisorinformationscraping.dto.ScrapeResult;
import ca.goudie.advisorinformationscraping.entities.EmployeeAddress;
import ca.goudie.advisorinformationscraping.entities.EmployeeEmail;
import ca.goudie.advisorinformationscraping.entities.EmployeeEntity;
import ca.goudie.advisorinformationscraping.entities.EmployeePhone;
import ca.goudie.advisorinformationscraping.entities.FirmAddress;
import ca.goudie.advisorinformationscraping.entities.FirmEmail;
import ca.goudie.advisorinformationscraping.entities.FirmEntity;
import ca.goudie.advisorinformationscraping.entities.FirmPhone;
import ca.goudie.advisorinformationscraping.entities.ids.EmployeeAddressId;
import ca.goudie.advisorinformationscraping.entities.ids.EmployeeEmailId;
import ca.goudie.advisorinformationscraping.entities.ids.EmployeePhoneId;
import ca.goudie.advisorinformationscraping.entities.ids.FirmAddressId;
import ca.goudie.advisorinformationscraping.entities.ids.FirmEmailId;
import ca.goudie.advisorinformationscraping.entities.ids.FirmPhoneId;
import ca.goudie.advisorinformationscraping.exceptions.ScrapeException;
import ca.goudie.advisorinformationscraping.exceptions.SearchException;
import ca.goudie.advisorinformationscraping.repositories.EmployeeAddressRepository;
import ca.goudie.advisorinformationscraping.repositories.EmployeeEmailRepository;
import ca.goudie.advisorinformationscraping.repositories.EmployeePhoneRepository;
import ca.goudie.advisorinformationscraping.repositories.EmployeeRepository;
import ca.goudie.advisorinformationscraping.repositories.FirmAddressRepository;
import ca.goudie.advisorinformationscraping.repositories.FirmEmailRepository;
import ca.goudie.advisorinformationscraping.repositories.FirmPhoneRepository;
import ca.goudie.advisorinformationscraping.repositories.FirmRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
public class RunService {

	@Autowired
	private EmployeeRepository employeeRepo;

	@Autowired
	private EmployeeAddressRepository employeeAddressRepository;

	@Autowired
	private EmployeeEmailRepository employeeEmailRepository;

	@Autowired
	private EmployeePhoneRepository employeePhoneRepository;

	@Autowired
	private FirmRepository firmRepo;

	@Autowired
	private FirmAddressRepository firmAddressRepository;

	@Autowired
	private FirmEmailRepository firmEmailRepository;

	@Autowired
	private FirmPhoneRepository firmPhoneRepository;

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
			final FirmResult firm;

			try {
				firm = scraper.scrapeWebsite(webDriver, link, countryCode);
			} catch (ScrapeException e) {
				continue;
			}

			firms.add(firm);
			this.saveFirmResult(firm, info.getId());
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

	private void saveFirmResult(final FirmResult firm, final String firmId) {
		this.firmRepo.save(this.buildFirmEntity(firm, firmId));
	}

	private FirmEntity buildFirmEntity(
			final FirmResult firm,
			final String firmId) {
		// Check if the DB already has an entry for this firm-source combo.
		final Optional<Long> internalFirmIdOpt =
				this.firmRepo.findIdBySemarchyIdAndFirmSource(firmId, firm.getSource());
		final Long internalFirmId;

		if (internalFirmIdOpt.isPresent()) {
			internalFirmId = internalFirmIdOpt.get();
			// If it does, set all employees for this combo to out-of-date.
			this.employeeRepo.updateIsCurrent(internalFirmId);

			this.firmAddressRepository.deleteByIdFirmId(internalFirmId);
			this.firmEmailRepository.deleteByIdFirmId(internalFirmId);
			this.firmPhoneRepository.deleteByIdFirmId(internalFirmId);
		} else {
			internalFirmId = null;
		}

		final Collection<FirmAddress> firmAddresses = new ArrayList<>();
		final Collection<FirmEmail> firmEmails = new ArrayList<>();
		final Collection<FirmPhone> firmPhones = new ArrayList<>();
		final Collection<EmployeeEntity> employees = new ArrayList<>();

		for (final String value : firm.getAddresses()) {
			firmAddresses.add(
					FirmAddress.builder()
							.id(FirmAddressId.builder().address(value).build())
							.build());
		}

		for (final String value : firm.getEmails()) {
			firmEmails.add(
					FirmEmail.builder()
							.id(FirmEmailId.builder().email(value).build())
							.build());
		}

		for (final String value : firm.getPhones()) {
			firmPhones.add(
					FirmPhone.builder()
							.id(FirmPhoneId.builder().phone(value).build())
							.build());
		}

		for (final EmployeeResult employee : firm.getEmployees()) {
			employees.add(this.buildEmployeeEntity(employee, internalFirmId));
		}

		final FirmEntity firmEntity = new FirmEntity();
		firmEntity.setId(internalFirmId);
		firmEntity.setSemarchyId(firmId);
		firmEntity.setFirmSource(firm.getSource());
		firmEntity.setUrl(firm.getFirmUrl());

		firmEntity.addAddresses(firmAddresses);
		firmEntity.addEmails(firmEmails);
		firmEntity.addPhones(firmPhones);
		firmEntity.addEmployees(employees);

		return firmEntity;
	}

	private EmployeeEntity buildEmployeeEntity(
			final EmployeeResult employee,
			final Long internalFirmId
	) {
		final EmployeeEntity employeeEntity = new EmployeeEntity();

		if (internalFirmId != null) {
			final Optional<Long> internalEmployeeIdOpt =
					this.employeeRepo.findIdByFirmIdAndName(
							internalFirmId, employee.getName());

			if (internalEmployeeIdOpt.isPresent()) {
				final Long internalEmployeeId = internalEmployeeIdOpt.get();

				employeeEntity.setId(internalEmployeeId);

				this.employeeAddressRepository.deleteByIdEmployeeId(internalEmployeeId);
				this.employeeEmailRepository.deleteByIdEmployeeId(internalEmployeeId);
				this.employeePhoneRepository.deleteByIdEmployeeId(internalEmployeeId);
			}
		}

		final Collection<EmployeeAddress> addresses = new ArrayList<>();
		final Collection<EmployeeEmail> emails = new ArrayList<>();
		final Collection<EmployeePhone> phones = new ArrayList<>();

		for (final String key : employee.getAddresses().keySet()) {
			addresses.add(
					EmployeeAddress.builder()
							.id(EmployeeAddressId.builder().address(key).build())
							.score(employee.getAddresses().get(key))
							.build());
		}

		for (final String key : employee.getEmails().keySet()) {
			emails.add(
					EmployeeEmail.builder()
							.id(EmployeeEmailId.builder().email(key).build())
							.score(employee.getEmails().get(key))
							.build());
		}

		for (final String key : employee.getPhones().keySet()) {
			phones.add(
					EmployeePhone.builder()
							.id(EmployeePhoneId.builder().phone(key).build())
							.score(employee.getPhones().get(key))
							.build());
		}

		employeeEntity.setIsCurrent(true);
		employeeEntity.setName(employee.getName());
		employeeEntity.setSource(employee.getSource());
		employeeEntity.setTitle(employee.getTitle());

		employeeEntity.addAddresses(addresses);
		employeeEntity.addEmails(emails);
		employeeEntity.addPhones(phones);

		return employeeEntity;
	}

}
