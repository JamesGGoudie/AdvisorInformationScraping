package ca.goudie.advisorinformationscraping.repositories;

import ca.goudie.advisorinformationscraping.constants.SqlConstants;
import ca.goudie.advisorinformationscraping.entities.EmployeeEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository
		extends JpaRepository<EmployeeEntity, Long> {

	@Query(
			value = "SELECT " + SqlConstants.EMPLOYEE_ID_COLUMN +
					" FROM " + SqlConstants.EMPLOYEE_TABLE +
					" WHERE " + SqlConstants.FIRM_ID_COLUMN + " = :firmId" +
					" AND " + SqlConstants.EMPLOYEE_NAME_COLUMN + " = :name",
			nativeQuery = true)
	Long findIdByFirmIdAndName(
			@Param("firmId")
			final Long firmId,
			@Param("name")
			final String name);

	@Query(
			value = "UPDATE " + SqlConstants.EMPLOYEE_TABLE +
					" SET " + SqlConstants.EMPLOYEE_IS_CURRENT_COLUMN + " = false" +
					" WHERE " + SqlConstants.FIRM_ID_COLUMN + " = :firmId",
			nativeQuery = true)
	Integer updateIsCurrent(
			@Param("firmId")
			final Long firmId);

}
