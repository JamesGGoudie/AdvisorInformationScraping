package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.ids.EmployeeId;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Collection;

@Entity(name = SqlConstants.EMPLOYEE_TABLE)
@Data
public class EmployeeEntity {

	public static final String FIRM_FIELD = "firm";

	@EmbeddedId
	private EmployeeId id;

	@Column(name = SqlConstants.EMPLOYEE_TITLE_COLUMN)
	private String title;

	@Column(name = SqlConstants.EMPLOYEE_SOURCE_COLUMN)
	private String source;

	@ManyToOne
	@JoinColumns({
			@JoinColumn(
					name = SqlConstants.FIRM_ID_COLUMN,
					insertable = false,
					updatable = false),
			@JoinColumn(
					name = SqlConstants.FIRM_SOURCE_COLUMN,
					insertable = false,
					updatable = false)
	})
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
