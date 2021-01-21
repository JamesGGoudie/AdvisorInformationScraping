package ca.goudie.advisorinformationscraping.repositories;

import ca.goudie.advisorinformationscraping.entities.EmployeeAddress;
import ca.goudie.advisorinformationscraping.entities.ids.EmployeeAddressId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeAddressRepository
		extends JpaRepository<EmployeeAddress, EmployeeAddressId> {}
