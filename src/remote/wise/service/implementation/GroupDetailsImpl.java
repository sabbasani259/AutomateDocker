package remote.wise.service.implementation;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.dao.GroupDetailsDAO;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class GroupDetailsImpl {

	public String getGroupDetails(String loginId) {
			GroupDetailsDAO daoObj = new GroupDetailsDAO();
			List<LinkedHashMap<String, Object>> mapList = daoObj
					.getGroupDetailsFromDB(loginId);
			String result = convertToJson(mapList);
			return result;
	}

	public String convertToJson(
			final List<LinkedHashMap<String, Object>> mapList) {

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
