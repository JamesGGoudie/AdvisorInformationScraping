package ca.goudie.advisorinformationscraping.services;

import ca.goudie.advisorinformationscraping.entities.BlacklistItem;
import ca.goudie.advisorinformationscraping.repositories.BlacklistItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class BlacklistService {

	@Autowired
	private BlacklistItemRepository blacklistRepo;

	public Collection<String> addToBlacklist(
			final Collection<String> hosts
	) {
		this.blacklistRepo.saveAll(this.parseStringsToEntities(hosts));

		return this.getBlacklist();
	}

	public Collection<String> getBlacklist() {
		return this.parseEntitiesToStrings(
				this.blacklistRepo.findAll(Sort.by("host")));
	}

	public Collection<String> removeFromBlacklist(
			final Collection<String> hosts
	) {
		this.blacklistRepo.deleteAll(this.parseStringsToEntities(hosts));

		return this.getBlacklist();
	}

	private Collection<BlacklistItem> parseStringsToEntities(
			final Collection<String> hosts
	) {
		final Collection<BlacklistItem> parsed = new ArrayList<>();

		for (final String host : hosts) {
			final BlacklistItem item = new BlacklistItem();
			item.setHost(host);
			parsed.add(item);
		}

		return parsed;
	}

	private Collection<String> parseEntitiesToStrings(
			final Collection<BlacklistItem> blacklistItems
	) {
		final Collection<String> parsed = new ArrayList<>();

		for (final BlacklistItem blacklistItem : blacklistItems) {
			parsed.add(blacklistItem.getHost());
		}

		return parsed;
	}

}
