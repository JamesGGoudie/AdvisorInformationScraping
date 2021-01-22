package ca.goudie.advisorinformationscraping.dto;

import lombok.Data;

import java.util.Collection;

@Data
public class QueryDto {

	final String semarchyId;
	final String name;
	final String city;
	final String region;
	final Boolean isUsa;

	final Collection<FirmDto> results;

}
