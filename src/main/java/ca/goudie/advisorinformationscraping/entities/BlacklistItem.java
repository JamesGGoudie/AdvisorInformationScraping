package ca.goudie.advisorinformationscraping.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "blacklist_item")
@Data
public class BlacklistItem {

	@Id
	@Column(name = "host")
	private String host;

}
