package ca.goudie.advisorinformationscraping.models.bloomberg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BloombergEmployee {

	@JsonProperty("@type")
	private String type;

	@JsonProperty
	private String name;

	@JsonProperty
	private String title;

}
