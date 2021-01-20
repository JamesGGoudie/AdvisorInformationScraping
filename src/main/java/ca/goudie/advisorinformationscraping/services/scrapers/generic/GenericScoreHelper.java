package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.constants.GenericConstants;
import ca.goudie.advisorinformationscraping.dto.EmployeeResult;
import ca.goudie.advisorinformationscraping.dto.FirmResult;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
public class GenericScoreHelper {

	/**
	 * Generates confidence scores for the email, phone, and address values of the
	 * firm's employees.
	 *
	 * This is based off of the frequency of the values across employees and if
	 * the value is shared by the firm.
	 *
	 * @param firm
	 */
	void calculateScores(final FirmResult firm) {
		log.info("Calculating Firm Scores");

		this.compareFirmsEmployeesToFirm(firm);
		this.compareFirmsEmployeesToEachOther(firm);
	}

	/**
	 * Lowers the score of any values shared by an employee and the firm.
	 *
	 * @param firm
	 */
	private void compareFirmsEmployeesToFirm(final FirmResult firm) {
		for (final EmployeeResult employee : firm.getEmployees()) {
			this.compareValuesToFirm(firm.getAddresses(),
					employee.getAddresses());
			this.compareValuesToFirm(firm.getEmails(),
					employee.getEmails());
			this.compareValuesToFirm(firm.getPhones(),
					employee.getPhones());
		}
	}

	/**
	 * Checks to see if the employee shares a value with the firm.
	 *
	 * If it does, the score will be lowered.
	 *
	 * @param firmKeys
	 * @param employeeScores
	 */
	private void compareValuesToFirm(
			final Collection<String> firmKeys,
			final Map<String, Float> employeeScores
	) {
		for (final String key : employeeScores.keySet()) {
			if (firmKeys.contains(key)) {
				final float penalty =
						GenericConstants.FIRM_PENALTY * employeeScores.get(key);
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
	private void compareFirmsEmployeesToEachOther(final FirmResult firm) {
		final Collection<Map<String, Float>> addressScores = new ArrayList<>();
		final Collection<Map<String, Float>> emailScores = new ArrayList<>();
		final Collection<Map<String, Float>> phoneScores = new ArrayList<>();

		for (final EmployeeResult employee : firm.getEmployees()) {
			addressScores.add(employee.getAddresses());
			emailScores.add(employee.getEmails());
			phoneScores.add(employee.getPhones());
		}

		this.compareValuesAcrossEmployees(addressScores);
		this.compareValuesAcrossEmployees(emailScores);
		this.compareValuesAcrossEmployees(phoneScores);
	}

	/**
	 * Lower the scores of keys based on how common the keys are.
	 *
	 * @param employeeScoresCollection
	 */
	private void compareValuesAcrossEmployees(
			final Collection<Map<String, Float>> employeeScoresCollection
	) {
		final Map<String, Integer> occurrences = new HashMap<>();

		for (final Map<String, Float> employeeScores : employeeScoresCollection) {
			for (final String key : employeeScores.keySet()) {
				occurrences.merge(key, 1, Integer::sum);
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
