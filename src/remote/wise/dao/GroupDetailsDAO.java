package remote.wise.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class GroupDetailsDAO {

	public List<LinkedHashMap<String, Object>> getGroupDetailsFromDB(String loginid) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String searchQuery = "SELECT distinct(cags.group_id), group_name FROM custom_asset_group_snapshot cags, custom_asset_group cag "+
				"WHERE user_id="+"'"+loginid+"'"+" and cags.group_id = cag.group_id";
		boolean found = false;
		iLogger.info("searchQuery : "+searchQuery);
		List<LinkedHashMap<String, Object>> mapList = new ArrayList<>();
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(searchQuery);) {
			LinkedHashMap<String, Object> tableMap = null;
			while (rs.next()) {
				 tableMap = new LinkedHashMap<String, Object>();
	                if(rs.getString("Group_Name")!=null)
	                tableMap.put("Group_Name", rs.getString("Group_Name"));
	                if(rs.getString("Group_ID")!=null)
	                tableMap.put("Group_ID", rs.getString("Group_ID"));

	                mapList.add(tableMap);
			}

		} catch (SQLException se) {
			fLogger.fatal("issue with query " + searchQuery + se.getMessage());
		} catch (Exception e) {
			fLogger.fatal("searchVin() issue " + e.getMessage());
		}
		return mapList;

		
	}

}
