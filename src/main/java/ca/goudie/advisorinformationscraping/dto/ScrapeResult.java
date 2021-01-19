package ca.goudie.advisorinformationscraping.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A collection of firm results for a single firm.
 */
@Data
public class ScrapeResult {

	private final Collection<Firm> firms = new ArrayList<>();

}
