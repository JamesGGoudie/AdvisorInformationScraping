package ca.goudie.advisorinformationscraping.utils.csv;

import ca.goudie.advisorinformationscraping.dto.IFirmInfo;
import ca.goudie.advisorinformationscraping.utils.csv.models.FirmInfo;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

public class AisCsvUtils {

	public static Collection<IFirmInfo> parseRunRequest(
			final MultipartFile file
	) throws IOException {
		final InputStream is = file.getInputStream();
		final Reader reader = new InputStreamReader(is);

		final CsvToBean<IFirmInfo> csvToBean =
				new CsvToBeanBuilder(reader).withType(FirmInfo.class).build();

		return csvToBean.parse();
	}

}
