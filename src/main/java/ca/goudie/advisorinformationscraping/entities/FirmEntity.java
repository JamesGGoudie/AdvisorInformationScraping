package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.dto.FirmResult;

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
						SqlConstants.FIRM_SEMARCHY_ID_COLUMN,
						SqlConstants.FIRM_SOURCE_COLUMN
				})
})
public class FirmEntity {

	@Column(name = SqlConstants.FIRM_ID_COLUMN)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	@Column(
			name = SqlConstants.FIRM_SEMARCHY_ID_COLUMN,
			nullable = false)
	private String semarchyId;

	@Column(
			name = SqlConstants.FIRM_SOURCE_COLUMN,
			nullable = false)
	private String source;

	@Column(name = SqlConstants.FIRM_URL_COLUMN)
	private String url;

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

	public void addAddresses(final Collection<FirmAddress> addresses) {
		this.addresses.addAll(addresses);

		for (final FirmAddress address : addresses) {
			address.setFirm(this);
		}
	}

	public void addEmails(final Collection<FirmEmail> emails) {
		this.emails.addAll(emails);

		for (final FirmEmail address : emails) {
			address.setFirm(this);
		}
	}

	public void addPhones(final Collection<FirmPhone> phones) {
		this.phones.addAll(phones);

		for (final FirmPhone address : phones) {
			address.setFirm(this);
		}
	}

	public void addEmployees(final Collection<EmployeeEntity> employees) {
		this.employees.addAll(employees);

		for (final EmployeeEntity address : employees) {
			address.setFirm(this);
		}
	}

	public FirmResult toDto() {
		final FirmResult firm = new FirmResult();

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
