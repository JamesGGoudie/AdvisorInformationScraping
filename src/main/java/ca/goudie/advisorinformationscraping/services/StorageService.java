package ca.goudie.advisorinformationscraping.services;

import ca.goudie.advisorinformationscraping.dto.EmployeeResult;
import ca.goudie.advisorinformationscraping.dto.FirmResult;
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
import ca.goudie.advisorinformationscraping.repositories.EmployeeAddressRepository;
import ca.goudie.advisorinformationscraping.repositories.EmployeeEmailRepository;
import ca.goudie.advisorinformationscraping.repositories.EmployeePhoneRepository;
import ca.goudie.advisorinformationscraping.repositories.EmployeeRepository;
import ca.goudie.advisorinformationscraping.repositories.FirmAddressRepository;
import ca.goudie.advisorinformationscraping.repositories.FirmEmailRepository;
import ca.goudie.advisorinformationscraping.repositories.FirmPhoneRepository;
import ca.goudie.advisorinformationscraping.repositories.FirmRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

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

	public void storeFirmResult(
			final FirmResult firm,
			final String semarchyId) {
		this.firmRepo.save(this.buildFirmEntity(firm, semarchyId));
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
			internalFirmId = internalFirmIdOpt.get();
			// Set all of the employees to out-of-date
			this.employeeRepo.updateIsCurrent(internalFirmId);
		} else {
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

		for (final EmployeeResult employee : firm.getEmployees()) {
			employees.add(this.buildEmployeeEntity(employee, internalFirmId));
		}

		firmEntity.setSemarchyId(semarchyId);
		firmEntity.setFirmSource(firm.getSource());
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
				internalEmployeeId = internalEmployeeIdOpt.get();
				final Optional<EmployeeEntity> employeeOpt =
						this.employeeRepo.findById(internalEmployeeId);

				employeeEntity = employeeOpt.orElseGet(EmployeeEntity::new);
			} else {
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
