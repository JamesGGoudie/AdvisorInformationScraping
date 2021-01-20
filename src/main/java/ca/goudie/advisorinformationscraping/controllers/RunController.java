package ca.goudie.advisorinformationscraping.controllers;

import ca.goudie.advisorinformationscraping.dto.IFirmInfo;
import ca.goudie.advisorinformationscraping.dto.ScrapeResult;
import ca.goudie.advisorinformationscraping.services.RunService;
import ca.goudie.advisorinformationscraping.utils.csv.AisCsvUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/run")
public class RunController {

	@Autowired
	private RunService runService;

	@PostMapping
	public Collection<ScrapeResult> run(
			@RequestParam("file")
			final MultipartFile file
	) throws IOException {
		final Collection<IFirmInfo> allFirmInfo =
				AisCsvUtils.parseRunRequest(file);

		return this.runService.run(allFirmInfo);
	}

}
