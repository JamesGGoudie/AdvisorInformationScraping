package ca.goudie.advisorinformationscraping.repositories;

import ca.goudie.advisorinformationscraping.entities.FirmEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmRepository extends JpaRepository<FirmEntity, Long> {

	Long findIdBySemarchyIdAndFirmSource(
			final String semarchyId,
			final String firmSource);

}
