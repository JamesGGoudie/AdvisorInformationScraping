package ca.goudie.advisorinformationscraping.utils.json;

import ca.goudie.advisorinformationscraping.dto.specialized.bloomberg.IBloombergOrganization;
import ca.goudie.advisorinformationscraping.exceptions.ScrapeException;
import ca.goudie.advisorinformationscraping.utils.json.specialized.bloomberg.BloombergOrganization;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class AisJsonUtils {

	/**
	 * Converts the given JSON string to a class that represents the organization
	 * data provided by Bloomberg.
	 *
	 * If the JSON is not of the expected format, returns null.
	 *
	 * @param jsonStr
	 * @return
	 * @throws ScrapeException
	 */
	public static IBloombergOrganization parseBloombergJson(
			final String jsonStr
	) throws ScrapeException {
		final ObjectMapper om = new ObjectMapper();

		try {
			if (!AisJsonUtils.isBloombergOrganizationJson(jsonStr)) {
				return null;
			}

			BloombergOrganization org = om.readValue(jsonStr,
					BloombergOrganization.class);

			return org;
		} catch (JsonProcessingException e) {
			throw new ScrapeException(e);
		}
	}

	/**
	 * Returns true if the given JSON string can likely be parsed into the
	 * Bloomberg organization object.
	 *
	 * @param jsonStr
	 * @return
	 * @throws ScrapeException
	 */
	public static boolean isBloombergOrganizationJson(final String jsonStr)
			throws ScrapeException {
		final ObjectMapper om = new ObjectMapper();

		try {
			Map<String, Object> tempMap = om.readValue(jsonStr, Map.class);

			return tempMap.get("@type").equals("Organization");
		} catch (JsonProcessingException e) {
			throw new ScrapeException(e);
		}
	}

}
