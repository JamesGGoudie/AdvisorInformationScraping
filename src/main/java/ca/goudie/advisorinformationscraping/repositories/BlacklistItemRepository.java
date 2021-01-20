package ca.goudie.advisorinformationscraping.repositories;

import ca.goudie.advisorinformationscraping.entities.BlacklistItem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistItemRepository
		extends JpaRepository<BlacklistItem, String> {}
