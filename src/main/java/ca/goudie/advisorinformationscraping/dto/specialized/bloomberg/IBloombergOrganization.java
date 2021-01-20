package ca.goudie.advisorinformationscraping.dto.specialized.bloomberg;

import java.util.Collection;

public interface IBloombergOrganization {

	String getName();
	String getAddress();
	String getTelephone();
	String getUrl();
	Collection<? extends IBloombergEmployee> getEmployees();

}
