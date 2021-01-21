package ca.goudie.advisorinformationscraping.repositories;

import ca.goudie.advisorinformationscraping.entities.EmployeeEmail;
import ca.goudie.advisorinformationscraping.entities.ids.EmployeeEmailId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeEmailRepository
		extends JpaRepository<EmployeeEmail, EmployeeEmailId> {}
