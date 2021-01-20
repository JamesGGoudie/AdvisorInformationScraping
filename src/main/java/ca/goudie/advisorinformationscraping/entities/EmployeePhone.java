package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.ids.EmployeePhoneId;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Builder
@Data
@Entity
@Table(name = SqlConstants.EMPLOYEE_PHONE_TABLE)
public class EmployeePhone {

	public static final String EMPLOYEE_FIELD = "employee";

	@EmbeddedId
	private EmployeePhoneId id;

	@Column(name = SqlConstants.EMPLOYEE_PHONE_SCORE_COLUMN)
	private Float score;

	@JoinColumn(
			name = SqlConstants.EMPLOYEE_ID_COLUMN,
			nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("employeeId")
	private EmployeeEntity employee;

}
