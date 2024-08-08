package remote.wise.businessobject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.Qhandler.ServiceHistoryQHandler;
import remote.wise.EAintegration.dataContract.ServiceHistoryInputContract;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ServiceHistoryImpl;
import remote.wise.util.ConnectMySQL;

public class ServiceClouserBO {
	
	//****************************************** get Service Closure Details for a machine ********************************************************

	public HashMap<String, Object> getServiceCloserDetails(String assetEventId)
	{

		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	
    	HashMap<String,Object> serviceDetails = new HashMap<String,Object>();
    	    	
    	ConnectMySQL connMySql = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		List<String> completedByChoices= new LinkedList<String>();
		try
		{
			conn = connMySql.getConnection();
			stmt = conn.createStatement();
			stmt1= conn.createStatement();
			iLogger.info("ServiceDetailsBO:getServiceCloserDetailsFromMysql::"+assetEventId+" START");
			String query="select * from asset_event ae,service_schedule ss where ae.Event_Type_ID=1 and ae.Asset_Event_ID='"+assetEventId+"' and ae.Service_Schedule_ID=ss.serviceScheduleId";
		
			rs = stmt.executeQuery(query);
		
			while(rs.next())
			{
					int serviceScheduleId_Int = (Integer) rs.getObject("Service_Schedule_Id");
					String serviceName = (String)rs.getObject("serviceName");
					String serialNumber = (String)rs.getObject("Serial_Number");
					String hoursSchedule= String.valueOf(rs.getObject("EngineHours_Schedule"));
					String durationSchedule= String.valueOf(rs.getObject("Duration_Schedule"));
					
					
					
					serviceDetails.put("serviceScheduleId", serviceScheduleId_Int);
					serviceDetails.put("serviceName", serviceName);
					serviceDetails.put("serialNumber", serialNumber);
					serviceDetails.put("serviceSchdule", hoursSchedule+" Hours and "+durationSchedule+" Days");

			}
			
			String qry="Select * from Service_Closure_Action";
			rs1 = stmt1.executeQuery(qry);
			while(rs1.next())
			{
				completedByChoices.add(rs1.getString("actions"));
			}
			serviceDetails.put("completedByChoices", completedByChoices);
			
			iLogger.info("ServiceDetailsBO:getServiceCloserDetailsFromMysql::"+assetEventId+" END");
		}
		catch(Exception e)
		{
			fLogger.fatal("ServiceDetailsBO:getServiceCloserDetailsFromMysql::"+assetEventId+":Exception"+e.getMessage());
			return serviceDetails;
		}
		finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if(rs1!=null)
				try {
					rs1.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if(stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			if(stmt1!=null)
				try {
					stmt1.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}		
		
		return serviceDetails;
	}
	//****************************************** End of get Service Closure Details for a machine ********************************************************

	//****************************************** Set Service Closure Details for a machine ********************************************************

	public String setServiceCloserDetails(HashMap<String, String> inputObj ){
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String output="SUCCESS";
		
		String assetEventId = inputObj.get("assetEventId");
    	String completedBy = inputObj.get("completedBy");
    	String jobCardNo = inputObj.get("jobCardNo");
    	String engineHrs = inputObj.get("engineHrs");
    	String comments = inputObj.get("comments");
    	String serviceDate = inputObj.get("serviceDate");
    	String loginId = inputObj.get("loginId");
		if(jobCardNo==null||jobCardNo==""||jobCardNo==" "){
    		jobCardNo=assetEventId;
    	}
		
    	ConnectMySQL connMySql = new ConnectMySQL();
    	
    	String serialNumber = null, attendedBy = null, nextServiceDate = null, 
    			 serviceTypeId = null, faultId = null, serviceName = null, scheduleName = null ,DBMS_partCode=null,accountCode = null,callTypeId = null;
    	Timestamp eventGeneratedTime=null;
		int serviceScheduleId=0 ;
		String eventSeverity="";
		int eventID=0;
		String countryCode="";
		FileOutputStream out=null;
		Connection conn=null;
		Statement stmt=null;
		ResultSet rs=null;
		String data="";
		try
		{
			conn = connMySql.getConnection();
			stmt = conn.createStatement();
			String sel_Query="SELECT a.Serial_Number,Event_Generated_Time,Account_Code,serviceName,scheduleName,Service_Schedule_ID,DBMS_partCode,call_type_id,Event_Severity,Event_ID,country_code FROM "+
    " asset_event ae,service_schedule ss, asset a, asset_owner_snapshot aos,account acc"+
    " WHERE"+
    	" ae.Event_Type_ID = 1"+
        " AND ae.Asset_Event_ID =" +assetEventId+
        " AND ae.Serial_Number = a.Serial_Number"+
        " AND aos.Serial_Number=a.Serial_Number"+
        " And aos.account_type = 'Dealer'"+
        " AND aos.Account_ID=acc.Account_ID"+
        " AND ae.Service_Schedule_ID = ss.serviceScheduleId"+
        " AND ae.active_status = 1"+
        " AND ae.PartitionKey = 1";
			rs = stmt.executeQuery(sel_Query);
			iLogger.info("sel_Query = "+sel_Query);
			while(rs.next())
			{
				serialNumber = (String)rs.getObject("Serial_Number");
				eventGeneratedTime = (Timestamp)rs.getObject("Event_Generated_Time");
				
				/*rs1= stmt1.executeQuery("select * from asset_owner_snapshot where Serial_Number='"+serialNumber+"' and account_type = 'Dealer'");
				if(rs1.next()){
					dealerId= (Integer)rs1.getObject("Account_ID");
				}*/
				accountCode= (String)rs.getObject("Account_Code");
				serviceName = (String)rs.getObject("serviceName");
				scheduleName = (String)rs.getObject("scheduleName");
				serviceScheduleId = (Integer)rs.getObject("Service_Schedule_ID");
				DBMS_partCode = (String)rs.getObject("DBMS_partCode");
				eventSeverity = (String)rs.getObject("Event_Severity");
				eventID = rs.getInt("Event_ID");
				countryCode = (String)rs.getObject("country_code");
				callTypeId=rs.getString("call_type_id");
			}
			ServiceHistoryInputContract inputObject = new ServiceHistoryInputContract();
			inputObject.setSerialNumber(serialNumber);
			inputObject.setDealerCode(accountCode);
			inputObject.setJobCardNumber(jobCardNo);
			inputObject.setDbmsPartCode(DBMS_partCode);
			inputObject.setServicedDate(serviceDate+" 00:00:00");
			inputObject.setProcess("ServiceHistory");
			inputObject.setReprocessJobCode("RServiceHistory");
			inputObject.setCallTypeId(callTypeId);

			iLogger.info("Service Clouser History- Input to the Q::"+inputObject.getSerialNumber()+","+inputObject.getDealerCode()+","+inputObject.getJobCardNumber()+","+"" +
					inputObject.getDbmsPartCode()+","+inputObject.getCallTypeId()+","+inputObject.getServicedDate()+"," +
					inputObject.getFileRef()+","+"" +
					inputObject.getMessageId()+","+inputObject.getProcess()+","+inputObject.getReprocessJobCode());
			
		String	responseStatus= new ServiceHistoryImpl().setServiceHistoryDetails(serialNumber,accountCode,jobCardNo,DBMS_partCode,serviceDate, null,callTypeId, null);
		iLogger.info("responseStatus for serial "+responseStatus+"...."+serialNumber);

			ServiceHistoryQHandler queueObj = new ServiceHistoryQHandler();
			
			output = queueObj.handleServiceHistoryDetailsToKafkaQueue("ServiceHistoryQueue",inputObject);
			
			if(output.equalsIgnoreCase("FAILURE")||output.contains("FAILURE")){
				data = serialNumber+"|"+accountCode+"|"+jobCardNo+"|"+DBMS_partCode+"|"+callTypeId+"|"+serviceDate+"|"+"ServiceHistory"+"|"+"RServiceHistory\r";
				 
				out = new FileOutputStream("/user/JCBLiveLink/Kafka_Service_Clouser/ServiceClouserFailure.txt",true);
				 
				out.write(data.getBytes());
				out.close();
				output="SUCCESS-WITH KAFKA ERROR";
			}
			else if(output.equalsIgnoreCase("SUCCESS")){
				iLogger.info("Service Clouser History- Input to the Q: Success Data: "+data);
			}
			
		}
		catch(Exception e)
		{
			fLogger.fatal("ServiceDetailsBO:setServiceCloserDetails:AssetEventID :"+assetEventId+":login :"+loginId+":Exception"+e.getMessage());
			output="FAILURE";
			try{
			data = serialNumber+"|"+accountCode+"|"+jobCardNo+"|"+DBMS_partCode +"|"+callTypeId+"|"+serviceDate+"|"+"ServiceHistory"+"|"+"RServiceHistory\r";
			 
			out = new FileOutputStream("/user/JCBLiveLink/Kafka_Service_Clouser/ServiceClouserFailure.txt",true);
			 
			out.write(data.getBytes());
			out.close();
			output="SUCCESS-WITH KAFKA ERROR";
			}
			catch(IOException e1){
				fLogger.fatal("ServiceDetailsBO:setServiceCloserDetails:AssetEventID :"+assetEventId+":login :"+loginId+"Not able to write in file due to  :Exception"+e.getMessage()+" data: "+data);
			}
		}
		finally
		{
			if(out!=null)
				try {
					out.close();
				} catch (Exception e1) {
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
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		
		}	
		return output;
	}
	//****************************************** END of Set Service Closure Details for a machine ********************************************************

}
