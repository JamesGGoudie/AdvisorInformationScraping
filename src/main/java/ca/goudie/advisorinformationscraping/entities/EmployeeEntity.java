package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.dto.EmployeeDto;
import ca.goudie.advisorinformationscraping.entities.ids.EmployeeAddressId;
import ca.goudie.advisorinformationscraping.entities.ids.EmployeeEmailId;
import ca.goudie.advisorinformationscraping.entities.ids.EmployeePhoneId;
import ca.goudie.advisorinformationscraping.entities.ids.FirmAddressId;
import ca.goudie.advisorinformationscraping.entities.ids.FirmEmailId;
import ca.goudie.advisorinformationscraping.entities.ids.FirmPhoneId;
import ca.goudie.advisorinformationscraping.services.scrapers.models.EmployeeResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(
		name = SqlConstants.EMPLOYEE_TABLE,
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {
						SqlConstants.FIRM_ID_COLUMN,
						SqlConstants.EMPLOYEE_NAME_COLUMN
				})
})
public class EmployeeEntity {

	public static final String FIRM_FIELD = "firm";

	@Column(name = SqlConstants.EMPLOYEE_ID_COLUMN)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	@Column(
			insertable = false,
			name = SqlConstants.FIRM_ID_COLUMN,
			nullable = false,
			updatable = false)
	private Long firmId;

	@Column(
			name = SqlConstants.EMPLOYEE_NAME_COLUMN,
			nullable = false)
	private String name;

	@Column(name = SqlConstants.EMPLOYEE_TITLE_COLUMN)
	private String title;

	@Column(name = SqlConstants.EMPLOYEE_SOURCE_COLUMN)
	private String source;

	@Column(name = SqlConstants.EMPLOYEE_IS_CURRENT_COLUMN)
	private Boolean isCurrent;

	@JoinColumn(
			name = SqlConstants.FIRM_ID_COLUMN,
			nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private FirmEntity firm;

	@OneToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = EmployeeAddress.EMPLOYEE_FIELD,
			orphanRemoval = true)
	@EqualsAndHashCode.Exclude
	private final Collection<EmployeeAddress> addresses = new HashSet<>();

	@OneToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = EmployeeEmail.EMPLOYEE_FIELD,
			orphanRemoval = true)
	@EqualsAndHashCode.Exclude
	private final Collection<EmployeeEmail> emails = new HashSet<>();

	@OneToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = EmployeePhone.EMPLOYEE_FIELD,
			orphanRemoval = true)
	@EqualsAndHashCode.Exclude
	private final Collection<EmployeePhone> phones = new HashSet<>();

	public void updateAddresses(final Map<String, Float> foundValues) {
		final Collection<String> knownKeys = new HashSet<>();

		for (final EmployeeAddress storedValue : this.addresses) {
			final String key = storedValue.getId().getAddress();

			// If we did not find the stored value this scrape...
			if (!foundValues.containsKey(key)) {
				// Chuck it
				this.addresses.remove(storedValue);
			} else {
				// Otherwise, mark that the stored value is known
				knownKeys.add(key);
				// Update the score
				storedValue.setScore(foundValues.get(key));
			}
		}

		for (final String foundKey : foundValues.keySet()) {
			final Float foundValue = foundValues.get(foundKey);
			// If the value found from the scrape hasn't been found yet...
			if (!knownKeys.contains(foundKey)) {
				// Create a new tuple
				this.addresses.add(EmployeeAddress.builder()
						.id(EmployeeAddressId.builder()
								.address(foundKey)
								.employeeId(this.id)
								.build())
						.score(foundValue)
						.employee(this)
						.build());
			}
		}
	}

	public void updateEmails(final Map<String, Float> foundValues) {
		final Collection<String> knownKeys = new HashSet<>();

		for (final EmployeeEmail storedValue : this.emails) {
			final String key = storedValue.getId().getEmail();

			// If we did not find the stored value this scrape...
			if (!foundValues.containsKey(key)) {
				// Chuck it
				this.emails.remove(storedValue);
			} else {
				// Otherwise, mark that the stored value is known
				knownKeys.add(key);
				// Update the score
				storedValue.setScore(foundValues.get(key));
			}
		}

		for (final String foundKey : foundValues.keySet()) {
			final Float foundValue = foundValues.get(foundKey);
			// If the value found from the scrape hasn't been found yet...
			if (!knownKeys.contains(foundKey)) {
				// Create a new tuple
				this.emails.add(EmployeeEmail.builder()
						.id(EmployeeEmailId.builder()
								.email(foundKey)
								.employeeId(this.id)
								.build())
						.score(foundValue)
						.employee(this)
						.build());
			}
		}
	}

	public void updatePhones(final Map<String, Float> foundValues) {
		final Collection<String> knownKeys = new HashSet<>();

		for (final EmployeePhone storedValue : this.phones) {
			final String key = storedValue.getId().getPhone();

			// If we did not find the stored value this scrape...
			if (!foundValues.containsKey(key)) {
				// Chuck it
				this.phones.remove(storedValue);
			} else {
				// Otherwise, mark that the stored value is known
				knownKeys.add(key);
				// Update the score
				storedValue.setScore(foundValues.get(key));
			}
		}

		for (final String foundKey : foundValues.keySet()) {
			final Float foundValue = foundValues.get(foundKey);
			// If the value found from the scrape hasn't been found yet...
			if (!knownKeys.contains(foundKey)) {
				// Create a new tuple
				this.phones.add(EmployeePhone.builder()
						.id(EmployeePhoneId.builder()
								.phone(foundKey)
								.employeeId(this.id)
								.build())
						.score(foundValue)
						.employee(this)
						.build());
			}
		}
	}

	public EmployeeDto toDto() {
		final EmployeeDto employee = new EmployeeDto();

		employee.setInternalEmployeeId(this.id);
		employee.setInternalFirmId(this.firmId);

		employee.setName(this.name);
		employee.setSource(this.source);
		employee.setTitle(this.title);

		for (final EmployeeAddress address : this.addresses) {
			employee.getAddresses().put(
					address.getId().getAddress(),
					address.getScore());
		}

		for (final EmployeeEmail email : this.emails) {
			employee.getEmails().put(
					email.getId().getEmail(),
					email.getScore());
		}

		for (final EmployeePhone phone : this.phones) {
			employee.getPhones().put(
					phone.getId().getPhone(),
					phone.getScore());
		}

		return employee;
	}

}
