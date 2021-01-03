package ca.goudie.advisorinformationscraping.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RunController {

	@PostMapping("/run")
	public String run() {
		return "running";
	}

}
