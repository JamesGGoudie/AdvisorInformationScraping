package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.ids.FirmId;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Collection;

@Data
@Entity(name = SqlConstants.FIRM_TABLE)
public class FirmEntity {

	@EmbeddedId
	private FirmId id;

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
