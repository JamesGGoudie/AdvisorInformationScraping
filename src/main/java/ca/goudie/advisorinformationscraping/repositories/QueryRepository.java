package ca.goudie.advisorinformationscraping.repositories;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.QueryEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface QueryRepository extends JpaRepository<QueryEntity, String> {

	@Query(
			value = "SELECT " + SqlConstants.QUERY_SEMARCHY_ID_COLUMN +
					" FROM " + SqlConstants.QUERY_TABLE,
			nativeQuery = true)
	Collection<String> findSemarchyIds();

}
