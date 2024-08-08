//CR321-20220712-Vidya SagarM-CombineToFotaReport to combine required fields to wisetraceability schema .
package remote.wise.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

//CR321V2-20221130-VidyaSagarM-Fota report to have batch count 1000 CR321
//CR321V2.sn
public class CombineToFotaReportV2 
{
	private final static  int MAX_BATCH_COUNT = 100;
	private static int[] insertCount=null;
	private static int totalInsertedCount=0; 
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	private static Logger fLogger = FatalLoggerClass.logger;
	private static Logger iLogger = InfoLoggerClass.logger;
	public static void main(String args[])
	{
		CombineToFotaReportV2 cfr=new CombineToFotaReportV2();
		cfr.processCombineToFotaReport();
	}
	
	String[] fotalist=
			{
			"VIN",
			"IMEI",
			"currentFirmware",
			"NIPVersion",
			"MIPVersion",	
			"latestFotaRequestTS",
			"FOTApktsdownloaded",
			"totalFOTApkts",
			"lastFOTApktdownloadedTS",	
			"HMRbeforeFota",
			"fotaFinalTimestamp",
			"resultCode",
			"latestFOTABINconfigured",
			"dateofFOTAConfiguration",
			"upgradedFirmwareAfterFOTA",
			"previousFirmware",
			"lastFOTACompletionDate",
			"HMRafterFota",
			"scriptLastUpdatedTime",
			"FOTASanityStatus"
			};
	public static ExecutorService executor;
	ArrayList<FotaStatusReportV2> fotaList=new ArrayList<> ();
	Connection conn_Wise3306= null,conn_Edgeproxy4306= null,connectionWiseTrace=null;
	Statement stmt_com_rep_oem = null,stmt_fota_history = null,stmt_fota_version = null,stmt_fotaReport_forVinList = null;
	ResultSet rs_com_rep_oem=null,rs_fota_version = null,rs_fota_history = null;
	ResultSet rs_fotaTrack=null,rs_upgradeHistory=null,rs_fotaReport_forVinList=null;
	Statement stmt_fotaTrack=null,stmt_wiseTrace_fotaUpgradeHistory=null;
	//to have upgardeHistory details with vin and other details
	HashMap<String,List<String>> fotaUpgradeHistoryHash=new HashMap<>();
	HashMap<String,List<String>> fota_TrackHash=new HashMap<>();
	HashMap<String,List<String>> fota_HistoryHash=new HashMap<>();
	HashMap<String,List<String>> fota_VersionHash=new HashMap<>();

	HashSet<String> fotaHistoryVinSet=new HashSet<String>();
	
	
	public String processCombineToFotaReport()
	{
		ConnectMySQL connectMySQL=new ConnectMySQL();
        conn_Wise3306=connectMySQL.getConnection();
		connectionWiseTrace=connectMySQL.getConnection_wise_traceability3306();
        conn_Edgeproxy4306=connectMySQL.getEdgeProxyNewConnection();
		
		/*conn_Wise3306=getConnectionWise3306();
		connectionWiseTrace=getConnectionWiseTrace3306();
        conn_Edgeproxy4306=getConnectionEdgeproxy4306();
        executor = Executors.newFixedThreadPool(100);*/


        try {
			if(conn_Wise3306==null||conn_Edgeproxy4306==null ||  connectionWiseTrace==null)
			{
				System.out.println("MYSQL connection not established properly . ");
				fLogger.error("MYSQL connection not established properly . ");
				return "FAILURE";
			}
			
				long startTime = System.currentTimeMillis(),endTime,startTime_basic=System.currentTimeMillis(),endtime_Basic;
				
				 
				 //vin Data to hashset from fota_report (to insert or update)
				 stmt_fotaReport_forVinList=connectionWiseTrace.createStatement();
				 rs_fotaReport_forVinList=stmt_fotaReport_forVinList.executeQuery("select VIN from fota_report;");
				 while(rs_fotaReport_forVinList.next())
				 {
					 fotaHistoryVinSet.add(rs_fotaReport_forVinList.getString("VIN"));
				 }
				 endtime_Basic=System.currentTimeMillis();
				 iLogger.info("FOTA_REPORT:Reading rs_fotaReport vin existence:"+fotaHistoryVinSet.size()+"; Turn around Time (in ms):"+(endtime_Basic-startTime_basic));
				 System.out.println("FOTA_REPORT:Reading rs_fotaReport vin existence:"+fotaHistoryVinSet.size()+"; Turn around Time (in ms):"+(endtime_Basic-startTime_basic));
				 startTime_basic=System.currentTimeMillis();
				 
				 
				 
				stmt_wiseTrace_fotaUpgradeHistory=connectionWiseTrace.createStatement();
				//JCB6290.n
				String fotaUpgradeQuery="SELECT any_value(f1.Serial_Number) as Serial_Number,any_value(f1.Transaction_Timestamp) as Transaction_Timestamp,any_value(f1.current_FW_VER) as current_FW_VER,any_value(f1.old_FW_VER) as old_FW_VER,any_value(f1.cmh) as cmh from fota_upgrade_history f1,(SELECT MAX(Transaction_Timestamp) as Transaction_Timestamp,Serial_Number FROM fota_upgrade_history group by Serial_Number) f2 where f1.Transaction_Timestamp=f2.Transaction_Timestamp and f2.Serial_Number=f1.Serial_Number group by f1.Serial_Number;";
				System.out.println(fotaUpgradeQuery);
				rs_upgradeHistory=stmt_wiseTrace_fotaUpgradeHistory.executeQuery(fotaUpgradeQuery);
				while(rs_upgradeHistory.next())
				 {
					 String vin=rs_upgradeHistory.getString("Serial_Number");
					 String current_FW_VER=rs_upgradeHistory.getString("current_FW_VER");
					 String previousFW_VER=rs_upgradeHistory.getString("old_FW_VER");
					 String transaction_Timestamp=rs_upgradeHistory.getString("Transaction_Timestamp");
					 Timestamp txntimestampInIST = new IstGmtTimeConversion().convertGmtToIst(transaction_Timestamp);
					 String stringtxntimestampInIST=sdf.format(new Date(txntimestampInIST.getTime()));
					 String cmh=rs_upgradeHistory.getString("cmh");
					 fotaUpgradeHistoryHash.put(vin,Arrays.asList(current_FW_VER,previousFW_VER,stringtxntimestampInIST,cmh)) ;
				 }
				 endtime_Basic=System.currentTimeMillis();
				 iLogger.info("FOTA_REPORT:Reading rs_upgradeHistory:"+fotaUpgradeHistoryHash.size()+"; Turn around Time (in ms):"+(endtime_Basic-startTime_basic));
				 System.out.println("FOTA_REPORT:Reading rs_upgradeHistory:"+fotaUpgradeHistoryHash.size()+"; Turn around Time (in ms):"+(endtime_Basic-startTime_basic));
				 startTime_basic=System.currentTimeMillis();
				
				 
				 
				 
				stmt_fotaTrack=conn_Edgeproxy4306.createStatement();
				rs_fotaTrack = stmt_fotaTrack.executeQuery("select *from edgeproxy.Fota_Track");
				while(rs_fotaTrack.next())
				 {
					String vin=rs_fotaTrack.getString("vin_no");
					String imei=rs_fotaTrack.getString("imei_number");
					String device_version=rs_fotaTrack.getString("device_version");
					String deviceMIP=rs_fotaTrack.getString("device_MIP_version");
					String deviceNIP=rs_fotaTrack.getString("device_NIP_version");
					String ep_timeStamp=rs_fotaTrack.getString("ep_timeStamp");
					String chunks_downloaded=rs_fotaTrack.getString("chunks_downloaded");
					String total_chunks=rs_fotaTrack.getString("total_chunks");
					String last_updated=rs_fotaTrack.getString("last_updated");
					fota_TrackHash.put(vin,Arrays.asList(imei,device_version,deviceMIP,deviceNIP,ep_timeStamp,chunks_downloaded,total_chunks,last_updated));
									
				 }
				 endtime_Basic=System.currentTimeMillis();
				 iLogger.info("FOTA_REPORT:Reading rs_fotaTrack:"+fota_TrackHash.size()+"; Turn around Time (in ms):"+(endtime_Basic-startTime_basic));
				 System.out.println("FOTA_REPORT:Reading rs_fotaTrack:"+fota_TrackHash.size()+"; Turn around Time (in ms):"+(endtime_Basic-startTime_basic));
				 startTime_basic=System.currentTimeMillis();
				stmt_fota_history = conn_Edgeproxy4306.createStatement();
				//JCB6290.sn any_value in the query
				String fota_historyQuery="select any_value(fh.vin_no) as vin_no,any_value(fh.sanity_received_TS) as sanity_received_TS,any_value(fh.HMR_before_fota) as HMR_before_fota,any_value(fh.fota_final_timestamp) as fota_final_timestamp,any_value(fh.integer_result_code) as integer_result_code from fota_history fh,(select max(generic_req_TS) as generic_req_TS,vin_no from fota_history  group by vin_no) as fh2 where  fh2.vin_no=fh.vin_no and fh.generic_req_TS=fh2.generic_req_TS group by fh.vin_no;";
				System.out.println(fota_historyQuery);
				rs_fota_history = stmt_fota_history.executeQuery(fota_historyQuery);
				while(rs_fota_history.next())
				 {
					String vin=rs_fota_history.getString("vin_no");
					String sanity=rs_fota_history.getString("sanity_received_TS");
					String HMR_before_fota=rs_fota_history.getString("HMR_before_fota");
					String fota_final_timestamp=rs_fota_history.getString("fota_final_timestamp");
					String resultCode=rs_fota_history.getString("integer_result_code");
				 
					fota_HistoryHash.put(vin, Arrays.asList(sanity,HMR_before_fota,fota_final_timestamp,resultCode));
				 }
				 endtime_Basic=System.currentTimeMillis();
				 iLogger.info("FOTA_REPORT:Reading rs_fota_history:"+fota_HistoryHash.size()+"; Turn around Time (in ms):"+(endtime_Basic-startTime_basic));
				 System.out.println("FOTA_REPORT:Reading rs_fota_history:"+fota_HistoryHash.size()+"; Turn around Time (in ms):"+(endtime_Basic-startTime_basic));
				 startTime_basic=System.currentTimeMillis();
				 //JCB6290.sn
				 String fotaVersionQuery= "select  any_value(f1.file_Path) as file_Path,any_value(f1.value) as value,any_value(f1.Version_Date) as Version_Date from fota_version f1,(select max(Version_Date) as Version_Date,value from fota_version where level='VIN' group by value) as f2  where  f1.level='VIN' and f1.value=f2.value and f1.Version_Date=f2.Version_Date group by f1.value;";
			     System.out.println(fotaVersionQuery);
				 stmt_fota_version = conn_Edgeproxy4306.createStatement();				 
				rs_fota_version= stmt_fota_version.executeQuery(fotaVersionQuery);
				while(rs_fota_version.next())
				 {
					//fota_version
					String vin=rs_fota_version.getString("value");
					String binConfigured=rs_fota_version.getString("File_Path");
					if(binConfigured!=null)
					{
						binConfigured=binConfigured.replaceAll("#", "");
					}
					String versionDate=rs_fota_version.getString("Version_Date");
					fota_VersionHash.put(vin,  Arrays.asList(binConfigured,versionDate));
				 }
				
				 endtime_Basic=System.currentTimeMillis();
				 iLogger.info("FOTA_REPORT:Reading rs_fota_history:"+fota_VersionHash.size()+"; Turn around Time (in ms):"+(endtime_Basic-startTime_basic));
				 System.out.println("FOTA_REPORT:Reading rs_fota_history:"+fota_VersionHash.size()+"; Turn around Time (in ms):"+(endtime_Basic-startTime_basic));
				
				
				stmt_com_rep_oem = conn_Wise3306.createStatement();
				rs_com_rep_oem = stmt_com_rep_oem.executeQuery("select * from asset where Status=1");
		 		 
				while(rs_com_rep_oem.next()){
					//Fota_Track
					
					 String serialNumber = rs_com_rep_oem.getString("Serial_Number");
					 FotaStatusReportV2 fotaStatusReport=new FotaStatusReportV2();
					fotaStatusReport.setVIN(serialNumber);//Serial_Number
					if(fota_TrackHash.containsKey(serialNumber))
					{
						//0          1            2        3             4            5              6            7
						//imei,device_version,deviceMIP,deviceNIP,ep_timeStamp,chunks_downloaded,total_chunks,last_updated
						List<String> list=fota_TrackHash.get(serialNumber);
						fotaStatusReport.setIMEI(list.get(0));
						fotaStatusReport.setCurrentFirmware(list.get(1));
						fotaStatusReport.setMIPVersion(list.get(2));
						fotaStatusReport.setNIPVersion(list.get(3));
						fotaStatusReport.setLatestFotaRequestTS(list.get(4));
						fotaStatusReport.setFOTApktsdownloaded(list.get(5));
						fotaStatusReport.setTotalFOTApkts(list.get(6));
						fotaStatusReport.setLastFOTApktdownloadedTS(list.get(7));	
					}
					
					if(fota_HistoryHash.containsKey(serialNumber))
					{
						// 0            1             2                    3
						//sanity,HMR_before_fota,fota_final_timestamp,resultCode
						List<String> list=fota_HistoryHash.get(serialNumber);
						fotaStatusReport.setfOTASanityStatus(list.get(0));
						fotaStatusReport.setHMRbeforeFota(list.get(1));
						fotaStatusReport.setFotaFinalTimestamp(list.get(2));
						fotaStatusReport.setResultCode(list.get(3));
					 }
					 stmt_fota_version = conn_Edgeproxy4306.createStatement();	
					
					if(fota_VersionHash.containsKey(serialNumber))
					{
						List<String> list=fota_VersionHash.get(serialNumber);
						fotaStatusReport.setLatestFOTABINconfigured(list.get(0));
						fotaStatusReport.setDateofFOTAConfiguration(list.get(1));
					}
					if(fotaUpgradeHistoryHash.containsKey(serialNumber))
					{
						fotaStatusReport.setUpgradedFirmwareAfterFOTA(fotaUpgradeHistoryHash.get(serialNumber).get(0));
						fotaStatusReport.setPreviousFirmware(fotaUpgradeHistoryHash.get(serialNumber).get(1));
						fotaStatusReport.setLastFOTACompletionDate(fotaUpgradeHistoryHash.get(serialNumber).get(2));
						fotaStatusReport.setHMRafterFota(fotaUpgradeHistoryHash.get(serialNumber).get(3));
					}
					String scriptLastUpdatedTime =sdf.format(new Date());
					fotaStatusReport.setScriptLastUpdatedTime(scriptLastUpdatedTime);
					fotaList.add(fotaStatusReport);
					//System.out.println(fotaStatusReport.toString());
				}
				endTime = System.currentTimeMillis();
				iLogger.info("FOTA_REPORT:Reading totalRecords:"+fotaList.size()+"; Turn around Time (in ms):"+(endTime-startTime));
				System.out.println("FOTA_REPORT:Reading totalRecords:"+fotaList.size()+"; Turn around Time (in ms):"+(endTime-startTime));
			try { conn_Wise3306.close(); } catch (Exception e) { /* Ignored */ }
			try { conn_Edgeproxy4306.close(); } catch (Exception e) { /* Ignored */ }
		insertInfoFotaReport(fotaList);			
			
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
			try { stmt_fota_version.close(); } catch (Exception e) { /* Ignored */ }
			try { stmt_fota_history.close(); } catch (Exception e) { /* Ignored */ }
			try { stmt_fotaTrack.close(); } catch (Exception e) { /* Ignored */ }
			try { stmt_fotaReport_forVinList.close(); } catch (Exception e) { /* Ignored */ }
			try { stmt_wiseTrace_fotaUpgradeHistory.close(); } catch (Exception e) { /* Ignored */ }
			
			
			try { rs_com_rep_oem.close(); } catch (Exception e) { /* Ignored */ }
			try { rs_fota_version.close(); } catch (Exception e) { /* Ignored */ }
			try {rs_fota_history.close(); } catch (Exception e) { /* Ignored */ }	
			try {rs_fotaTrack.close(); } catch (Exception e) { /* Ignored */ }	
			try {rs_upgradeHistory.close(); } catch (Exception e) { /* Ignored */ }	
			try {rs_fotaReport_forVinList.close(); } catch (Exception e) { /* Ignored */ }	
			
			
		}
        return "SUCCESS";
	}
	public void insertInfoFotaReport(ArrayList<FotaStatusReportV2> list)
	{
		try (Statement statementWiseTrace = connectionWiseTrace.createStatement()) 
		{
			long startTime = System.currentTimeMillis(),
			startTime_batch = System.currentTimeMillis(),endTime_batch;
			int batchCount=0;
			for(FotaStatusReportV2 fr:list)
			{
				ArrayList<String> arrayListData=new ArrayList<>();
				arrayListData.add(nullCheck(fr.getVIN()));
				arrayListData.add(nullCheck(fr.getIMEI()));
				arrayListData.add(nullCheck(fr.getCurrentFirmware()));
				arrayListData.add(nullCheck(fr.getNIPVersion()));
				arrayListData.add(nullCheck(fr.getMIPVersion()));
				arrayListData.add(nullCheck(fr.getLatestFotaRequestTS()));
				arrayListData.add(nullCheck(fr.getFOTApktsdownloaded()));
				arrayListData.add(nullCheck(fr.getTotalFOTApkts()));
				arrayListData.add(nullCheck(fr.getLastFOTApktdownloadedTS()));	
				arrayListData.add(nullCheck(fr.getHMRbeforeFota()));
				arrayListData.add(nullCheck(fr.getFotaFinalTimestamp()));
				arrayListData.add(nullCheck(fr.getResultCode()));
				arrayListData.add(nullCheck(fr.getLatestFOTABINconfigured()));
				arrayListData.add(nullCheck(fr.getDateofFOTAConfiguration()));
				arrayListData.add(nullCheck(fr.getUpgradedFirmwareAfterFOTA()));
				arrayListData.add(nullCheck(fr.getPreviousFirmware()));
				arrayListData.add(nullCheck(fr.getLastFOTACompletionDate()));
				arrayListData.add(nullCheck(fr.getHMRafterFota()));
				arrayListData.add(nullCheck(fr.getScriptLastUpdatedTime()));
				arrayListData.add(nullCheck(fr.getFOTASanityStatus()));
				StringBuffer query=null;
				if(!fotaHistoryVinSet.contains(fr.getVIN()))
				{
					query=new StringBuffer("INSERT INTO fota_report (");
					StringBuffer keysQueryString=new StringBuffer("");
					for(int i=0;i<fotalist.length;++i)
					{
						keysQueryString.append(fotalist[i]+",");
					}
					keysQueryString.deleteCharAt(keysQueryString.length()-1);
					keysQueryString.append(") values (");
					query.append(keysQueryString);
					keysQueryString.setLength(0);;
					for(int i=0;i<arrayListData.size();++i)
					{
						keysQueryString.append("'"+arrayListData.get(i)+"',");
					}
					keysQueryString.setCharAt(keysQueryString.length()-1,')');
					query.append(keysQueryString);
				}
				else
				{
						query=new StringBuffer("UPDATE fota_report  SET ");

						for(int i=1;i<arrayListData.size();++i)
						{
							query.append(fotalist[i]+"='"+arrayListData.get(i)+"',");
						}
						query.setCharAt(query.length()-1,' ');
						query.append("where "+fotalist[0]+"='"+arrayListData.get(0)+"'");
				}
				//System.out.println(query.toString());
				statementWiseTrace.addBatch(query.toString());
				batchCount++;
				if(batchCount%MAX_BATCH_COUNT==0)
				{
					endTime_batch = System.currentTimeMillis();
					insertCount = statementWiseTrace.executeBatch();
					totalInsertedCount += Arrays.stream(insertCount).sum();
					iLogger.info("FOTA_REPORT:insertInfoFotaReport:Inserted Count:" + Arrays.stream(insertCount).sum()+" : totalTime:"+(endTime_batch-startTime_batch));
					System.out.println("FOTA_REPORT:insertInfoFotaReport:Inserted Count:" + Arrays.stream(insertCount).sum()+" : totalTime:"+(endTime_batch-startTime_batch));
					startTime_batch = System.currentTimeMillis();
				}
				
			}
			if(list.size()%MAX_BATCH_COUNT!=0)
			{
			
				insertCount = statementWiseTrace.executeBatch();
				totalInsertedCount += Arrays.stream(insertCount).sum();
				iLogger.info("FOTA_REPORT:insertInfoFotaReport:Inserted Count:" + Arrays.stream(insertCount).sum());
				System.out.println("FOTA_REPORT:insertInfoFotaReport:Inserted Count:" + Arrays.stream(insertCount).sum());
				
			}
			long endTime = System.currentTimeMillis();
			iLogger.info("FOTA_REPORT:Writing totalRecords:"+fotaList.size()+";totalInsertedCount:"+totalInsertedCount+"; Turn around Time (in ms):"+(endTime-startTime));
			System.out.println("FOTA_REPORT:Writing totalRecords:"+fotaList.size()+";totalInsertedCount:"+totalInsertedCount+"; Turn around Time (in ms):"+(endTime-startTime));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("FOTA_REPORT:insertInfoFotaReport:Exception in inserting record to table:fota_report::"
					+ e.getMessage());
		}
		
		//System.out.println(arrayListData.toString());
		
	}
	
	public String nullCheck(String str)
	{
		if(str==null)return "";
		if(str!=null&&str.equals("null"))return "";
		return str;
	}
	
	
	
}
//CR321V2.en