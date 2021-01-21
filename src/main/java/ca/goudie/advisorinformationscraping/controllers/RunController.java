package ca.goudie.advisorinformationscraping.controllers;

import ca.goudie.advisorinformationscraping.dto.IFirmInfo;
import ca.goudie.advisorinformationscraping.services.RunService;
import ca.goudie.advisorinformationscraping.utils.csv.AisCsvUtils;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Log4j2
@RestController
@RequestMapping("/run")
public class RunController {

	@Autowired
	private RunService runService;

	@Autowired
	private ThreadPoolTaskExecutor executor;

	private Future<Void> runThread;

	@PostMapping
	public void run(
			@RequestParam("file")
			final MultipartFile file
	) throws IOException {
		final Collection<IFirmInfo> allFirmInfo =
				AisCsvUtils.parseRunRequest(file);

		if (this.runThread != null && !this.runThread.isDone()) {
			this.runThread.cancel(false);

			int i = 0;

			log.info("====================");
			log.info("====================");
			log.info("Cancelling thread");

			log.info("====================");
			log.info("====================");
			// Wait for the thread to finish before starting again.
			while (!this.runThread.isDone()) {
				try {
					++i;
					TimeUnit.SECONDS.sleep(1);
					log.info("Waited " + i + "s");
				} catch (InterruptedException e) {}
			}
		}

		this.runService.run(allFirmInfo);
		// this.runThread = this.runService.run(allFirmInfo);
	}

}
