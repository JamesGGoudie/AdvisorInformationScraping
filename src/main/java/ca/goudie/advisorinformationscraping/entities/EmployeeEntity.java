package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;

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

	public void addAddresses(final Collection<EmployeeAddress> addresses) {
		this.addresses.addAll(addresses);

		for (final EmployeeAddress address : addresses) {
			address.setEmployee(this);
		}
	}

	public void addEmails(final Collection<EmployeeEmail> emails) {
		this.emails.addAll(emails);

		for (final EmployeeEmail address : emails) {
			address.setEmployee(this);
		}
	}

	public void addPhones(final Collection<EmployeePhone> phones) {
		this.phones.addAll(phones);

		for (final EmployeePhone address : phones) {
			address.setEmployee(this);
		}
	}

}
