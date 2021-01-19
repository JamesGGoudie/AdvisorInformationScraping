package ca.goudie.advisorinformationscraping.services.searchers;

import ca.goudie.advisorinformationscraping.exceptions.SearchException;
import org.openqa.selenium.WebDriver;

import java.util.Collection;

public interface ISearchService {

	Collection<String> search(
			final WebDriver driver,
			final String query,
			final int resultsLimit,
			final Collection<String> blacklist
	) throws SearchException;

}
