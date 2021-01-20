package ca.goudie.advisorinformationscraping.repositories;

import ca.goudie.advisorinformationscraping.entities.FirmPhone;
import ca.goudie.advisorinformationscraping.entities.ids.FirmPhoneId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmPhoneRepository
		extends JpaRepository<FirmPhone, FirmPhoneId> {

	void deleteByIdFirmId(Long firmId);

}
