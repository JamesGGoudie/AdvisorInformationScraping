package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Collection;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Entity()
@Table(
		name = SqlConstants.FIRM_TABLE,
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {
						SqlConstants.FIRM_SEMARCHY_ID_COLUMN,
						SqlConstants.FIRM_SOURCE_COLUMN
				})
})
public class FirmEntity {

	@Column(name = SqlConstants.FIRM_ID_COLUMN)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	@Column(name = SqlConstants.FIRM_SEMARCHY_ID_COLUMN)
	private String semarchyId;

	@Column(name = SqlConstants.FIRM_SOURCE_COLUMN)
	private String firmSource;

	@Column(name = SqlConstants.FIRM_URL_COLUMN)
	private String url;

	@OneToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = EmployeeEntity.FIRM_FIELD,
			orphanRemoval = true)
	private Collection<EmployeeEntity> employees;

	@OneToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = FirmAddress.FIRM_FIELD,
			orphanRemoval = true)
	private Collection<FirmAddress> addresses;

	@OneToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = FirmEmail.FIRM_FIELD,
			orphanRemoval = true)
	private Collection<FirmEmail> emails;

	@OneToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = FirmPhone.FIRM_FIELD,
			orphanRemoval = true)
	private Collection<FirmPhone> phone;

}
