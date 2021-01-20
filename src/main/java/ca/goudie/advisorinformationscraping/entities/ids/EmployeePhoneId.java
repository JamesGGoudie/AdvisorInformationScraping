package ca.goudie.advisorinformationscraping.entities.ids;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Builder
@Data
@Embeddable
public class EmployeePhoneId implements Serializable {

	private static final Long serialVersionUID = 1L;

	@Column(name = SqlConstants.EMPLOYEE_ID_COLUMN)
	private Long employeeId;

	@Column(name = SqlConstants.EMPLOYEE_PHONE_COLUMN)
	private String phone;

}
