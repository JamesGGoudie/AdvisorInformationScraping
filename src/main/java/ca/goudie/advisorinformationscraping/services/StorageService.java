package ca.goudie.advisorinformationscraping.services;

import ca.goudie.advisorinformationscraping.dto.EmployeeDto;
import ca.goudie.advisorinformationscraping.dto.FirmDto;
import ca.goudie.advisorinformationscraping.dto.IFirmInfo;
import ca.goudie.advisorinformationscraping.dto.QueryDto;
import ca.goudie.advisorinformationscraping.entities.QueryEntity;
import ca.goudie.advisorinformationscraping.repositories.QueryRepository;
import ca.goudie.advisorinformationscraping.services.scrapers.models.EmployeeResult;
import ca.goudie.advisorinformationscraping.services.scrapers.models.FirmResult;
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
import ca.goudie.advisorinformationscraping.exceptions.ResultMissingException;
import ca.goudie.advisorinformationscraping.repositories.EmployeeAddressRepository;
import ca.goudie.advisorinformationscraping.repositories.EmployeeEmailRepository;
import ca.goudie.advisorinformationscraping.repositories.EmployeePhoneRepository;
import ca.goudie.advisorinformationscraping.repositories.EmployeeRepository;
import ca.goudie.advisorinformationscraping.repositories.FirmAddressRepository;
import ca.goudie.advisorinformationscraping.repositories.FirmEmailRepository;
import ca.goudie.advisorinformationscraping.repositories.FirmPhoneRepository;
import ca.goudie.advisorinformationscraping.repositories.FirmRepository;
import ca.goudie.advisorinformationscraping.services.scrapers.models.QueryResult;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Log4j2
@Service
public class StorageService {

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
	private QueryRepository queryRepo;

	public QueryEntity storeResults(final QueryResult result) {
		log.info("Storing Query Results");

		return this.queryRepo.save(this.buildQueryEntity(result));
	}

	public Collection<String> getSemarchyIds() {
		return this.queryRepo.findSemarchyIds();
	}

	public QueryDto getResultsBySemarchyId(final String id)
			throws ResultMissingException {
		final Optional<QueryEntity> query = this.queryRepo.findById(id);

		if (!query.isPresent()) {
			throw new ResultMissingException(
					"Firm Result With Internal ID (" + id + ") Does Not Exist");
		}

		return query.get().toDto();
	}

	public FirmDto getFirmById(final Long id) throws ResultMissingException {
		final Optional<FirmEntity> firm = this.firmRepo.findById(id);

		if (!firm.isPresent()) {
			throw new ResultMissingException(
					"Firm Result With Internal ID (" + id + ") Does Not Exist");
		}

		return firm.get().toDto();
	}

	public EmployeeDto getEmployeeById(final Long id)
			throws ResultMissingException {
		final Optional<EmployeeEntity> employee = this.employeeRepo.findById(id);

		if (!employee.isPresent()) {
			throw new ResultMissingException(
					"Employee Result With Internal ID (" + id + ") Does Not Exist");
		}

		return employee.get().toDto();
	}

	private QueryEntity buildQueryEntity(
			final QueryResult queryResult
	) {
		final IFirmInfo firmInfo = queryResult.getQueryInfo();

		final QueryEntity queryEntity;

		final Optional<QueryEntity> queryOpt =
				this.queryRepo.findById(firmInfo.getSemarchyId());

		if (queryOpt.isPresent()) {
			log.info(
					"Query with ID (" + firmInfo.getSemarchyId() + ") Exists");
			queryEntity = queryOpt.get();
		} else {
			log.info(
					"Query with ID (" + firmInfo.getSemarchyId() + ") does Not Exist");
			queryEntity = new QueryEntity();
		}

		queryEntity.setCity(firmInfo.getCity());
		queryEntity.setName(firmInfo.getName());
		queryEntity.setRegion(firmInfo.getRegion());
		queryEntity.setIsUsa(firmInfo.getIsUsa());
		queryEntity.setSemarchyId(firmInfo.getSemarchyId());

		final Collection<FirmEntity> resultEntities = new ArrayList<>();

		log.info("Query has " + queryResult.getFirms().size() + " Firm Results");

		for (final FirmResult firmResult : queryResult.getFirms()) {
			resultEntities.add(
					this.buildFirmEntity(firmResult, firmInfo.getSemarchyId()));
		}

		queryEntity.addResults(resultEntities);

		return queryEntity;
	}

	private FirmEntity buildFirmEntity(
			final FirmResult firm,
			final String semarchyId) {
		final FirmEntity firmEntity = new FirmEntity();

		// Check if the DB already has an entry for this firm-source combo.
		final Optional<Long> internalFirmIdOpt =
				this.firmRepo.findIdBySemarchyIdAndFirmSource(
						semarchyId, firm.getSource());
		final Long internalFirmId;

		if (internalFirmIdOpt.isPresent()) {
			log.info("Firm with Semarchy ID (" + semarchyId + ")" +
					" and Source (" + firm.getSource() + ") Exists");

			internalFirmId = internalFirmIdOpt.get();
			// Set all of the employees to out-of-date
			log.info("Setting Employees as Non-Current");
			this.employeeRepo.updateIsCurrentToFalse(internalFirmId);
		} else {
			log.info("Firm with Semarchy ID (" + semarchyId + ")" +
					" and Source (" + firm.getSource() + ") does not Exist");

			internalFirmId = null;
		}

		firmEntity.setId(internalFirmId);

		final Collection<FirmAddress> firmAddresses = new ArrayList<>();
		final Collection<FirmEmail> firmEmails = new ArrayList<>();
		final Collection<FirmPhone> firmPhones = new ArrayList<>();
		final Collection<EmployeeEntity> employees = new ArrayList<>();

		for (final String address : firm.getAddresses()) {
			firmAddresses.add(this.buildFirmAddress(address, internalFirmId));
		}

		for (final String email : firm.getEmails()) {
			firmEmails.add(this.buildFirmEmail(email, internalFirmId));
		}

		for (final String phone : firm.getPhones()) {
			firmPhones.add(this.buildFirmPhone(phone, internalFirmId));
		}

		log.info("Firm (" + internalFirmId + ") has " +
				firm.getEmployees().size() + " Employee Results");

		for (final EmployeeResult employee : firm.getEmployees()) {
			employees.add(this.buildEmployeeEntity(employee, internalFirmId));
		}

		firmEntity.setSource(firm.getSource());
		firmEntity.setUrl(firm.getFirmUrl());

		firmEntity.addAddresses(firmAddresses);
		firmEntity.addEmails(firmEmails);
		firmEntity.addPhones(firmPhones);
		firmEntity.addEmployees(employees);

		return firmEntity;
	}

	private FirmAddress buildFirmAddress(
			final String address,
			final Long internalFirmId
	) {
		if (internalFirmId == null) {
			return FirmAddress.builder()
					.id(FirmAddressId.builder()
							.address(address)
							.build())
					.build();
		}

		final FirmAddressId id = FirmAddressId.builder()
				.firmId(internalFirmId)
				.address(address)
				.build();
		final Optional<FirmAddress> opt = this.firmAddressRepository.findById(id);

		return opt.orElseGet(() -> FirmAddress.builder().id(id).build());

	}

	private FirmEmail buildFirmEmail(
			final String email,
			final Long internalFirmId
	) {
		if (internalFirmId == null) {
			return FirmEmail.builder()
					.id(FirmEmailId.builder()
							.email(email)
							.build())
					.build();
		}

		final FirmEmailId id = FirmEmailId.builder()
				.firmId(internalFirmId)
				.email(email)
				.build();
		final Optional<FirmEmail> opt = this.firmEmailRepository.findById(id);

		return opt.orElseGet(() -> FirmEmail.builder().id(id).build());

	}

	private FirmPhone buildFirmPhone(
			final String phone,
			final Long internalFirmId
	) {
		if (internalFirmId == null) {
			return FirmPhone.builder()
					.id(FirmPhoneId.builder()
							.phone(phone)
							.build())
					.build();
		}

		final FirmPhoneId id = FirmPhoneId.builder()
				.firmId(internalFirmId)
				.phone(phone)
				.build();
		final Optional<FirmPhone> opt = this.firmPhoneRepository.findById(id);

		return opt.orElseGet(() -> FirmPhone.builder().id(id).build());

	}

	private EmployeeEntity buildEmployeeEntity(
			final EmployeeResult employee,
			final Long internalFirmId
	) {
		final EmployeeEntity employeeEntity;
		final Long internalEmployeeId;

		// If the firm already exists...
		if (internalFirmId != null) {
			// Check if the employee exists as well.
			final Optional<Long> internalEmployeeIdOpt =
					this.employeeRepo.findIdByFirmIdAndName(
							internalFirmId, employee.getName());

			// If it does...
			if (internalEmployeeIdOpt.isPresent()) {
				log.info("Employee with Internal Firm ID (" + internalFirmId + ")" +
						" and Name (" + employee.getName() + ") Exists");

				internalEmployeeId = internalEmployeeIdOpt.get();
				final Optional<EmployeeEntity> employeeOpt =
						this.employeeRepo.findById(internalEmployeeId);

				employeeEntity = employeeOpt.orElseGet(EmployeeEntity::new);
			} else {
				log.info("Employee with Internal Firm ID (" + internalFirmId + ")" +
						" and Name (" + employee.getName() + ") does not Exist");

				internalEmployeeId = null;
				employeeEntity = new EmployeeEntity();
			}
		} else {
			internalEmployeeId = null;
			employeeEntity = new EmployeeEntity();
		}

		final Collection<EmployeeAddress> addresses = new ArrayList<>();
		final Collection<EmployeeEmail> emails = new ArrayList<>();
		final Collection<EmployeePhone> phones = new ArrayList<>();

		for (final String address : employee.getAddresses().keySet()) {
			addresses.add(
					this.buildEmployeeAddress(
							address,
							internalEmployeeId,
							employee.getAddresses().get(address)));
		}

		for (final String email : employee.getEmails().keySet()) {
			emails.add(
					this.buildEmployeeEmail(
							email,
							internalEmployeeId,
							employee.getEmails().get(email)));
		}

		for (final String phone : employee.getPhones().keySet()) {
			phones.add(
					this.buildEmployeePhone(
							phone,
							internalEmployeeId,
							employee.getPhones().get(phone)));
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

	private EmployeeAddress buildEmployeeAddress(
			final String address,
			final Long internalEmployeeId,
			final Float score
	) {
		if (internalEmployeeId == null) {
			return EmployeeAddress.builder()
					.id(EmployeeAddressId.builder()
							.address(address)
							.build())
					.score(score)
					.build();
		}

		final EmployeeAddressId id = EmployeeAddressId.builder()
				.employeeId(internalEmployeeId)
				.address(address)
				.build();
		final Optional<EmployeeAddress> opt =
				this.employeeAddressRepository.findById(id);

		if (opt.isPresent()) {
			final EmployeeAddress employeeAddress = opt.get();
			employeeAddress.setScore(score);

			return employeeAddress;
		}

		return EmployeeAddress.builder()
				.id(id)
				.score(score)
				.build();
	}

	private EmployeeEmail buildEmployeeEmail(
			final String email,
			final Long internalEmployeeId,
			final Float score
	) {
		if (internalEmployeeId == null) {
			return EmployeeEmail.builder()
					.id(EmployeeEmailId.builder()
							.email(email)
							.build())
					.score(score)
					.build();
		}

		final EmployeeEmailId id = EmployeeEmailId.builder()
				.employeeId(internalEmployeeId)
				.email(email)
				.build();
		final Optional<EmployeeEmail> opt =
				this.employeeEmailRepository.findById(id);

		if (opt.isPresent()) {
			final EmployeeEmail employeeEmail = opt.get();
			employeeEmail.setScore(score);

			return employeeEmail;
		}

		return EmployeeEmail.builder()
				.id(id)
				.score(score)
				.build();
	}

	private EmployeePhone buildEmployeePhone(
			final String phone,
			final Long internalEmployeeId,
			final Float score
	) {
		if (internalEmployeeId == null) {
			return EmployeePhone.builder()
					.id(EmployeePhoneId.builder()
							.phone(phone)
							.build())
					.score(score)
					.build();
		}

		final EmployeePhoneId id = EmployeePhoneId.builder()
				.employeeId(internalEmployeeId)
				.phone(phone)
				.build();
		final Optional<EmployeePhone> opt =
				this.employeePhoneRepository.findById(id);

		if (opt.isPresent()) {
			final EmployeePhone employeePhone = opt.get();
			employeePhone.setScore(score);

			return employeePhone;
		}

		return EmployeePhone.builder()
				.id(id)
				.score(score)
				.build();
	}

}
