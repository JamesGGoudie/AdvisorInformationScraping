package ca.goudie.advisorinformationscraping.services;

import ca.goudie.advisorinformationscraping.dto.EmployeeDto;
import ca.goudie.advisorinformationscraping.dto.FirmDto;
import ca.goudie.advisorinformationscraping.dto.IFirmInfo;
import ca.goudie.advisorinformationscraping.dto.QueryDto;
import ca.goudie.advisorinformationscraping.entities.EmployeeEntity;
import ca.goudie.advisorinformationscraping.entities.FirmEntity;
import ca.goudie.advisorinformationscraping.entities.QueryEntity;
import ca.goudie.advisorinformationscraping.exceptions.ResultMissingException;
import ca.goudie.advisorinformationscraping.repositories.EmployeeRepository;
import ca.goudie.advisorinformationscraping.repositories.FirmRepository;
import ca.goudie.advisorinformationscraping.repositories.QueryRepository;
import ca.goudie.advisorinformationscraping.services.scrapers.models.FirmResult;
import ca.goudie.advisorinformationscraping.services.scrapers.models.QueryResult;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Log4j2
@Service
public class StorageService {

	@Autowired
	private EmployeeRepository employeeRepo;

	@Autowired
	private FirmRepository firmRepo;

	@Autowired
	private QueryRepository queryRepo;

	public QueryEntity storeResults(final QueryResult result) {
		log.info("Storing Query Results");

		return this.queryRepo.save(this.buildQueryEntity(result));
	}

	public Collection<String> getSemarchyIds() {
		return this.queryRepo.findSemarchyIds();
	}

	public QueryDto getResultsBySemarchyId(final String id)
			throws ResultMissingException {
		final Optional<QueryEntity> query = this.queryRepo.findById(id);

		if (!query.isPresent()) {
			throw new ResultMissingException(
					"Firm Result With Internal ID (" + id + ") Does Not Exist");
		}

		return query.get().toDto();
	}

	public FirmDto getFirmById(final Long id) throws ResultMissingException {
		final Optional<FirmEntity> firm = this.firmRepo.findById(id);

		if (!firm.isPresent()) {
			throw new ResultMissingException(
					"Firm Result With Internal ID (" + id + ") Does Not Exist");
		}

		return firm.get().toDto();
	}

	public EmployeeDto getEmployeeById(final Long id)
			throws ResultMissingException {
		final Optional<EmployeeEntity> employee = this.employeeRepo.findById(id);

		if (!employee.isPresent()) {
			throw new ResultMissingException(
					"Employee Result With Internal ID (" + id + ") Does Not Exist");
		}

		return employee.get().toDto();
	}

	private QueryEntity buildQueryEntity(
			final QueryResult queryResult
	) {
		final IFirmInfo firmInfo = queryResult.getQueryInfo();

		final QueryEntity queryEntity;

		final Optional<QueryEntity> queryOpt =
				this.queryRepo.findById(firmInfo.getSemarchyId());

		if (queryOpt.isPresent()) {
			log.info(
					"Query with ID (" + firmInfo.getSemarchyId() + ") Exists");
			queryEntity = queryOpt.get();
		} else {
			log.info(
					"Query with ID (" + firmInfo.getSemarchyId() + ") does Not Exist");
			queryEntity = new QueryEntity();
		}

		queryEntity.setCity(firmInfo.getCity());
		queryEntity.setName(firmInfo.getName());
		queryEntity.setRegion(firmInfo.getRegion());
		queryEntity.setIsUsa(firmInfo.getIsUsa());
		queryEntity.setSemarchyId(firmInfo.getSemarchyId());

		final Collection<FirmEntity> resultEntities = new ArrayList<>();

		log.info("Query has " + queryResult.getFirms().size() + " Firm Results");

		for (final FirmResult firmResult : queryResult.getFirms()) {
			resultEntities.add(
					this.buildFirmEntity(
							queryEntity,
							firmResult));
		}

		queryEntity.addResults(resultEntities);

		return queryEntity;
	}

	private FirmEntity buildFirmEntity(
			final QueryEntity queryEntity,
			final FirmResult firm
	) {
		final String semarchyId = queryEntity.getSemarchyId();
		FirmEntity firmEntity = null;

		boolean found = false;

		for (final FirmEntity result : queryEntity.getResults()) {
			// A blank semarchy ID means that it is a new firm result
			if (StringUtils.isNotBlank(result.getSemarchyId()) &&
					result.getSemarchyId().equals(semarchyId) &&
					result.getSource().equals(firm.getSource())) {
				found = true;
				firmEntity = result;

				break;
			}
		}

		if (!found) {
			firmEntity = new FirmEntity();
			queryEntity.addResult(firmEntity);
		}

		final Long internalFirmId = firmEntity.getId();

		log.info("Firm (" + internalFirmId + ") has " +
				firm.getEmployees().size() + " Employee Results");

		firmEntity.setSource(firm.getSource());
		firmEntity.setUrl(firm.getFirmUrl());

		firmEntity.updateAddresses(firm.getAddresses());
		firmEntity.updateEmails(firm.getEmails());
		firmEntity.updatePhones(firm.getPhones());
		firmEntity.addEmployees(firm.getEmployees());

		return firmEntity;
	}

}
