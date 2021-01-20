package ca.goudie.advisorinformationscraping.repositories;

import ca.goudie.advisorinformationscraping.entities.FirmAddress;
import ca.goudie.advisorinformationscraping.entities.ids.FirmAddressId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmAddressRepository
		extends JpaRepository<FirmAddress, FirmAddressId> {

	void deleteByIdFirmId(Long firmId);

}
