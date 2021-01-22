package ca.goudie.advisorinformationscraping.entities;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.services.scrapers.models.FirmResult;
import ca.goudie.advisorinformationscraping.services.scrapers.models.QueryResult;

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
		this.results.addAll(results);

		for (final FirmEntity result : results) {
			result.setQuery(this);
		}
	}

	public QueryResult toDto() {
		final QueryResult query = new QueryResult();

		for (final FirmEntity result : this.results) {
			query.getFirms().add(result.toDto());
		}

		return query;
	}

}
