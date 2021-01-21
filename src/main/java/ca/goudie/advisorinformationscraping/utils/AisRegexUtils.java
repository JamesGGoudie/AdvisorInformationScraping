package ca.goudie.advisorinformationscraping.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AisRegexUtils {

	/**
	 * Comprehensive regex for identifying email addresses shamelessly stolen from
	 * from the Chromium repository.
	 */
	private static final String EMAIL_REGEX =
			"(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\"" +
					".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|" +
					"(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))";

	/**
	 * Matches individual segments of a path.
	 */
	private static final String URL_PATH_REGEX = "([^/].*?)/";
	/**
	 * Matches name-like strings.
	 *
	 * Assumes that names do not contain numbers or special symbols besides the
	 * hyphen.
	 */
	private static final String NAME_REGEX = "^[A-Za-z- ]+$";

	private static final Pattern EMAIL_PATTERN =
			Pattern.compile(AisRegexUtils.EMAIL_REGEX);
	private static final Pattern URL_PATH_PATTERN =
			Pattern.compile(AisRegexUtils.URL_PATH_REGEX);
	private static final Pattern NAME_PATTERN =
			Pattern.compile(AisRegexUtils.NAME_REGEX);

	/**
	 * Returns the first email found in the given text.
	 *
	 * If no emails are found, returns null.
	 *
	 * @param text
	 * @return
	 */
	public static String findFirstEmail(final String text) {
		final Matcher matcher = AisRegexUtils.EMAIL_PATTERN.matcher(text);

		if (matcher.find()) {
			return matcher.group();
		}

		return null;
	}

	public static List<String> findPathSegments(final String path) {
		final Matcher matcher = AisRegexUtils.URL_PATH_PATTERN.matcher(path);

		final List<String> out = new ArrayList<>();

		while (matcher.find()) {
			out.add(matcher.group(1));
		}

		return out;
	}

	public static boolean isPossiblyName(final String pathSegment) {
		final Matcher matcher = AisRegexUtils.NAME_PATTERN.matcher(pathSegment);

		return matcher.find();
	}

}
