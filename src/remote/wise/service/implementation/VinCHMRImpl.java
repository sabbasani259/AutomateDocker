package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class VinCHMRImpl {

	public HashMap<String, String> getCHMRForVin(String vins) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		HashMap<String, String> cmhrMap = new HashMap<>();

		String searchQuery = "select serial_number, tmh from com_rep_oem_enhanced where Serial_Number in " + "(" + vins + ")";
		iLogger.info(searchQuery);
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(searchQuery);) {

			while (rs.next()) {
				cmhrMap.put(rs.getString("serial_number"), rs.getString("tmh"));
			}

		} catch (SQLException se) {
			fLogger.fatal("issue with query " + searchQuery + se.getMessage());
		} catch (Exception e) {
			fLogger.fatal("getCHMRForVin issue " + e.getMessage());
		}
		return cmhrMap;
	}

}
