package ca.goudie.advisorinformationscraping.entities.ids;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class FirmEmailId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = SqlConstants.FIRM_ID_COLUMN)
	private String firmId;

	@Column(name = SqlConstants.FIRM_SOURCE_COLUMN)
	private String firmSource;

	@Column(name = SqlConstants.FIRM_EMAIL_COLUMN)
	private String email;

}
