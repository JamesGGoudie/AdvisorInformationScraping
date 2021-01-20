package ca.goudie.advisorinformationscraping.repositories;

import ca.goudie.advisorinformationscraping.entities.EmployeePhone;
import ca.goudie.advisorinformationscraping.entities.ids.EmployeePhoneId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeePhoneRepository
		extends JpaRepository<EmployeePhone, EmployeePhoneId> {

	void deleteByIdEmployeeId(Long employeeId);

}
