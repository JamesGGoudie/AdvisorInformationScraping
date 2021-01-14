package ca.goudie.advisorinformationscraping.utils;

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class AisPhoneUtils {

	/**
	 * Returns the first phone number found in the given text.
	 *
	 * If no phone numbers are found, returns null.
	 *
	 * @param text
	 * @return
	 */
	public static String findFirstPhoneNumber(final String text) {
		Iterable<PhoneNumberMatch> numbers =
				PhoneNumberUtil.getInstance().findNumbers(text,null);

		for (final PhoneNumberMatch number : numbers) {
			return number.rawString();
		}

		return null;
	}

}
