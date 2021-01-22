package ca.goudie.advisorinformationscraping.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@Data
public class FirmDto {

	private String semarchyId;
	private Long internalFirmId;

	private String firmUrl;
	private final Collection<String> phones = new HashSet<>();
	private final Collection<String> emails = new HashSet<>();
	private final Collection<String> addresses = new HashSet<>();
	private String source;

	private final Collection<EmployeeDto> employees = new ArrayList<>();

}
