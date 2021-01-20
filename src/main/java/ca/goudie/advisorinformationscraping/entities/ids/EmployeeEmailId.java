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
public class EmployeeEmailId implements Serializable {

	private static final Long serialVersionUID = 1L;

	private Long employeeId;
	private String email;

}
