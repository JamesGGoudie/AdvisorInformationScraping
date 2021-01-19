package ca.goudie.advisorinformationscraping.controllers;

import ca.goudie.advisorinformationscraping.services.BlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/blacklist")
public class BlacklistController {

	@Autowired
	private BlacklistService blacklistService;

	@PostMapping
	public Collection<String> addToBlacklist(
			@RequestBody
			final Collection<String> hosts
	) {
		return this.blacklistService.addToBlacklist(hosts);
	}

	@GetMapping
	public Collection<String> getBlacklist() {
		return this.blacklistService.getBlacklist();
	}

	@DeleteMapping
	public Collection<String> removeFromBlacklist(
			@RequestBody
			final Collection<String> hosts
	) {
		return this.blacklistService.removeFromBlacklist(hosts);
	}

}
