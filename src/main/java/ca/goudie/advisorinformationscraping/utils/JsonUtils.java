package ca.goudie.advisorinformationscraping.utils;

import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;
import ca.goudie.advisorinformationscraping.models.bloomberg.BloombergOrganization;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtils {

	/**
	 * Converts the given JSON string to a class that represents the organization
	 * data provided by Bloomberg.
	 *
	 * If the JSON is not of the expected format, returns null.
	 *
	 * @param jsonStr
	 * @return
	 * @throws ScrapingFailedException
	 */
	public static BloombergOrganization parseBloombergJson(
			final String jsonStr
	) throws ScrapingFailedException {
		final ObjectMapper om = new ObjectMapper();

		try {
			if (!JsonUtils.isBloombergOrganizationJson(jsonStr)) {
				return null;
			}

			BloombergOrganization org = om.readValue(jsonStr,
					BloombergOrganization.class);

			return org;
		} catch (JsonProcessingException e) {
			throw new ScrapingFailedException(e);
		}
	}

	/**
	 * Returns true if the given JSON string can likely be parsed into the
	 * Bloomberg organization object.
	 *
	 * @param jsonStr
	 * @return
	 * @throws ScrapingFailedException
	 */
	public static boolean isBloombergOrganizationJson(final String jsonStr)
			throws ScrapingFailedException {
		final ObjectMapper om = new ObjectMapper();

		try {
			Map<String, Object> tempMap = om.readValue(jsonStr, Map.class);

			return tempMap.get("@type").equals("Organization");
		} catch (JsonProcessingException e) {
			throw new ScrapingFailedException(e);
		}
	}

}