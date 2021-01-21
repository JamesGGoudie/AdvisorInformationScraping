package ca.goudie.advisorinformationscraping.controllers;

import ca.goudie.advisorinformationscraping.dto.IFirmInfo;
import ca.goudie.advisorinformationscraping.exceptions.RunFailureException;
import ca.goudie.advisorinformationscraping.services.RunService;
import ca.goudie.advisorinformationscraping.utils.csv.AisCsvUtils;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

@Log4j2
@RestController
@RequestMapping("/run")
public class RunController {

	@Autowired
	private RunService runService;

	@PostMapping
	public void runApp(
			@RequestParam("file")
			final MultipartFile file
	) throws IOException, RunFailureException {
		final Collection<IFirmInfo> allFirmInfo =
				AisCsvUtils.parseRunRequest(file);

		this.runService.run(allFirmInfo);
	}

	@GetMapping
	public Boolean checkIfAppIsRunning() {
		return this.runService.isRunning();
	}

	@DeleteMapping
	public void cancelRunningApp() {
		this.runService.cancel();
	}

}
