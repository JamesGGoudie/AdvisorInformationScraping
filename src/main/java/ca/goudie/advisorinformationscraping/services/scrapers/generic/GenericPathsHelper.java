package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@Service
public class GenericPathsHelper {

	/**
	 * A list of possible paths that a website could be using for its employees
	 * page.
	 *
	 * An employees page is a page that contains information about relevant
	 * employees within the company.
	 *
	 * These pages may contain anchors for employee's personal pages.
	 */
	private static final Collection<String> EMPLOYEE_PAGE_PATHS = new HashSet<>();

	public GenericPathsHelper() {
		// Populate our static hash-sets here for simplicity.
		Collections.addAll(GenericPathsHelper.EMPLOYEE_PAGE_PATHS,
				"our-team",
				"our-people",
				"contact-us");
	}

	/**
	 * Returns true if the given path segment matches one of the known employee
	 * page paths.
	 *
	 * @param segment
	 * @return
	 */
	boolean isEmployeePageSegment(final String segment) {
		return GenericPathsHelper.EMPLOYEE_PAGE_PATHS.contains(
				segment.toLowerCase());
	}

}
