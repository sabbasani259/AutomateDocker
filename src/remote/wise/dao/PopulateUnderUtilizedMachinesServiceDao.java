package remote.wise.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class PopulateUnderUtilizedMachinesServiceDao {

	public static void updateDB(List<LinkedHashMap<String, Object>> responseList){
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		String query="delete from underUtilizationMachinesInfo";
		String insertQuery="insert into underUtilizationMachinesInfo values(";
		for(LinkedHashMap<String, Object> map:responseList){
		for (Map.Entry<String,Object> entry : map.entrySet()){
			insertQuery=insertQuery+"'"+entry.getValue()+"'"+",";
		}
		if(insertQuery.endsWith(","))
			insertQuery=insertQuery.substring(0,insertQuery.length()-1);
		insertQuery+="),(";
		}
		if(insertQuery.endsWith("("))
			insertQuery=insertQuery.substring(0,insertQuery.length()-2);
		
		iLogger.info("updateDB() insertQuery():: "+insertQuery);
		try (Connection connection = connFactory.getConnection();
				PreparedStatement statement = connection.prepareStatement(query);
				ResultSet rs1 = null;) {
			statement.executeUpdate(query);
			statement.executeUpdate(insertQuery);
		}catch(Exception e){
			fLogger.fatal("updateDB()::issue while updating DB "+e.getMessage());
		}
	}
	/*public static void main(String[] args) {
		List<LinkedHashMap<String, Object>> responseList = new ArrayList<LinkedHashMap<String, Object>>();
		LinkedHashMap<String, Object> map1=new LinkedHashMap<>();
		map1.put("AssetID", "HAR3DXSST02596097");
		map1.put("PeriodHMR", "0.0");
		map1.put("AvgUtilizationPerct", "0.0");
		LinkedHashMap<String, Object> map2=new LinkedHashMap<>();
		map2.put("AssetID", "HAR2DXLSC02577493");
		map2.put("PeriodHMR", "0.0");
		map2.put("AvgUtilizationPerct", "0.0");
		LinkedHashMap<String, Object> map3=new LinkedHashMap<>();
		map3.put("AssetID", "HAR3DXSSA02596061");
		map3.put("PeriodHMR", "0.0");
		map3.put("AvgUtilizationPerct", "0.0");
		responseList.add(map1);
		responseList.add(map2);
		responseList.add(map3);
		PopulateUnderUtilizedMachinesServiceDao.updateDB(responseList);
	}*/
}
