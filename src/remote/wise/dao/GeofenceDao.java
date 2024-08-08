package remote.wise.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

/**
 * CR428 : 20230830 : Dhiraj Kumar : Sea Ports (Landmark) Configurations
 */
public class GeofenceDao {

	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	ConnectMySQL connFactory = new ConnectMySQL();

	public List<Integer> getlandmarkIds(String landmarkName) {
		List<Integer> landmarkIdList = new ArrayList<>();

		String selectQ = "select l.landmark_id from landmark l inner join landmark_catagory lc on l.landmark_category_id=lc.landmark_category_id "
				+ "where lc.Landmark_Category_Name='" + landmarkName + "'";
		try (Connection conn = connFactory.getConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(selectQ)) {
			while (rs.next()) {
				landmarkIdList.add(rs.getInt("landmark_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception occured : " + e.getMessage());
		}
		iLogger.info("Landmark Id list : " + landmarkIdList.toString());
		return landmarkIdList;
	}

	public String updateLandmarkAssetMap(String landmarkname, String vin) {
		String status = "SUCCESS";
		List<Integer> landmarkIdList = getlandmarkIds(landmarkname);
		int rowsInserted = 0;
		String insertQ = "INSERT IGNORE INTO landmark_asset (landmark_id, serial_number) VALUES ";
		try (Connection conn = connFactory.getConnection(); 
				Statement statement = conn.createStatement()) {
			for (Integer landmarkId : landmarkIdList) {
				insertQ += "("+ landmarkId + ", '" + vin +"'), ";
			}
			insertQ = insertQ.substring(0, insertQ.lastIndexOf(","));
			rowsInserted=statement.executeUpdate(insertQ);
		} catch (Exception e) {
			e.printStackTrace();
			status = "FAILURE";
			fLogger.fatal("Exception occured : " + e.getMessage());
		}
		iLogger.info("Rows inserted in landmark_asset table :  " + rowsInserted);

		return status;
	}
}
