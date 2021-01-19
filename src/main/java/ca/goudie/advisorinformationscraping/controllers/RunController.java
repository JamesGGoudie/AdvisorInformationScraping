package ca.goudie.advisorinformationscraping.controllers;

import ca.goudie.advisorinformationscraping.dto.ScrapeResult;
import ca.goudie.advisorinformationscraping.exceptions.SearchException;
import ca.goudie.advisorinformationscraping.services.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RunController {

	@Autowired
	private RunService runService;

	@PostMapping("/run")
	public ScrapeResult run() throws SearchException {
		return this.runService.run();
	}

}
