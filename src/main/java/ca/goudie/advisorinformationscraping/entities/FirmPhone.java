package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.ids.FirmPhoneId;

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
@Table(name = SqlConstants.FIRM_PHONE_TABLE)
public class FirmPhone {

	public static final String FIRM_FIELD = "firm";

	@EmbeddedId
	private FirmPhoneId id;

	@JoinColumn(
			name = SqlConstants.FIRM_ID_COLUMN,
			insertable = false,
			updatable = false)
	@ManyToOne
	private FirmEntity firm;

}
