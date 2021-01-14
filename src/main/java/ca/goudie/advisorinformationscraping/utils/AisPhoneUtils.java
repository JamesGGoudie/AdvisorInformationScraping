package ca.goudie.advisorinformationscraping.utils;

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.Collection;
import java.util.HashSet;

public class AisPhoneUtils {

	/**
	 * Returns the first phone number found in the given text.
	 *
	 * If no phone numbers are found, returns null.
	 *
	 * @param text
	 * @return
	 */
	public static String findFirstPhone(final String text) {
		final Iterable<PhoneNumberMatch> numbers =
				PhoneNumberUtil.getInstance().findNumbers(text, null);

		for (final PhoneNumberMatch number : numbers) {
			return number.rawString();
		}

		return null;
	}

	/**
	 * Returns a unique collection of all phone numbers found in the text.
	 *
	 * If none are found, an empty collection is returned.
	 *
	 * @param text
	 * @return
	 */
	public static Collection<String> findPhones(final String text) {
		final Iterable<PhoneNumberMatch> numbers =
				PhoneNumberUtil.getInstance().findNumbers(text, null);
		final Collection<String> out = new HashSet<>();

		for (final PhoneNumberMatch number : numbers) {
			out.add(number.rawString());
		}

		return out;
	}

}
