package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.ids.FirmAddressId;
import ca.goudie.advisorinformationscraping.entities.ids.FirmEmailId;

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
@IdClass(FirmEmailId.class)
@Table(name = SqlConstants.FIRM_EMAIL_TABLE)
public class FirmEmail {

	public static final String FIRM_FIELD = "firm";

	@Column(
			name = SqlConstants.FIRM_ID_COLUMN,
			insertable = false,
			updatable = false)
	@Id
	private long firmId;

	@Column(name = SqlConstants.FIRM_EMAIL_COLUMN)
	@Id
	private String email;

	@JoinColumn(name = SqlConstants.FIRM_ID_COLUMN)
	@ManyToOne(fetch = FetchType.LAZY)
	private FirmEntity firm;

}
