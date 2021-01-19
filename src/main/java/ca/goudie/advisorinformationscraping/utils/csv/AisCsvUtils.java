package ca.goudie.advisorinformationscraping.utils.csv;

import ca.goudie.advisorinformationscraping.utils.csv.models.QueryInfo;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

public class AisCsvUtils {

	public static Collection<QueryInfo> parseRunRequest(
			final MultipartFile file
	) throws IOException {
		final InputStream is = file.getInputStream();
		final Reader reader = new InputStreamReader(is);

		final CsvToBean<QueryInfo> csvToBean =
				new CsvToBeanBuilder(reader).withType(QueryInfo.class).build();

		return csvToBean.parse();
	}

}
