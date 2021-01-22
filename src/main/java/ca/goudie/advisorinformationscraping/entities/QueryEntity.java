package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.dto.QueryDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashSet;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = SqlConstants.QUERY_TABLE)
public class QueryEntity {

	@Column(name = SqlConstants.QUERY_SEMARCHY_ID_COLUMN)
	@Id
	private String semarchyId;

	@Column(name = SqlConstants.QUERY_NAME_COLUMN, nullable = false)
	private String name;

	@Column(name = SqlConstants.QUERY_CITY_COLUMN)
	private String city;

	@Column(name = SqlConstants.QUERY_REGION_COLUMN)
	private String region;

	@Column(name = SqlConstants.QUERY_IS_USA_COLUMN)
	private Boolean isUsa;

	@OneToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			mappedBy = FirmEntity.QUERY_FIELD,
			orphanRemoval = true)
	@EqualsAndHashCode.Exclude
	private final Collection<FirmEntity> results = new HashSet<>();

	public void addResults(final Collection<FirmEntity> results) {
		for (final FirmEntity result : results) {
			this.addResult(result);
		}
	}

	public void addResult(final FirmEntity result) {
		this.results.add(result);
		result.setQuery(this);
	}

	public QueryDto toDto() {
		final QueryDto query = new QueryDto();

		query.setSemarchyId(this.semarchyId);
		query.setName(this.name);
		query.setCity(this.city);
		query.setRegion(this.region);
		query.setIsUsa(this.isUsa);

		for (final FirmEntity result : this.results) {
			query.getResults().add(result.toDto());
		}

		return query;
	}

}
