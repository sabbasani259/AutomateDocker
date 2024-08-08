package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

/**
 * CR434 : 20231006 : Dhiraj K : MOSP Software Id Report changes
 */

public class DynamicMOSPWeekWiseTableServiceImpl {

	public String createTable(int week, int year) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		String status="FAILURE";
		Properties prop = CommonUtil.getDepEnvProperties();
		String weekWisetableName = prop.getProperty("JCB_TAssetMOSPMonData_TableKey");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		int nextWeek = cal.get(Calendar.WEEK_OF_YEAR);
		int nextWeekYear = cal.get(Calendar.YEAR);

		if (week == 0 && year == 0) {
			week = nextWeek;
			year=nextWeekYear;
		}

		weekWisetableName = weekWisetableName.replace("week", String.valueOf(week))
											.replace("year",String.valueOf(year));
		
		iLogger.info("Dynamic week wise MOSP data Table for creation : " + weekWisetableName);
		String createQuery = "CREATE TABLE IF NOT EXISTS `wise`.`"+weekWisetableName+"` (" + 
				"  `Serial_Number` varchar(45) NOT NULL," + 
				"  `Transaction_Timestamp` datetime NOT NULL," + 
				"  `Created_Timestamp` timestamp NULL DEFAULT NULL," + 
				"  `Last_Updated_Timestamp` timestamp NULL DEFAULT NULL," + 
				"  `Message_ID` json DEFAULT NULL," + 
				"  `TxnData` json DEFAULT NULL," + 
				"  `Segment_ID` int(4) NOT NULL," + 
				"  `Update_Count` int(11) DEFAULT NULL," + 
				"  PRIMARY KEY (`Serial_Number`,`Transaction_Timestamp`,`Segment_ID`)," + 
				"  KEY `AssetMon_VIN_Txn_idx` (`Serial_Number`,`Transaction_Timestamp`,`Segment_ID`))" + 
				"  PARTITION BY HASH (Segment_ID) PARTITIONS 300";
		ConnectMySQL connFactory = new ConnectMySQL();
		try(Connection conn = connFactory.getDatalakeConnection_3309();
				Statement st = conn.createStatement()){
			st.executeUpdate(createQuery);
			iLogger.info("Created table : "+weekWisetableName);
			status = "SUCCESS";
		}catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception occurred : " + e.getMessage());
		}
		return status;
	}

	public String purgeTable(int week, int year) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		String status="FAILURE";
		Properties prop = CommonUtil.getDepEnvProperties();
		String weekWisetableName = prop.getProperty("JCB_TAssetMOSPMonData_TableKey");
		String retentionStr = prop.getProperty("JCB_TAssetMOSPMonData_Table_RetentionWeeks");
		int retention = Integer.valueOf("-"+retentionStr);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_YEAR, retention);
		int purgeWeek = cal.get(Calendar.WEEK_OF_YEAR);
		int purgeWeekYear = cal.get(Calendar.YEAR);

		if (week == 0 && year == 0) {
			week=purgeWeek;
			year=purgeWeekYear;
		}

		weekWisetableName = weekWisetableName.replace("week", String.valueOf(week))
											.replace("year",String.valueOf(year));
		
		iLogger.info("Dynamic week wise MOSP data Table for purging : " + weekWisetableName);
		String dropQuery = "DROP TABLE IF EXISTS `wise`.`"+weekWisetableName+"`";

		ConnectMySQL connFactory = new ConnectMySQL();
		try(Connection conn = connFactory.getDatalakeConnection_3309();
				Statement st = conn.createStatement()){
			st.executeUpdate(dropQuery);
			iLogger.info("Purged table : "+weekWisetableName);
			status = "SUCCESS";
		}catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception occurred : " + e.getMessage());
		}
		return status;
	}

}
