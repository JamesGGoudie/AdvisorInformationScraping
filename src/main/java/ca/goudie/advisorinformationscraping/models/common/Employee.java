package ca.goudie.advisorinformationscraping.models.common;

import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * A collection of data about an employee from a website.
 */
@Data
public class Employee {

	private String name;
	private String title;
	private final Collection<String> phones = new HashSet<>();
	private final Map<String, Float> phoneScores = new HashMap<>();
	private final Collection<String> emails = new HashSet<>();
	private final Map<String, Float> emailScores = new HashMap<>();
	private final Collection<String> addresses = new HashSet<>();
	private final Map<String, Float> addressScores = new HashMap<>();
	private String source;

}
