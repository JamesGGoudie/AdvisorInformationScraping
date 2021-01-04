package ca.goudie.advisorinformationscraping.controllers;

import ca.goudie.advisorinformationscraping.services.search.GoogleSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RunController {

	@Autowired
	private GoogleSearchService googleSearchService;

	@PostMapping("/run")
	public String run() {
		return this.googleSearchService.search();
	}

}
