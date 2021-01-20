package ca.goudie.advisorinformationscraping.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * A collection of data about a firm collection from a website.
 */
@Data
public class FirmResult {

	private String firmUrl;
	private final Collection<String> phones = new HashSet<>();
	private final Collection<String> emails = new HashSet<>();
	private final Collection<String> addresses = new HashSet<>();
	private String source;

	private final Collection<EmployeeResult> employees = new ArrayList<>();

}
