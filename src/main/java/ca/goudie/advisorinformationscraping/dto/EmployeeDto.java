package ca.goudie.advisorinformationscraping.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EmployeeDto {

	private Long internalFirmId;
	private Long internalEmployeeId;

	private Boolean isCurrent;

	private String name;
	private String title;
	private final Map<String, Float> phones = new HashMap<>();
	private final Map<String, Float> emails = new HashMap<>();
	private final Map<String, Float> addresses = new HashMap<>();
	private String source;

}
