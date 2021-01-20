package ca.goudie.advisorinformationscraping.repositories;

import ca.goudie.advisorinformationscraping.entities.FirmEmail;
import ca.goudie.advisorinformationscraping.entities.ids.FirmEmailId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmEmailRepository
		extends JpaRepository<FirmEmail, FirmEmailId> {

	void deleteByIdFirmId(Long firmId);

}
