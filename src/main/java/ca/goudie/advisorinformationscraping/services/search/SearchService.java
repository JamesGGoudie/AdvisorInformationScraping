package ca.goudie.advisorinformationscraping.services.search;

import org.openqa.selenium.WebDriver;

import java.util.List;

public interface SearchService {

	List<String> search(
			final WebDriver driver, final String query, final int resultsLimit
	);

}
