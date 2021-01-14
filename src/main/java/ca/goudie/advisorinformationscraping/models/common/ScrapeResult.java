package ca.goudie.advisorinformationscraping.models.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A collection of firm results for a single firm.
 */
@Data
public class ScrapeResult {

	private String firmId;
	private final Collection<FirmResult> firmResults = new ArrayList<>();

}
