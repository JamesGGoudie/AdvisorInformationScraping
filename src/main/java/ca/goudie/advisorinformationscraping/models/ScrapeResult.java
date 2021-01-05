package ca.goudie.advisorinformationscraping.models;

import lombok.Data;

import java.util.List;

@Data
public class ScrapeResult {

	private FirmResult firm;
	private List<IndividualResult> individuals;

}
