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
public class EmployeeEmailId implements Serializable {

	private static final long serialVersionUID = 2L;

	@Column(name = SqlConstants.EMPLOYEE_ID_COLUMN)
	private Long employeeId;

	@Column(name = SqlConstants.EMPLOYEE_EMAIL_COLUMN)
	private String email;


}
