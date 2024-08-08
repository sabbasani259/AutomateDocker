/**
 * CR337 : 20220721 : Dhiraj K : Property file read.
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Stack;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServiceClouserReportUpdateImpl {

	public String setServiceCloserDetails() {
	
	String output="SUCCESS";
	Logger fLogger = FatalLoggerClass.logger;
	Logger iLogger= InfoLoggerClass.logger;
	ConnectMySQL connMySql = new ConnectMySQL();
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	String serialNumber = null;
	String assetEventId = null;
	String countryCode = null;
	String eventSeverity = null;
	int eventID = 0;
	String eventGeneratedTime = null;
	String serviceDate = null;
	String loginId = null;
	OutputStream os = null; 
	BufferedReader br = null;
	String result = null;
	Stack<String> del_assetEventId = new Stack<String>();
	//CR337.sn
	String connIP=null;
	String connPort=null;
	Properties prop = null;
	try{
		prop = CommonUtil.getDepEnvProperties();
		connIP = prop.getProperty("MDA_ServerIP");
		connPort = prop.getProperty("MDA_ServerPort");
		iLogger.info("ServiceClouserReportUpdateImpl:MDAIP" + connIP + " :: MDAPort" +connPort);
	}catch(Exception e){
		fLogger.fatal("ServiceClouserReportUpdateImpl : setServiceCloserDetails : " +
				"Exception in getting Server Details for MDA Layer from properties file: " +e);
	}
	//CR337.en
	try
	{
				conn = connMySql.getConnection();
				stmt = conn.createStatement();
				String sel_Query="select * from Service_Clouser_MoolDA_Failure";
				rs = stmt.executeQuery(sel_Query);
				while(rs.next())
				{
					assetEventId = rs.getString("assetEventId");
					countryCode = rs.getString("countryCode");
					serialNumber = rs.getString("serialNumber");
					eventSeverity = rs.getString("eventSeverity");
					eventID = rs.getInt("eventID");
					eventGeneratedTime = rs.getString("eventGeneratedTime");
					serviceDate = rs.getString("serviceDate");
					loginId = rs.getString("loginId");
					 iLogger.info("ServiceClouserReportUpdateImpl:setServiceCloserDetails:AssetEventID :"+assetEventId+":login :"+loginId+": Calling MoolDA AlertClosureService Reports ");
					 JSONObject json = new JSONObject();
					 if(countryCode==null){
							countryCode="+91";
					 }
				 	 json.put("uniqueIdentifier",assetEventId);
				 	 json.put("countryCode",countryCode);
				 	 json.put("assetID",serialNumber);
				 	 json.put("eventSeverity",eventSeverity);
				 	 json.put("eventID",Integer.toString(eventID));
				 	 json.put("eventGenerationTime",eventGeneratedTime);
				 	 json.put("eventClosureTime",serviceDate);
				 	 json.put("loginID",loginId);
				 	 String request=json.toString();
				 	 //String URL= "http://10.210.196.206:26030/MoolDAReports/AlertClosureService/triggerAlertClosure";//PROD  //CR337.o
				 	 String URL= "http://"+connIP+":"+connPort+"/MoolDAReports/AlertClosureService/triggerAlertClosure";//PROD //CR337.n
				 	 //String URL= "http://10.106.68.9:8112/MoolDAReports/AlertClosureService/triggerAlertClosure";//SIT				 	 
				 	 URL url = new URL(URL);
				 	 HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				 	 connection.setDoOutput(true);
				 	 connection.setRequestMethod("POST");
				 	 connection.setRequestProperty("Content-Type", "application/json");
				 	 os = (OutputStream) connection.getOutputStream();
				 	 os.write(request.getBytes());
				 	 os.flush();
				 	 iLogger.info("setServiceCloserDetails:setServiceCloserDetails:AssetEventID::---> HTTP code from MoolDA :"+connection.getResponseCode());
				 	 if (connection.getResponseCode() != 200 && connection.getResponseCode() != 204) {
						fLogger.fatal("setServiceCloserDetails:setServiceCloserDetails:AssetEventID :"+assetEventId+":login :"+loginId+" Response: "+connection.getResponseCode()+" :Exception caused because of no response from MOOLDA URL:"+URL+" Request:"+request);
						iLogger.info("MDAReports report status: FAILURE for setServiceCloserDetails Service Report ::Response Code:"+connection.getResponseCode());
						throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
				 	 }
				 	iLogger.info("MDAReports report status: SUCCESS for setServiceCloserDetails Service Report ::Response Code:"+connection.getResponseCode());
				 	 br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
				 	 while ((result = br.readLine()) != null) {
				 		 output = result.toUpperCase();
				 	 }
				 	 iLogger.info("setServiceCloserDetails:setServiceCloserDetails:AssetEventID :"+assetEventId+":login :"+loginId+": MoolDA Report response "+output);
				 	 if(output.equalsIgnoreCase("SUCCESS")){
				 		del_assetEventId.push(assetEventId);
				 	 }
				}
				while(!del_assetEventId.isEmpty()){
					String assetEventId1=del_assetEventId.pop();
					String del_sql = "DELETE FROM Service_Clouser_MoolDA_Failure " +
			                   "WHERE assetEventId ='"+assetEventId1+"'";
					 iLogger.info("ServiceClouserReportUpdateImpl:setServiceCloserDetails: delete query: "+del_sql);
					stmt.executeUpdate(del_sql);
				}
	}
	catch(Exception e)
	{
		fLogger.fatal("setServiceCloserDetails:setServiceCloserDetails:AssetEventID :"+assetEventId+":login :"+loginId+":Exception"+e.getMessage());
		output="FAILURE";
		return output;
	}
	finally
	{
		if(os!=null)
			try {
				os.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		if(br!=null)
			try {
				br.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		if(rs!=null)
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		if(stmt!=null)
			try {
				stmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		if(conn!=null)
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	}
		return output;
	}
}
