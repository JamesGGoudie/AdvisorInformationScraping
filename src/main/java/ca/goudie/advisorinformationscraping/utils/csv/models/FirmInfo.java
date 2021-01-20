package ca.goudie.advisorinformationscraping.utils.csv.models;

import ca.goudie.advisorinformationscraping.dto.IFirmInfo;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class FirmInfo implements IFirmInfo {

	@CsvBindByPosition(position = 0, required = true)
	private String id;
	@CsvBindByPosition(position = 1, required = true)
	private String name;
	@CsvBindByPosition(position = 2)
	private String city;
	@CsvBindByPosition(position = 3)
	private String region;
	@CsvBindByPosition(position = 4)
	private Boolean isUsa;

}
