package ca.goudie.advisorinformationscraping.utils.json.specialized.bloomberg;

import ca.goudie.advisorinformationscraping.dto.specialized.bloomberg.IBloombergOrganization;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.Collection;

@Data
public class BloombergOrganization implements IBloombergOrganization {

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
	private Collection<BloombergEmployee> employees;

}
