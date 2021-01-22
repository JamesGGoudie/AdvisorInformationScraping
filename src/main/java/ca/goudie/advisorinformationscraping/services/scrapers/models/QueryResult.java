package ca.goudie.advisorinformationscraping.services.scrapers.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A collection of firm results for a single firm.
 */
@Data
public class QueryResult {

	private final Collection<FirmResult> firms = new ArrayList<>();

}
