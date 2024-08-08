package remote.wise.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class UnblockVINSDAO {

	public String updateVinToUnblockedState(String vin, String loginId) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		String query="";
		String result="";
		boolean isActiveVin=false;
//		iLogger.info("Query for updateVinToUnblockedState : "+query);
		query="select DisconnectedStatus from asset where serial_number='"+vin+"'";
		

		iLogger.info("Query for fetching data from asset table : "+query);
		//getting the vins updated on the day
			try (Connection connection = connFactory.getConnection();
					PreparedStatement statement = connection
							.prepareStatement(query);
					ResultSet rs=statement.executeQuery();
					) {
				while(rs.next()){
					if(rs.getInt("DisconnectedStatus")==1)
						isActiveVin=true;
				}
	
			} catch (Exception e) {
				result="FAILURE";
				fLogger.fatal("updateAssetTable()::issue while fetching data from  the blockedVIn table "
						+ e.getMessage());
			}
			
		
			if(!isActiveVin){
			//updating the asset table
			try (Connection connection = connFactory.getConnection();
					PreparedStatement statement = connection
							.prepareStatement(query);
					) {
					
					query = "update BlockedVins set StatusFlag=0"+
						" where VINNumber="+"'"+vin+"'";
					statement.executeUpdate(query);
					
					
					Timestamp requestTS=new Timestamp(new Date().getTime());
					SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
					String date= dformat.format(requestTS);
					query = "update BlockVINTraceability set RequestTS="+"'"+date+"'"+",UserRequested="+"'"+loginId+"'"+
							" where VINNumber="+"'"+vin+"'";
					iLogger.info("Query for updateVinToUnblockedState : "+query);
					statement.executeUpdate(query);
					
					query = "update asset set DisconnectedStatus=1"+
							" where serial_number="+"'"+vin+"'";
					statement.executeUpdate(query);
				
					result=vin +" activated successfully";
	
			} catch (Exception e) {
				result="FAILURE";
				fLogger.fatal("updateVinToUnblockedState()::issue while updating the asset table "
						+ e.getMessage());
			}
			}
			else{
				result=vin+" is in active state";
			}
		return result;
	}

	public String ifVinExistsInDB(String vin) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		String query=null;
		String result ="FALSE";
		query="select serial_number from asset where serial_number='"+vin+"'";
		iLogger.info("query to chk if vin exists = "+query);
		try (Connection connection = connFactory.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(query);
				ResultSet rs= statement.executeQuery();
				) {
				
				while(rs.next()){
						result="TRUE";
				}
			

		} catch (Exception e) {
			result="FAILURE";
			fLogger.fatal("ifVinExistsInDB()::issue while querying in the asset table "
					+ e.getMessage());
		}
		return result;
	}
}