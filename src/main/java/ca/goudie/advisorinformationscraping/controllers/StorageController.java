package ca.goudie.advisorinformationscraping.controllers;

import ca.goudie.advisorinformationscraping.services.scrapers.models.EmployeeResult;
import ca.goudie.advisorinformationscraping.services.scrapers.models.FirmResult;
import ca.goudie.advisorinformationscraping.services.scrapers.models.QueryResult;
import ca.goudie.advisorinformationscraping.exceptions.ResultMissingException;
import ca.goudie.advisorinformationscraping.services.StorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/storage")
public class StorageController {

	@Autowired
	private StorageService storage;

	@GetMapping("/query-ids")
	public Collection<String> getSemarchyIds() {
		return this.storage.getSemarchyIds();
	}

	@GetMapping("/query/{id}")
	public QueryResult getResultsBySemarchyId(
			@PathVariable("id")
			final String id
	) throws ResultMissingException {
		return this.storage.getResultsBySemarchyId(id);
	}

	@GetMapping("/firm/{id}")
	public FirmResult getFirmById(
			@PathVariable("id")
			final Long id
	) throws ResultMissingException {
		return this.storage.getFirmById(id);
	}

	@GetMapping("/employee/{id}")
	public EmployeeResult getEmployeeById(
			@PathVariable("id")
			final Long id
	) throws ResultMissingException {
		return this.storage.getEmployeeById(id);
	}

}
