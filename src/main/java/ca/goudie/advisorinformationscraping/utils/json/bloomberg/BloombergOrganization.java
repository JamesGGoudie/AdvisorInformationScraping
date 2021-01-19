package ca.goudie.advisorinformationscraping.utils.json.bloomberg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BloombergOrganization {

	@JsonProperty("@type")
	private String type;

	@JsonProperty
	private String name;

	@JsonProperty
	private String description;

	@JsonProperty
	private String address;

	@JsonProperty
	private String telephone;

	@JsonProperty
	private String foundingDate;

	@JsonProperty
	private String url;

	@JsonProperty
	private Integer numberOfEmployees;

	@JsonProperty
	private List<BloombergEmployee> employees;

}
