package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.dto.FirmDto;
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

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(
		name = SqlConstants.FIRM_TABLE,
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {
						SqlConstants.QUERY_SEMARCHY_ID_COLUMN,
						SqlConstants.FIRM_SOURCE_COLUMN
				})
})
public class FirmEntity {

	public static final String QUERY_FIELD = "query";

	@Column(name = SqlConstants.FIRM_ID_COLUMN)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	@Column(
			insertable = false,
			name = SqlConstants.QUERY_SEMARCHY_ID_COLUMN,
			nullable = false,
			updatable = false)
	private String semarchyId;

	@Column(
			name = SqlConstants.FIRM_SOURCE_COLUMN,
			nullable = false)
	private String source;

	@Column(name = SqlConstants.FIRM_URL_COLUMN)
	private String url;

	@JoinColumn(
			name = SqlConstants.QUERY_SEMARCHY_ID_COLUMN,
			nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private QueryEntity query;

	@OneToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = EmployeeEntity.FIRM_FIELD,
			orphanRemoval = true)
	@EqualsAndHashCode.Exclude
	private final Collection<EmployeeEntity> employees = new HashSet<>();

	@OneToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = FirmAddress.FIRM_FIELD,
			orphanRemoval = true)
	@EqualsAndHashCode.Exclude
	private final Collection<FirmAddress> addresses = new HashSet<>();

	@OneToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = FirmEmail.FIRM_FIELD,
			orphanRemoval = true)
	@EqualsAndHashCode.Exclude
	private final Collection<FirmEmail> emails = new HashSet<>();

	@OneToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = FirmPhone.FIRM_FIELD,
			orphanRemoval = true)
	@EqualsAndHashCode.Exclude
	private final Collection<FirmPhone> phones = new HashSet<>();

	public void updateAddresses(final Collection<String> foundValues) {
		final Collection<String> knownValues = new HashSet<>();

		for (final FirmAddress storedValue : this.addresses) {
			// If we did not find the stored value this scrape...
			if (!foundValues.contains(storedValue.getId().getAddress())) {
				// Chuck it
				this.addresses.remove(storedValue);
			} else {
				// Otherwise, mark that the stored value is known
				knownValues.add(storedValue.getId().getAddress());
			}
		}

		for (final String foundValue : foundValues) {
			// If the value found from the scrape hasn't been found yet...
			if (!knownValues.contains(foundValue)) {
				// Create a new tuple
				this.addresses.add(FirmAddress.builder()
						.id(FirmAddressId.builder()
								.address(foundValue)
								.firmId(this.id)
								.build())
						.firm(this)
						.build());
			}
		}
	}

	public void updateEmails(final Collection<String> foundValues) {
		final Collection<String> knownValues = new HashSet<>();

		for (final FirmEmail storedValue : this.emails) {
			// If we did not find the stored value this scrape...
			if (!foundValues.contains(storedValue.getId().getEmail())) {
				// Chuck it
				this.emails.remove(storedValue);
			} else {
				// Otherwise, mark that the stored value is known
				knownValues.add(storedValue.getId().getEmail());
			}
		}

		for (final String foundValue : foundValues) {
			// If the value found from the scrape hasn't been found yet...
			if (!knownValues.contains(foundValue)) {
				// Create a new tuple
				this.emails.add(FirmEmail.builder()
						.id(FirmEmailId.builder()
								.email(foundValue)
								.firmId(this.id)
								.build())
						.firm(this)
						.build());
			}
		}
	}

	public void updatePhones(final Collection<String> foundValues) {
		final Collection<String> knownValues = new HashSet<>();

		for (final FirmPhone storedValue : this.phones) {
			// If we did not find the stored value this scrape...
			if (!foundValues.contains(storedValue.getId().getPhone())) {
				// Chuck it
				this.phones.remove(storedValue);
			} else {
				// Otherwise, mark that the stored value is known
				knownValues.add(storedValue.getId().getPhone());
			}
		}

		for (final String foundValue : foundValues) {
			// If the value found from the scrape hasn't been found yet...
			if (!knownValues.contains(foundValue)) {
				// Create a new tuple
				this.phones.add(FirmPhone.builder()
						.id(FirmPhoneId.builder()
								.phone(foundValue)
								.firmId(this.id)
								.build())
						.firm(this)
						.build());
			}
		}
	}

	public void addEmployees(final Collection<EmployeeResult> foundValues) {
		final Collection<String> knownKeys = new HashSet<>();

		for (final EmployeeEntity storedValue : this.employees) {
			boolean match = false;
			EmployeeResult corresponding = null;

			for (final EmployeeResult foundValue : foundValues) {
				if (foundValue.getName().equals(storedValue.getName())) {
					match = true;
					corresponding = foundValue;

					break;
				}
			}

			// If the employee already exists in the firm...
			if (match) {
				// Track the name of the employee.
				knownKeys.add(storedValue.getName());
				// Update the employees information.
				storedValue.updateAddresses(corresponding.getAddresses());
				storedValue.updateEmails(corresponding.getEmails());
				storedValue.updatePhones(corresponding.getPhones());
			} else {
				// Track that the employee did not appear in the most recent scrape
				storedValue.setIsCurrent(false);
			}
		}

		for (final EmployeeResult foundValue : foundValues) {
			// If the value found from the scrape hasn't been found yet...
			if (!knownKeys.contains(foundValue.getName())) {
				// Create a new tuple
				final EmployeeEntity newValue = new EmployeeEntity();
				newValue.setName(foundValue.getName());
				newValue.setSource(foundValue.getSource());
				newValue.setTitle(foundValue.getTitle());
				newValue.setFirm(this);
				newValue.setIsCurrent(true);

				newValue.updateAddresses(foundValue.getAddresses());
				newValue.updateEmails(foundValue.getEmails());
				newValue.updatePhones(foundValue.getPhones());

				this.employees.add(newValue);
			}
		}
	}

	public FirmDto toDto() {
		final FirmDto firm = new FirmDto();

		firm.setInternalFirmId(this.id);
		firm.setSemarchyId(this.semarchyId);

		firm.setSource(this.source);
		firm.setFirmUrl(this.url);

		for (final FirmAddress address : this.addresses) {
			firm.getAddresses().add(address.getId().getAddress());
		}

		for (final FirmEmail email : this.emails) {
			firm.getEmails().add(email.getId().getEmail());
		}

		for (final FirmPhone phone : this.phones) {
			firm.getPhones().add(phone.getId().getPhone());
		}

		for (final EmployeeEntity employee : this.employees) {
			firm.getEmployees().add(employee.toDto());
		}

		return firm;
	}

}
