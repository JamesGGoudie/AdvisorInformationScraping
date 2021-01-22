package ca.goudie.advisorinformationscraping.controllers;

import ca.goudie.advisorinformationscraping.constants.SearchEngineConstants;
import ca.goudie.advisorinformationscraping.constants.WebBrowserConstants;
import ca.goudie.advisorinformationscraping.dto.IFirmInfo;
import ca.goudie.advisorinformationscraping.exceptions.RunFailureException;
import ca.goudie.advisorinformationscraping.services.RunService;
import ca.goudie.advisorinformationscraping.utils.csv.AisCsvUtils;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
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
			final MultipartFile file,
			@RequestParam(value = "limit", required = false, defaultValue = "3")
			final Integer resultsLimit,
			@RequestParam(
					value = "browser",
					required = false,
					defaultValue = WebBrowserConstants.CHROMIUM)
			final String webBrowserKey,
			@RequestParam(
					value = "engine",
					required = false,
					defaultValue = SearchEngineConstants.GOOGLE)
			final String searchEngineKey
	) throws IOException, RunFailureException {
		final Collection<IFirmInfo> allFirmInfo =
				AisCsvUtils.parseRunRequest(file);

		log.info("REQ: Staring App");
		log.info("Firms to Process: " + allFirmInfo.size());
		log.info("Target Results per Firm: " + resultsLimit);
		log.info("Chosen Web Browser: " + webBrowserKey);
		log.info("Chosen Search Engine: " + searchEngineKey);

		this.runService.prepareThreadManager();
		this.runService.runThread(
				allFirmInfo,
				resultsLimit,
				webBrowserKey,
				searchEngineKey);
	}

	@GetMapping("/active")
	public Boolean checkIfAppIsRunning() {
		log.info("REQ: Checking if App is Running");
		return this.runService.isRunning();
	}

	@PostMapping("/cancel")
	public void cancelApp() {
		log.info("REQ: Stopp the App");
		this.runService.reset();
	}

	@PostMapping("/reset")
	public void resetApp() {
		log.info("REQ: Reset the App");
		this.runService.reset();
	}

}
