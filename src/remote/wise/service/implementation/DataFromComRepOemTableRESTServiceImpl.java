package remote.wise.service.implementation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.dao.DataFromComRepOemTableDAO;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class DataFromComRepOemTableRESTServiceImpl {

	private Logger fLogger = FatalLoggerClass.logger;
	private Logger iLogger = InfoLoggerClass.logger;

	public String getMapData(List<String> vinList) {
		// to add comma in the list after each element
		long start=System.currentTimeMillis();
		long end = 0;
		String vins = "";
		String result="";
		try{
		if (!vinList.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String vin : vinList)
				sb.append("'").append(vin.trim()).append("'").append(",");
			vins = sb.deleteCharAt(sb.length() - 1).toString();
		}
		DataFromComRepOemTableDAO obj = new DataFromComRepOemTableDAO();
		Map<String, Map<String, Object>> map=obj.fetchDataFromDB(vins);
		result=convertToJson(map);
		end =System.currentTimeMillis();
		}catch(Exception e){
			fLogger.fatal(e.getMessage());
			fLogger.info(e.getMessage());
		}finally{
			iLogger.info("Total time getting response from wise : "+(end-start));
		}
		return result;
	}
	
	public String convertToJson(final Map<String, Map<String, Object>> map) {

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonData = "";
		try {
			jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
		} catch (IOException e) {
			e.printStackTrace();
			fLogger.fatal("convertToJson()  Exception:" + e.getMessage());
		}
		return jsonData;
	}
}
