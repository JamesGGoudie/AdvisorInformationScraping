package ca.goudie.advisorinformationscraping.models.common;

import lombok.Data;

import java.util.List;

@Data
public class ScrapeResult {

	private FirmResult firm;
	private List<IndividualResult> individuals;

}
