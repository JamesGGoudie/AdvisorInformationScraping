package ca.goudie.advisorinformationscraping.services;

import lombok.Data;

import org.springframework.stereotype.Service;

@Data
@Service
public class ThreadService {

	private Boolean isRunning = false;
	private Boolean isAllowedToRun = true;

}
