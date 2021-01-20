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
@Table(name = SqlConstants.EMPLOYEE_ADDRESS_TABLE)
public class EmployeeAddress {

	public static final String EMPLOYEE_FIELD = "employee";

	@EmbeddedId
	private EmployeeAddressId id;

	@Column(name = SqlConstants.EMPLOYEE_ADDRESS_SCORE_COLUMN)
	private Float score;

	@ManyToOne
	@JoinColumn(
			name = SqlConstants.EMPLOYEE_ID_COLUMN,
			insertable = false,
			updatable = false)
	private EmployeeEntity employee;

}
