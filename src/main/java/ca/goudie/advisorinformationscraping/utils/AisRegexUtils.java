package ca.goudie.advisorinformationscraping.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AisRegexUtils {

	/**
	 * Comprehensive regex for identifying email addresses shamelessly stolen from
	 * from the Chromium repository.
	 */
	private static final String EMAIL_REGEX =
			"(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\"" +
					".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|" +
					"(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))";

	/**
	 * Returns the first email found in the given text.
	 *
	 * If no emails are found, returns null.
	 *
	 * @param text
	 * @return
	 */
	public static String findEmail(final String text) {
		Pattern emailPattern = Pattern.compile(AisRegexUtils.EMAIL_REGEX);
		Matcher innerMatcher = emailPattern.matcher(text);

		if (innerMatcher.find()) {
			return innerMatcher.group();
		}

		return null;
	}

}
