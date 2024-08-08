package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

/**
 * FuelUtilizationDetailImpl will allow to List ofFuel Utilization Details for specified LoginId and SerialNumber
 * @author KO369761
 *
 */

public class DeleteDuplicateFaultDetailsRecordsImpl {


	public String deleteDuplicateFaultDetailsRecords(){

		Logger fLogger = FatalLoggerClass.logger;
		String response = "SUCCESS";

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		try{
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			
			rs = statement.executeQuery("select file_name from fault_details where deletion_flag = 1");
			while(rs.next()){
				CommonUtil.updateInterfaceLogDetails(rs.getString(1), "sucessCount", 1);
				CommonUtil.updateInterfaceLogDetails(rs.getString(1),"failureCount", -1);
			}
			statement.executeUpdate("delete from fault_details where deletion_flag = 1");
		}

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception in delete token records from table::idMaster::"+e.getMessage());
			response = "FAILURE";
		}
		finally {
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}
}
