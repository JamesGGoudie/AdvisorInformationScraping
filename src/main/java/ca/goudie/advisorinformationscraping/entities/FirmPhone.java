package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.ids.FirmPhoneId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Entity
@Table(name = SqlConstants.FIRM_PHONE_TABLE)
public class FirmPhone {

	public static final String FIRM_FIELD = "firm";

	@EmbeddedId
	private FirmPhoneId id;

	@JoinColumn(
			name = SqlConstants.FIRM_ID_COLUMN,
			nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId(FirmPhoneId.FIRM_ID_FIELD)
	private FirmEntity firm;

}
