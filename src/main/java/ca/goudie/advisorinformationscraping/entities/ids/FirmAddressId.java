package ca.goudie.advisorinformationscraping.entities.ids;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Builder
@Data
@Embeddable
public class FirmAddressId implements Serializable {

	private static final long serialVersionUID = 2L;

	@Column(name = SqlConstants.FIRM_ID_COLUMN)
	private Long firmId;

	@Column(name = SqlConstants.FIRM_ADDRESS_COLUMN)
	private String address;

}
