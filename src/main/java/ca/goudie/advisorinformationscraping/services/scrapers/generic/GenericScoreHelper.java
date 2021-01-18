package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.models.common.Employee;
import ca.goudie.advisorinformationscraping.models.common.Firm;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class GenericScoreHelper {

	private static final float INIT_SCORE = 1.0f;
	private static final float FIRM_PENALTY = 0.5f;

	/**
	 * Generates confidence scores for the email, phone, and address values of the
	 * firm's employees.
	 *
	 * This is based off of the frequency of the values across employees and if
	 * the value is shared by the firm.
	 *
	 * @param firm
	 */
	void calculateScores(final Firm firm) {
		this.initScores(firm);
		this.compareValuesToFirm(firm);
		this.compareValuesAcrossEmployees(firm);
	}

	/**
	 * Initializes all of the scores maps of all of the firm's employees.
	 *
	 * @param firm
	 */
	private void initScores(final Firm firm) {
		for (final Employee employee : firm.getEmployees()) {
			this.initScores(employee.getAddresses(), employee.getAddressScores());
			this.initScores(employee.getEmails(), employee.getEmailScores());
			this.initScores(employee.getPhones(), employee.getPhoneScores());
		}
	}

	/**
	 * Initializes the given scores map.
	 *
	 * @param employeeKeys
	 * @param employeeScores
	 */
	private void initScores(
			final Collection<String> employeeKeys,
			final Map<String, Float> employeeScores
	) {
		for (final String key : employeeKeys) {
			employeeScores.put(key, GenericScoreHelper.INIT_SCORE);
		}
	}

	/**
	 * Lowers the score of any values shared by an employee and the firm.
	 *
	 * @param firm
	 */
	private void compareValuesToFirm(final Firm firm) {
		for (final Employee employee : firm.getEmployees()) {
			this.compareValuesToFirm(firm.getAddresses(),
					employee.getAddresses(),
					employee.getAddressScores());
			this.compareValuesToFirm(firm.getEmails(),
					employee.getEmails(),
					employee.getEmailScores());
			this.compareValuesToFirm(firm.getPhones(),
					employee.getPhones(),
					employee.getPhoneScores());
		}
	}

	/**
	 * Checks to see if the employee shares a value with the firm.
	 *
	 * If it does, the score will be lowered.
	 *
	 * @param firmKeys
	 * @param employeeKeys
	 * @param employeeScores
	 */
	private void compareValuesToFirm(
			final Collection<String> firmKeys,
			final Collection<String> employeeKeys,
			final Map<String, Float> employeeScores
	) {
		for (final String key : employeeKeys) {
			if (firmKeys.contains(key)) {
				final float penalty =
						GenericScoreHelper.FIRM_PENALTY * employeeScores.get(key);
				employeeScores.put(key, penalty);
			}
		}
	}

	/**
	 * Looks at the values across the firm's employees in search of shared values.
	 *
	 * Any shared values will have their scores decreased proportionally to the
	 * amount of times it is shared.
	 *
	 * @param firm
	 */
	private void compareValuesAcrossEmployees(final Firm firm) {
		final Collection<Collection<String>> addressKeys = new ArrayList<>();
		final Collection<Map<String, Float>> addressScores = new ArrayList<>();
		final Collection<Collection<String>> emailKeys = new ArrayList<>();
		final Collection<Map<String, Float>> emailScores = new ArrayList<>();
		final Collection<Collection<String>> phoneKeys = new ArrayList<>();
		final Collection<Map<String, Float>> phoneScores = new ArrayList<>();

		for (final Employee employee : firm.getEmployees()) {
			addressKeys.add(employee.getAddresses());
			addressScores.add(employee.getAddressScores());
			emailKeys.add(employee.getEmails());
			emailScores.add(employee.getEmailScores());
			phoneKeys.add(employee.getPhones());
			phoneScores.add(employee.getPhoneScores());
		}

		this.compareValuesAcrossEmployees(addressKeys, addressScores);
		this.compareValuesAcrossEmployees(emailKeys, emailScores);
		this.compareValuesAcrossEmployees(phoneKeys, phoneScores);
	}

	/**
	 * Lower the scores of keys based on how common the keys are.
	 *
	 * @param employeeKeysCollection
	 * @param employeeScoresCollection
	 */
	private void compareValuesAcrossEmployees(
			final Collection<Collection<String>> employeeKeysCollection,
			final Collection<Map<String, Float>> employeeScoresCollection
	) {
		final Map<String, Integer> occurrences = new HashMap<>();

		for (final Collection<String> employeeKeys : employeeKeysCollection) {
			for (final String key : employeeKeys) {
				if (occurrences.get(key) == null) {
					occurrences.put(key, 1);
				} else {
					occurrences.put(key, occurrences.get(key) + 1);
				}
			}
		}

		for (final Map<String, Float> employeeScores : employeeScoresCollection) {
			for (final String key : employeeScores.keySet()) {
				final float penalty = employeeScores.get(key) / occurrences.get(key);
				employeeScores.put(key, penalty);
			}
		}
	}

}
