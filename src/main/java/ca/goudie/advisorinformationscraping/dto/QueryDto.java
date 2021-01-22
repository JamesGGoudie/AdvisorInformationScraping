package ca.goudie.advisorinformationscraping.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class QueryDto {

	private String semarchyId;
	private String name;
	private String city;
	private String region;
	private Boolean isUsa;

	private final Collection<FirmDto> results = new ArrayList<>();

}
