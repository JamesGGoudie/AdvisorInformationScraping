package ca.goudie.advisorinformationscraping.utils.csv.models;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class QueryInfo {

	@CsvBindByPosition(position = 0, required = true)
	private String firmId;
	@CsvBindByPosition(position = 1, required = true)
	private String firmName;
	@CsvBindByPosition(position = 2)
	private String city;
	@CsvBindByPosition(position = 3)
	private String region;
	@CsvBindByPosition(position = 4)
	private Boolean isUsa;

}
