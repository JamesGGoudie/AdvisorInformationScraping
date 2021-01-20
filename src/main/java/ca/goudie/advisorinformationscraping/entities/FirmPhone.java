package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.ids.FirmPhoneId;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

@Data
@Entity(name = SqlConstants.FIRM_PHONE_TABLE)
public class FirmPhone {

	public static final String FIRM_FIELD = "firm";

	@EmbeddedId
	private FirmPhoneId id;

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

}
