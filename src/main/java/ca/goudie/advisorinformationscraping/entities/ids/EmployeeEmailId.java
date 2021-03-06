package ca.goudie.advisorinformationscraping.entities.ids;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Embeddable
public class EmployeeEmailId implements Serializable {

	public static final String EMPLOYEE_ID_FIELD = "employeeId";

	private static final Long serialVersionUID = 1L;

	@Column(name = SqlConstants.EMPLOYEE_ID_COLUMN)
	private Long employeeId;

	@Column(name = SqlConstants.EMPLOYEE_EMAIL_COLUMN)
	private String email;

}
