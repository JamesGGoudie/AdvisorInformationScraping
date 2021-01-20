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
		// Check if the DB already has an entry for this firm-source combo.
		final Optional<Long> internalFirmIdOpt =
				this.firmRepo.findIdBySemarchyIdAndFirmSource(
						semarchyId, firm.getSource());
		final Long internalFirmId;

		// If the firm is already present...
		if (internalFirmIdOpt.isPresent()) {
			internalFirmId = internalFirmIdOpt.get();
			// Set all of the employees to out-of-date
			this.employeeRepo.updateIsCurrent(internalFirmId);

			// Delete all of the firm data.
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
		// Will be null if firm doesn't already exist.
		firmEntity.setId(internalFirmId);
		firmEntity.setSemarchyId(semarchyId);
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

		// If the firm already exists...
		if (internalFirmId != null) {
			// Check if the employee exists as well.
			final Optional<Long> internalEmployeeIdOpt =
					this.employeeRepo.findIdByFirmIdAndName(
							internalFirmId, employee.getName());

			// If it does...
			if (internalEmployeeIdOpt.isPresent()) {
				final Long internalEmployeeId = internalEmployeeIdOpt.get();

				// Set the ID of the employee so we don't make a new entry.
				employeeEntity.setId(internalEmployeeId);

				// Delete the employee's data.
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
