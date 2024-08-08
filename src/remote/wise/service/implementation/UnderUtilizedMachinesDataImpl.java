package remote.wise.service.implementation;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.dao.UnderUtilizedMachinesDataDao;

public class UnderUtilizedMachinesDataImpl {

	public String getUnderUtiliedMachinesData() {
		UnderUtilizedMachinesDataDao obj = new UnderUtilizedMachinesDataDao();
		List<LinkedHashMap<String, Object>> mapList = obj
				.getUnderUtilizedDataFromDB();
		String result = convertToJson(mapList);

		return result;
	}

	public String convertToJson(final List<LinkedHashMap<String, Object>> mapList) {

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonData = "";
		try {
			jsonData = objectMapper.writeValueAsString(mapList);
		} catch (IOException e) {
			e.printStackTrace();
			// fLogger.fatal("convertToJson()  Exception:" + e.getMessage());
		}
		return jsonData;
	}
}
