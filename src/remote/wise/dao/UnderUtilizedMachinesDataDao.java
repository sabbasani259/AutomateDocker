package remote.wise.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class UnderUtilizedMachinesDataDao {
	 private Logger iLogger = InfoLoggerClass.logger;
	    private Logger fLogger = FatalLoggerClass.logger;
	    private ConnectMySQL connFactory = new ConnectMySQL();

	 

	    public  List<LinkedHashMap<String, Object>> getUnderUtilizedDataFromDB() {
	        String query = "select AssetID,PeriodHMR,AvgUtilizationPerct from underUtilizationMachinesInfo";
	        iLogger.info(query);
	        List<LinkedHashMap<String, Object>> mapList = new ArrayList<>();

	 

	        try (Connection connection = connFactory.getConnection();
	                PreparedStatement statement = connection.prepareStatement(query);
	                ResultSet rs = statement.executeQuery(query);) {

	 

	            LinkedHashMap<String, Object> tableMap = null;
	            while (rs.next()) {
	                tableMap = new LinkedHashMap<String, Object>();
	                if(rs.getString("AssetID")!=null)
	                tableMap.put("AssetID", rs.getString("AssetID"));
	                if(rs.getString("PeriodHMR")!=null)
	                tableMap.put("PeriodHMR", rs.getString("PeriodHMR"));
	                if(rs.getString("AvgUtilizationPerct")!=null)
	                tableMap.put("AvgUtilizationPerct", rs.getString("AvgUtilizationPerct"));

	                mapList.add(tableMap);
	            }

	 

	        } catch (Exception e) {
	            fLogger.fatal(e.getMessage());
	            fLogger.info(e.getMessage());
	        }
			return mapList;
	    }
}