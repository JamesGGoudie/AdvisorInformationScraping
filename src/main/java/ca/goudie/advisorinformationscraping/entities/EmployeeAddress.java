package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.ids.EmployeeAddressId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Entity
@IdClass(EmployeeAddressId.class)
@Table(name = SqlConstants.EMPLOYEE_ADDRESS_TABLE)
public class EmployeeAddress {

	public static final String EMPLOYEE_FIELD = "employee";

	@Column(
			name = SqlConstants.EMPLOYEE_ID_COLUMN,
			insertable = false,
			updatable = false)
	@Id
	private Long employeeId;

	@Column(name = SqlConstants.EMPLOYEE_ADDRESS_COLUMN)
	@Id
	private String address;

	@Column(name = SqlConstants.EMPLOYEE_ADDRESS_SCORE_COLUMN)
	private Float score;

	@JoinColumn(name = SqlConstants.EMPLOYEE_ID_COLUMN)
	@ManyToOne(fetch = FetchType.LAZY)
	private EmployeeEntity employee;

}
