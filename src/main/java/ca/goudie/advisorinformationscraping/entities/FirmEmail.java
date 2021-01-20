package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.ids.FirmEmailId;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Entity(name = SqlConstants.FIRM_EMAIL_TABLE)
public class FirmEmail {

	public static final String FIRM_FIELD = "firm";

	@EmbeddedId
	private FirmEmailId id;

	@JoinColumn(
			name = SqlConstants.FIRM_ID_COLUMN,
			insertable = false,
			updatable = false)
	@ManyToOne
	private FirmEntity firm;

}
