package ca.goudie.advisorinformationscraping.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "blacklist_item")
public class BlacklistItem {

	@Column(name = "host")
	@Id
	private String host;

}
