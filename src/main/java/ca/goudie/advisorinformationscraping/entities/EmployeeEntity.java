package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Collection;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
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

	@Column(name = SqlConstants.EMPLOYEE_NAME_COLUMN)
	private String name;

	@Column(name = SqlConstants.EMPLOYEE_TITLE_COLUMN)
	private String title;

	@Column(name = SqlConstants.EMPLOYEE_SOURCE_COLUMN)
	private String source;

	@JoinColumn(
			name = SqlConstants.FIRM_ID_COLUMN,
			insertable = false,
			updatable = false)
	@ManyToOne
	private FirmEntity firm;

	@OneToMany(
			cascade = CascadeType.ALL,
			mappedBy = EmployeeAddress.EMPLOYEE_FIELD,
			orphanRemoval = true)
	private Collection<EmployeeAddress> addresses;

	@OneToMany(
			cascade = CascadeType.ALL,
			mappedBy = EmployeeEmail.EMPLOYEE_FIELD,
			orphanRemoval = true)
	private Collection<EmployeeEmail> emails;

	@OneToMany(
			cascade = CascadeType.ALL,
			mappedBy = EmployeePhone.EMPLOYEE_FIELD,
			orphanRemoval = true)
	private Collection<EmployeePhone> phone;

}
