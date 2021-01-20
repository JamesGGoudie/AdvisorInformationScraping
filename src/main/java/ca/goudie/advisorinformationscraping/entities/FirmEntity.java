package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Collection;

@Data
@Entity(name = SqlConstants.FIRM_TABLE)
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {
				SqlConstants.FIRM_SEMARCHY_ID_COLUMN,
				SqlConstants.FIRM_SOURCE_COLUMN
		})
})
public class FirmEntity {

	@Column(name = SqlConstants.FIRM_ID_COLUMN)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Id
	private long id;

	@Column(name = SqlConstants.FIRM_SEMARCHY_ID_COLUMN)
	private String semarchyId;

	@Column(name = SqlConstants.FIRM_SOURCE_COLUMN)
	private String firmSource;

	@Column(name = SqlConstants.FIRM_URL_COLUMN)
	private String url;

	@OneToMany(
			cascade = CascadeType.ALL,
			mappedBy = EmployeeEntity.FIRM_FIELD,
			orphanRemoval = true)
	private Collection<EmployeeEntity> employees;

	@OneToMany(
			cascade = CascadeType.ALL,
			mappedBy = FirmAddress.FIRM_FIELD,
			orphanRemoval = true)
	private Collection<FirmAddress> addresses;

	@OneToMany(
			cascade = CascadeType.ALL,
			mappedBy = FirmEmail.FIRM_FIELD,
			orphanRemoval = true)
	private Collection<FirmEmail> emails;

	@OneToMany(
			cascade = CascadeType.ALL,
			mappedBy = FirmPhone.FIRM_FIELD,
			orphanRemoval = true)
	private Collection<FirmPhone> phone;

}
