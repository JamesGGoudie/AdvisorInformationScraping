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
@Embeddable
public class FirmAddressId implements Serializable {

	public static final String FIRM_ID_FIELD = "firmId";

	private static final Long serialVersionUID = 1L;

	@Column(name = SqlConstants.FIRM_ID_COLUMN)
	private Long firmId;

	@Column(name = SqlConstants.FIRM_ADDRESS_COLUMN)
	private String address;

}
