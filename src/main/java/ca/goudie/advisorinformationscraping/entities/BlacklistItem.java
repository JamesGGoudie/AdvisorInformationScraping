package ca.goudie.advisorinformationscraping.entities;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@Data
@Entity
@Table(name = "blacklist_item")
public class BlacklistItem {

	@Column(name = "host")
	@Id
	private String host;

}
