package ca.goudie.advisorinformationscraping.models.common;

import lombok.Data;

@Data
public class IndividualResult {

	private String firmId;
	private String name;
	private String title;
	private String phone;
	private Float phoneScore;
	private String email;
	private Float emailScore;
	private String address;
	private Float addressScore;
	private String source;

}
