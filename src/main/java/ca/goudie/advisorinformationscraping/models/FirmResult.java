package ca.goudie.advisorinformationscraping.models;

import lombok.Data;

@Data
public class FirmResult {

	private String firmId;
	private String firmUrl;
	private String phoneNumber;
	private String emailAddress;
	private String address;
	private String source;

}
