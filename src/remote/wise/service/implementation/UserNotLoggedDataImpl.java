package remote.wise.service.implementation;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.dao.UserNotLoggedDataDao;

public class UserNotLoggedDataImpl {

	public String getUserNotLoggedData(String accountFilter, String accountIdList) {
		UserNotLoggedDataDao obj = new UserNotLoggedDataDao();
		List<LinkedHashMap<String, Object>> mapList = obj
				.getUserNotLoggedDataFromDB(accountFilter,accountIdList);
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
