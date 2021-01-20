package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.ids.FirmAddressId;
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
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
@IdClass(FirmPhoneId.class)
@Table(name = SqlConstants.FIRM_PHONE_TABLE)
public class FirmPhone {

	public static final String FIRM_FIELD = "firm";

	@Column(
			name = SqlConstants.FIRM_ID_COLUMN,
			insertable = false,
			updatable = false)
	@Id
	private Long firmId;

	@Column(name = SqlConstants.FIRM_PHONE_COLUMN)
	@Id
	private String phone;

	@JoinColumn(name = SqlConstants.FIRM_ID_COLUMN)
	@ManyToOne(fetch = FetchType.LAZY)
	private FirmEntity firm;

}
