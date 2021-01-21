package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.ids.EmployeeAddressId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Entity
@Table(name = SqlConstants.EMPLOYEE_ADDRESS_TABLE)
public class EmployeeAddress {

	public static final String EMPLOYEE_FIELD = "employee";

	@EmbeddedId
	private EmployeeAddressId id;

	@Column(name = SqlConstants.EMPLOYEE_ADDRESS_SCORE_COLUMN)
	private Float score;

	@JoinColumn(
			name = SqlConstants.EMPLOYEE_ID_COLUMN,
			nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId(EmployeeAddressId.EMPLOYEE_ID_FIELD)
	private EmployeeEntity employee;

}
