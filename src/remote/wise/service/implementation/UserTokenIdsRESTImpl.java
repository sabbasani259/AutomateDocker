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

public class UserTokenIdsRESTImpl {


	public String deleteUserTokens(){

		Logger fLogger = FatalLoggerClass.logger;
		String response = "SUCCESS";

		Connection prodConnection = null;
		Statement statement = null;
		ConnectMySQL connMySql = new ConnectMySQL();

		try{
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.MINUTE, -30);
			
			Date deletingDateTime = cal.getTime();
			Timestamp currentTime = new Timestamp(deletingDateTime.getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String deletingTime=sdf.format(currentTime).toString().replaceAll("-","").replaceAll(":","").replaceAll("\\s","");
			
			//DF20180917 - Rajani Nagaraju - AutoReporting - Fixed user to be used where in sessionId for the user should not be deactivated as the job runs for more than half an hour
			String deleteQuery = "delete from idMaster where timestamp < '"+deletingTime+"' and " +
					"CAST(FROM_BASE64(userId) AS CHAR(10000) ) != 'repo00265' ";
			
			statement.executeUpdate(deleteQuery);
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
	

	public int validateUserToken(String tokenId){

		Logger fLogger = FatalLoggerClass.logger;
		int response = 0;

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		ConnectMySQL connMySql = new ConnectMySQL();

		try{
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			
			rs = statement.executeQuery("select * from idMaster where id ='"+tokenId+"' and status = 1");
			if(rs.next()){
				response = 1;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception in validating token from table::idMaster::"+e.getMessage());
			response = 0;
		}
		finally {
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
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
	
	public String userSignOut(String userId){
		
		String response = "SUCCESS";
		
		//DF20180731 - KO369761 - Deleting existing token ids for the user.
    	response = new CommonUtil().deleteUserTokenIds(userId);
    	
		return response;
	}
}
