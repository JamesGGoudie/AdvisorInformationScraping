package ca.goudie.advisorinformationscraping.controllers;

import ca.goudie.advisorinformationscraping.services.dihelpers.SearchServiceSelector;
import ca.goudie.advisorinformationscraping.services.dihelpers.WebDriverSelector;
import ca.goudie.advisorinformationscraping.services.search.GoogleSearchService;
import ca.goudie.advisorinformationscraping.services.search.SearchService;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RunController {

	@Autowired
	private SearchServiceSelector searchServiceSelector;

	@Autowired
	private WebDriverSelector webDriverSelector;

	@PostMapping("/run")
	public List<String> run() {
		SearchService searcher = this.searchServiceSelector.selectSearchService();
		WebDriver webDriver = this.webDriverSelector.selectWebDriver();

		return searcher.search(webDriver, "test", 20);
	}

}
