package ca.goudie.advisorinformationscraping.utils.json.specialized.bloomberg;

import ca.goudie.advisorinformationscraping.dto.specialized.bloomberg.IBloombergEmployee;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BloombergEmployee implements IBloombergEmployee {

	@JsonProperty("@type")
	private String type;

	@JsonProperty
	private String name;

	@JsonProperty
	private String title;

	@JsonProperty
	private String worksFor;

}
