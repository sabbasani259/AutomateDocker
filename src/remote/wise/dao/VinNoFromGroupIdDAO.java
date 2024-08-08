package remote.wise.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.AssetUtil;
import remote.wise.util.ConnectMySQL;

public class VinNoFromGroupIdDAO {

	public List<String> getVinAgainstGrpIdFromDB(List<Integer> grpIdList) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		List<String> vinList = null;
				new ArrayList<>();
		String groupIds = AssetUtil.getFormatedVinsToQuery(grpIdList);
		String query = "SELECT Asset_Id FROM custom_asset_group_snapshot where Group_ID in( "+groupIds+")";
		
		iLogger.info("query : "+query);
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(query);) {
			vinList=new ArrayList<>();
			while (rs.next()) {
	                if(rs.getString("Asset_Id")!=null){
	                	if(!vinList.contains(rs.getString("Asset_Id")))
	                	vinList.add(rs.getString("Asset_Id"));
	                }
			}

		} catch (SQLException se) {
			fLogger.fatal("issue with query " + query + se.getMessage());
		} catch (Exception e) {
			fLogger.fatal("searchVin() issue " + e.getMessage());
		}
		return vinList;
	}

}
