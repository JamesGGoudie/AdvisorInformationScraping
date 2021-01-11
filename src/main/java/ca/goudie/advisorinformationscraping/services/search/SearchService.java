package ca.goudie.advisorinformationscraping.services.search;

import ca.goudie.advisorinformationscraping.exceptions.SearchingFailedException;
import org.openqa.selenium.WebDriver;

import java.util.Collection;

public interface SearchService {

	Collection<String> search(
			final WebDriver driver, final String query, final int resultsLimit
	) throws SearchingFailedException;

}
