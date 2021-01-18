package ca.goudie.advisorinformationscraping.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AisCountryUtils {

	/**
	 * A map between names of countries and their shortened country codes.
	 *
	 * Used to determine phone number regions.
	 */
	private static final Map<String, String> COUNTRIES = new HashMap<>();

	static {
		// Create an initial set of countries based on Java Locales.
		for (final String country : Locale.getISOCountries()) {
			final Locale l = new Locale("", country);
			AisCountryUtils.COUNTRIES.put(l.getDisplayCountry(), l.getCountry());
		}

		// Manual country declarations.
		// Use this article to find country codes:
		// https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
		AisCountryUtils.COUNTRIES.put("UK", "GB");
		AisCountryUtils.COUNTRIES.put("Great Britain", "GB");
		AisCountryUtils.COUNTRIES.put("US", "US");
		AisCountryUtils.COUNTRIES.put("USA", "US");
		AisCountryUtils.COUNTRIES.put("United States of America", "US");
	}

	public static String findCountryCode(final String countryName) {
		return AisCountryUtils.COUNTRIES.get(countryName);
	}

}
