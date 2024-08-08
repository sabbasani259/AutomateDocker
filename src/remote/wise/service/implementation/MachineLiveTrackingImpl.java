package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.handler.WhatsappHandler;
import remote.wise.handler.WhatsappTemplate;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.ConnectMySQL;

public class MachineLiveTrackingImpl 
{
	public String startLiveTracking(String loginID, String accountCode, String AssetID, String Source)
	{
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try
		{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			//Get the mobile number of the user for which WhatsApp message has to be sent
			String query ="select Primary_Moblie_Number from contact where Contact_ID='"+loginID+"'";
			rs = stmt.executeQuery(query);
			String mobileNumber="";
			while(rs.next())
			{
				mobileNumber = rs.getString("Primary_Moblie_Number");
			}
			
			//Get the WhatsApp message to be sent
			String msgBody=null;
			String whatsApplinkQ = "select WhatsApp_Body from WhatsApp_template where Event_ID=0";
			rs = stmt.executeQuery(whatsApplinkQ);
			while (rs.next())
			{
				msgBody = rs.getString("WhatsApp_Body");
			}
			
			//Get the current LAT, LONG of the machine
			String latitude=null,longitude=null;
			String amsQuery="select JSON_EXTRACT(TxnData,'$.LAT') as Latitude, JSON_EXTRACT(TxnData,'$.LONG') as Longitude" +
					" from asset_monitoring_snapshot where serial_number='"+AssetID+"'";
			rs = stmt.executeQuery(amsQuery);
			while(rs.next())
			{
				if(rs.getString("Latitude")!=null)
					latitude=rs.getString("Latitude").replaceAll("\"", "");
				if(rs.getString("Longitude")!=null)
					longitude=rs.getString("Longitude").replaceAll("\"", "");
			}
			
			if(msgBody.contains("<VIN>"))
				msgBody = msgBody.replaceAll("<VIN>", AssetID);
			if(msgBody.contains("<LAT>"))
				msgBody = msgBody.replaceAll("<LAT>", latitude);
			if(msgBody.contains("<LONG>"))
				msgBody = msgBody.replaceAll("<LONG>", longitude);
			if(msgBody.contains("#VIN#"))
				msgBody = msgBody.replaceAll("#VIN#", "#"+AssetID+"#");
			
			WhatsappTemplate whatsappTemplate = new WhatsappTemplate();				
			whatsappTemplate.setTo(mobileNumber);
			whatsappTemplate.setMsgBody(msgBody);
			whatsappTemplate.setMsgType("Live Tracking");
			new WhatsappHandler().putToKafkaProducerQueue("WhatsAppQueue", whatsappTemplate);
			
		}
		catch (Exception e) 
		{
			status="FAILURE";
			fLogger.fatal("MachineLiveTrackingImpl:startLiveTracking:DAL:loginID:"+loginID+":assetID:"+AssetID+"; Exception:",e.getMessage(),e);
		}
		finally{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(con!=null)
					con.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("MachineLiveTrackingImpl:startLiveTracking:DAL:loginID:"+loginID+"; assetID:"+AssetID+"; Exception in closing the connection:"+e.getMessage(),e);
			}			
		}
		return status;
	}
}
