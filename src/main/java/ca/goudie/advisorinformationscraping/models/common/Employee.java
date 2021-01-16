package ca.goudie.advisorinformationscraping.models.common;

import lombok.Data;

import java.util.Collection;
import java.util.HashSet;

/**
 * A collection of data about an employee from a website.
 */
@Data
public class Employee {

	private String name;
	private String title;
	private final Collection<String> phones = new HashSet<>();
	private Float phoneScore;
	private final Collection<String> emails = new HashSet<>();
	private Float emailScore;
	private final Collection<String> addresses = new HashSet<>();
	private Float addressScore;
	private String source;

}
