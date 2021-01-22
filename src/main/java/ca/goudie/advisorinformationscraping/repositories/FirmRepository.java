package ca.goudie.advisorinformationscraping.repositories;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.FirmEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FirmRepository extends JpaRepository<FirmEntity, Long> {

	@Query(
			value = "SELECT " + SqlConstants.FIRM_ID_COLUMN +
					" FROM " + SqlConstants.FIRM_TABLE +
					" WHERE " + SqlConstants.QUERY_SEMARCHY_ID_COLUMN + " = :semarchyId" +
					" AND " + SqlConstants.FIRM_SOURCE_COLUMN + " = :firmSource",
			nativeQuery = true)
	Optional<Long> findIdBySemarchyIdAndFirmSource(
			@Param("semarchyId")
			final String semarchyId,
			@Param("firmSource")
			final String firmSource);

}
