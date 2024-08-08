//CR321-20220712-Vidya SagarM-CombineToFotaReport to combine required fields to wisetraceability schema .
package remote.wise.util;


import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;


import java.sql.Connection;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class CombineToFotaReport 
{
	private static Logger fLogger = FatalLoggerClass.logger;
	private static Logger iLogger = InfoLoggerClass.logger;
	/*public static void main(String args[])
	{
		CombineToFotaReport cfr=new CombineToFotaReport();
		cfr.processCombineToFotaReport();
	}*/
	
	String[] fotalist=
			{
		"VIN",
		"IMEI",
		"currentFirmware",
		"lastFOTAupdatedDate",
		"upgradedFirmware",
		"latestFotaRequestTS",
		"HMR",
		"FOTApktsdownloaded",
		"totalFOTApkts",
		"lastFOTApktdownloadedTS",
		"fOTASanityStatus",
		"latestFOTABINconfigured",
		"GPSfixstatus",
		"ExtBatteryVoltage",
		"InternalBatteryPercentage",
		"SignalQuality",
		"PacketCounter",
		"MECUsoftwareID",
		"DECUSoftwareID",
		"EngineECUSoftwareECU",
		"AfterTreatmentECUsoftwareID",
		"scriptLastUpdatedTime"
			};
	public static ExecutorService executor;
	ArrayList<FotaStatusReport> list=new ArrayList<FotaStatusReport> ();
	Connection conn_Wise3306= null,conn_Edgeproxy4306= null,connectionWiseTrace=null;
	Statement stmt_com_rep_oem = null,stmt_fota_history = null,stmt_fota_version = null,stmt_updateFotaReport = null, stmtDuplicateCheck = null;
	ResultSet rs_com_rep_oem=null,rs_fota_version = null,rs_fota_history = null,rs_duplicateCheck=null;
	HashMap<String,String> hash=new HashMap<String,String>();
	public String processCombineToFotaReport()
	{
        //conn_Wise3306=getConnectionWise3306();
        ConnectMySQL connectMySQL=new ConnectMySQL();
        conn_Wise3306=connectMySQL.getConnection();
        //conn_Edgeproxy4306=getConnectionEdgeproxy4306();
        conn_Edgeproxy4306=connectMySQL.getEdgeProxyNewConnection();
		//connectionWiseTrace=getConnectionWiseTrace3306();
		connectionWiseTrace=connectMySQL.getConnection_wise_traceability3306();
        executor = Executors.newFixedThreadPool(100);
        try {
			if(conn_Wise3306==null||conn_Edgeproxy4306==null ||  connectionWiseTrace==null)
			{
				System.out.println("MYSQL connection not established properly . ");
				fLogger.error("MYSQL connection not established properly . ");
				return "FAILURE";
			}
			
			
				stmt_com_rep_oem = conn_Wise3306.createStatement();
				 rs_com_rep_oem = stmt_com_rep_oem.executeQuery("select * from wise.com_rep_oem_enhanced");

				while(rs_com_rep_oem.next()){
					stmt_fota_history = conn_Edgeproxy4306.createStatement();
					 stmt_fota_version = conn_Edgeproxy4306.createStatement();
					
					String serialNumber = rs_com_rep_oem.getString("Serial_Number");
					
					FotaStatusReport fotaStatusReport=new FotaStatusReport();
					fotaStatusReport.setVIN(serialNumber);//Serial_Number
					String scriptLastUpdatedTime = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new java.util.Date());
					fotaStatusReport.setScriptLastUpdatedTime(scriptLastUpdatedTime);
					fotaStatusReport.setHMR(rs_com_rep_oem.getString("tmh"));
					fotaStatusReport.setGPSfixstatus(rs_com_rep_oem.getString("GPS_fix"));
					fotaStatusReport.setExtBatteryVoltage(rs_com_rep_oem.getString("Ext_Battery_voltage"));
					fotaStatusReport.setInternalBatteryPercentage(rs_com_rep_oem.getString("internal_battery_perct"));
					fotaStatusReport.setSignalQuality(rs_com_rep_oem.getString("signal_quality"));
					fotaStatusReport.setPacketCounter(rs_com_rep_oem.getString("packet_counter"));
					fotaStatusReport.setMECUsoftwareID(rs_com_rep_oem.getString("MECU_software_ID"));
					fotaStatusReport.setDECUSoftwareID(rs_com_rep_oem.getString("DECU_software_ID"));
					fotaStatusReport.setEngineECUSoftwareECU(rs_com_rep_oem.getString("Engine_ECU_Software_ECU"));
					fotaStatusReport.setAfterTreatmentECUsoftwareID(rs_com_rep_oem.getString("Treatment_ECU_Software_ID"));

					String fota_version_query="select *from edgeproxy.fota_version where value='"+serialNumber+"' order by Version_Date Desc limit 1 ";
					 rs_fota_version=stmt_fota_version.executeQuery(fota_version_query);
					 rs_fota_history = stmt_fota_history.executeQuery("select *from edgeproxy.fota_history where vin_no='"+serialNumber+"' order by ep_timeStamp desc limit 1");
					while(rs_fota_version.next())
					{
						String binConfigured=rs_fota_version.getString("File_Path");
						binConfigured=binConfigured.replaceAll("#", "");
						fotaStatusReport.setLatestFOTABINconfigured(binConfigured);
					}
					
					while(rs_fota_history.next())
					{
						String device_version=rs_fota_history.getString("device_version");
						if(device_version==null)
						{
							device_version=rs_fota_history.getString("device_MIP_version");
						}
						fotaStatusReport.setCurrentFirmware(device_version);
						fotaStatusReport.setLatestFotaRequestTS(rs_fota_history.getString("ep_timeStamp"));
						
						fotaStatusReport.setFOTApktsdownloaded(rs_fota_history.getString("chunks_downloaded"));
						fotaStatusReport.setTotalFOTApkts(rs_fota_history.getString("total_chunks"));
						fotaStatusReport.setLastFOTApktdownloadedTS(rs_fota_history.getString("fota_received_timestamp"));						String updateStatus=rs_fota_history.getString("update_status");
						fotaStatusReport.setUpgradedFirmware(rs_fota_history.getString("upgraded_FW"));
						fotaStatusReport.setfOTASanityStatus(updateStatus);
						fotaStatusReport.setIMEI(rs_fota_history.getString("imei_number"));
						fotaStatusReport.setLastFOTAupdatedDate(rs_fota_history.getString("last_updated"));
					
					
					}
					insertInfoFotaReport(fotaStatusReport);
				}
				//citiesMap = map;
				executor.shutdown(); 
				while (!executor.isTerminated()) {   }  
    			executor.shutdownNow();

			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("MYSQL connection error"+e);
			fLogger.error("MYSQL connection error"+e);

			return "FAILURE";
		}
		finally{
			
			try { conn_Wise3306.close(); } catch (Exception e) { /* Ignored */ }
			try { conn_Edgeproxy4306.close(); } catch (Exception e) { /* Ignored */ }
			try { connectionWiseTrace.close(); } catch (Exception e) { /* Ignored */ }
			
			try { stmt_com_rep_oem.close(); } catch (Exception e) { /* Ignored */ }
			try { stmt_fota_history.close(); } catch (Exception e) { /* Ignored */ }
			try { stmt_updateFotaReport.close(); } catch (Exception e) { /* Ignored */ }
			try { stmtDuplicateCheck.close(); } catch (Exception e) { /* Ignored */ }
			try { stmt_fota_version.close(); } catch (Exception e) { /* Ignored */ }
			
			try { rs_com_rep_oem.close(); } catch (Exception e) { /* Ignored */ }
			try { rs_fota_version.close(); } catch (Exception e) { /* Ignored */ }
			try {rs_fota_history.close(); } catch (Exception e) { /* Ignored */ }
			try {rs_duplicateCheck.close(); } catch (Exception e) { /* Ignored */ }
			
			
		}
        return "SUCCESS";
	}
	public void insertInfoFotaReport(FotaStatusReport fr)
	{
		ArrayList<String> arrayListData=new ArrayList<String>();
		arrayListData.add(nullCheck(fr.getVIN()));
		arrayListData.add(nullCheck(fr.getIMEI()));
		arrayListData.add(nullCheck(fr.getCurrentFirmware()));
		arrayListData.add(nullCheck(fr.getLastFOTAupdatedDate()));
		arrayListData.add(nullCheck(fr.getUpgradedFirmware()));
		arrayListData.add(nullCheck(fr.getLatestFotaRequestTS()));
		arrayListData.add(nullCheck(fr.getHMR()));
		arrayListData.add(nullCheck(fr.getFOTApktsdownloaded()));
		arrayListData.add(nullCheck(fr.getTotalFOTApkts()));
		arrayListData.add(nullCheck(fr.getLastFOTApktdownloadedTS()));
		arrayListData.add(nullCheck(fr.getFOTASanityStatus()));
		arrayListData.add(nullCheck(fr.getLatestFOTABINconfigured()));
		arrayListData.add(nullCheck(fr.getGPSfixstatus()));
		arrayListData.add(nullCheck(fr.getExtBatteryVoltage()));
		arrayListData.add(nullCheck(fr.getInternalBatteryPercentage()));
		arrayListData.add(nullCheck(fr.getSignalQuality()));
		arrayListData.add(nullCheck(fr.getPacketCounter()));
		arrayListData.add(nullCheck(fr.getMECUsoftwareID()));
		arrayListData.add(nullCheck(fr.getDECUSoftwareID()));
		arrayListData.add(nullCheck(fr.getEngineECUSoftwareECU()));
		arrayListData.add(nullCheck(fr.getAfterTreatmentECUsoftwareID()));
		arrayListData.add(nullCheck(fr.getScriptLastUpdatedTime()));
		
		//System.out.println(arrayListData.toString());
		
		DataPushThread implObj = new DataPushThread(fotalist,arrayListData);		
		Callable<String> worker = implObj;
		executor.submit(worker);

	}
	
	public String nullCheck(String str)
	{
		if(str==null)return "";
		return str;
	}
	/*
	public Connection getConnectionWise3306(){
	
		String dbURL  = "jdbc:mysql://10.210.196.240:3306/wise";
		
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn_Wise3306 =  DriverManager.getConnection(dbURL,"root","root@123#");
		}catch (Exception e) {
			//e.printStackTrace();
			System.out.println("MYSQL connection error 3306 wise:"+e);
		}
		return conn_Wise3306;
	}
	
	public Connection getConnectionWiseTrace3306(){
		
		String dbURL  = "jdbc:mysql://10.210.196.240:3306/wise_traceability";
		
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connectionWiseTrace =  DriverManager.getConnection(dbURL,"root","root@123#");
		}catch (Exception e) {
			//e.printStackTrace();
			System.out.println("MYSQL connection error 3306 wise traceability:"+e);

		}
		return connectionWiseTrace;
	}
	
	public Connection getConnectionEdgeproxy4306(){
		
		String dbURL  = "jdbc:mysql://10.210.196.240:4306/edgeproxy";
		
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn_Edgeproxy4306 = DriverManager.getConnection(dbURL,"root","root@123#");
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("MYSQL connection error 4306edge:"+e);
		}
		return conn_Edgeproxy4306;
	}
	
	*/
	
	public boolean checkDuplicate(String vin)
	{
		boolean output=false;
		String query="SELECT VIN from fota_report WHERE VIN='"+vin+"'";
		try{
			stmtDuplicateCheck = connectionWiseTrace.createStatement();
			Class.forName("com.mysql.jdbc.Driver");
			 rs_duplicateCheck    = stmtDuplicateCheck.executeQuery(query);
			while(rs_duplicateCheck.next())
			{
				return true;
			}

		}catch(Exception e){
			System.out.println("Error while getting DB connection :: "+e);
			fLogger.error("Error while getting DB connection :: "+e);
			e.printStackTrace();
		}
		finally{
			/*try{
				if(con!=null)
					con.close();
				if(stmt!=null)
					stmt.close();
			}catch(Exception e){
				System.out.println("Error while closing DB connection :: "+e);
				e.printStackTrace();
			}*/
		}
		return output;
	}
	

	 class DataPushThread implements Callable<String>{


		 private String[] keys;
		 private ArrayList<String> values;
		 private boolean chkFlag;
		 private String query;
		public DataPushThread(String[] keys,ArrayList<String> values){
			this.keys=keys;
			this.values=values;
		}
		@Override
		public String call() throws Exception
		{
			try{
				chkFlag=checkDuplicate(values.get(0));
			
			if(!chkFlag){
				query="INSERT INTO fota_report (";
						String keysQueryString="";
						for(int i=0;i<keys.length;++i)
						{
							keysQueryString+=keys[i]+",";
						}
				keysQueryString=keysQueryString.substring(0, keysQueryString.length()-1);
				query+=keysQueryString;
				query+=") VALUES (";
				keysQueryString="";
				for(int i=0;i<values.size();++i)
				{
					keysQueryString+="'"+values.get(i)+"',";
				}
				keysQueryString=keysQueryString.substring(0, keysQueryString.length()-1);
				query+=keysQueryString+")";
			}
			else {
				query="UPDATE fota_report SET ";

				
				for(int i=1;i<values.size();++i)
				{
					query=query+keys[i]+"='"+values.get(i)+"',";
				}
				query=query.substring(0, query.length()-1);
				query+=" where "+keys[0]+"='"+values.get(0)+"'";
			}
			stmt_updateFotaReport = connectionWiseTrace.createStatement();
			System.out.print("the final query"+query);
			stmt_updateFotaReport.executeUpdate(query);
			System.out.println(values.get(0)+" updated succesfully");
			iLogger.info(values.get(0)+" updated succesfully");
			return "SUCCESS";
			}
			catch(Exception ex)
			{
				System.out.println(values.get(0)+" updated failed"+ex);
				fLogger.error(values.get(0)+" updated failed"+ex);
				return "FAILURE";
				
			}
		

		}
	 }
}
