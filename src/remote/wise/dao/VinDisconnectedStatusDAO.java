package remote.wise.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class VinDisconnectedStatusDAO {

	public String getDisconnectionStatusOfVin(String vin) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		String query=null;
		String result ="FALSE";
		if(vin.length()==7){
			 query="select DisconnectedStatus from asset where serial_number like '%"+vin+"%' ";
		 }
		else{
		query="select DisconnectedStatus from asset where serial_number='"+vin+"'";
		}
		iLogger.info("query for getDisconnectionStatusOfVin = "+query);
		try (Connection connection = connFactory.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(query);
				ResultSet rs= statement.executeQuery();
				) {
				
				while(rs.next()){
					if(rs.getInt("DisconnectedStatus")==0)
						result="TRUE";
				}
			

		} catch (Exception e) {
			result="FAILURE";
			fLogger.fatal("getDisconnectionStatusOfVin()::issue while querying in the asset table "
					+ e.getMessage());
		}
		return result;
	}
	
	/**
	 * @author BI20081288
	 * @param VIN
	 * @return communication status for last 2 month
	 *
	 */
 public String getCommunicationStatus(String Vin){
	 Logger fLogger = FatalLoggerClass.logger;
	 Logger iLogger = InfoLoggerClass.logger;
	 ConnectMySQL connFactory = new ConnectMySQL();
	 String query=null;
	 String result ="NOT COMMUNICATED";
	 // fetching vin no  last updated  from today to 2 month before 
	 if(Vin.length()==7){
		 query="select serial_number from com_rep_oem_enhanced where Pkt_Recd_TS>=now()-interval 2 month and serial_number like '%"+Vin+"%' ";
	 }
	 else{
	 query="select serial_number from com_rep_oem_enhanced where Pkt_Recd_TS>=now()-interval 2 month and serial_number='"+Vin+"'";
	 }
	 try (Connection connection = connFactory.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(query);
				ResultSet rs= statement.executeQuery();
				) {
				
				while(rs.next()){
						result="COMMUNICATED";
					
				}
			

		} catch (Exception e) {
			result="NOT COMMUNICATED";
			fLogger.fatal("getCommunicationStatus()::issue while querying in the asset_monitoring_snapshot table "
					+ e.getMessage());
		}
	 return result;
 }
}
