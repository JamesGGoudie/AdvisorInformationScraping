package ca.goudie.advisorinformationscraping.entities.ids;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class EmployeeAddressId implements Serializable {

	private static final long serialVersionUID = 2L;

	@Column(name = SqlConstants.EMPLOYEE_ID_COLUMN)
	private long employeeId;

	@Column(name = SqlConstants.EMPLOYEE_ADDRESS_COLUMN)
	private String address;

}
