package remote.wise.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
/*
CR344 : VidyaSagarM : 20220817 : UnknownDTCFetchData from mysql 
CR344.sn
 */
public class UnknownDTCFetchData {

	
	public UnknownDTCFetchData()
	{
    executor = Executors.newFixedThreadPool(100);
	}
	public static ExecutorService executor;

	private  ConnectMySQL connectmysql=null;
	private static Logger fLogger = FatalLoggerClass.logger;
	private static Logger iLogger = InfoLoggerClass.logger;
	private  Connection conn_Wise3306= null,connectionWiseTrace=null;
	private  Statement stmt_com_rep_oem = null, stmtWiseTrace = null;
	private  ResultSet rs_com_rep_oem=null,rs_tWiseTrace = null;
		public void fetchComEnhancedData(String vin)
	{
		
	}
	 class ComRepoData implements Callable<UnknownDTCAlertReportRespContract>
	{
		 private String serial_Number;
		 private String model,profile;
		 private UnknownDTCAlertReportRespContract dtcnknownreport;
		 public ComRepoData(String serial_Number, UnknownDTCAlertReportRespContract dtcnknownreport,String model,String profile)
		 
		 {
			 this.serial_Number=serial_Number;
			 this.dtcnknownreport=dtcnknownreport;
			 this.model=model;
			 this.profile=profile;
		 }
		 @Override
			public UnknownDTCAlertReportRespContract call() throws Exception {
				// TODO Auto-generated method stub
			 dtcnknownreport=getDTCUnknownReportFromComRepOemEnhanced(serial_Number,dtcnknownreport,model,profile);
				return dtcnknownreport;
			}
	
			
	public  UnknownDTCAlertReportRespContract getDTCUnknownReportFromComRepOemEnhanced (String Serial_Number, UnknownDTCAlertReportRespContract dtcnknownreport, String models, String profiles) 
	{
	
		//conn_Wise3306=getConnectionWise3306();
		if(conn_Wise3306==null)
		{
			return null;
		}

		try
		{
			stmt_com_rep_oem = conn_Wise3306.createStatement();
			 StringBuffer query=new StringBuffer("");
			 query.append("select * from wise.com_rep_oem_enhanced where Serial_Number='"+Serial_Number+"'");

			if(models!=null)
			{
				//modelCodeList=JCB220,JS220,Z220 query adding to have '' for each model
				String modellist="'"+models.replaceAll(",","','")+"'";
				query.append(" and ModelMasterCode in ("+modellist+") ");
			}
			if(profiles!=null)
			{
				String profilelist="'"+profiles.replaceAll(",","','")+"'";
				query.append(" and ProfileMasterCode  in ("+profilelist+") ");
			}
			//System.out.println("The com_rep_oem_enhanced query:"+query);
			rs_com_rep_oem = stmt_com_rep_oem.executeQuery(query.toString());

		
			boolean isEmptyData=false;
			while(rs_com_rep_oem.next()){
				isEmptyData=true;
				dtcnknownreport.DealerName=rs_com_rep_oem.getString("Dealer_Name");
				dtcnknownreport.CustomerName=rs_com_rep_oem.getString("CustomerName");
				dtcnknownreport.ModelName=rs_com_rep_oem.getString("Model");
				dtcnknownreport.ProfileName=rs_com_rep_oem.getString("Profile");
				dtcnknownreport.ZonalName=rs_com_rep_oem.getString("Zone");
				dtcnknownreport.isValidModelOrProfile=true;
				return dtcnknownreport;
				
				
			}
			
			if(!isEmptyData)
			{
				dtcnknownreport.isValidModelOrProfile=false;
				return dtcnknownreport;
			}
			

		}catch(Exception e)
		{
			fLogger.error(Serial_Number+" : error in getDTCUnknownReportFromComRepOemEnhanced ",e);
		}
		return null;
		
	}
	
	}
	 
	public ArrayList<UnknownDTCAlertReportRespContract> getUnknownDTCReport(StringBuffer query,String models,String profiles) 
	{
		connectmysql=new ConnectMySQL();
	    HashSet<String> vinNumberNotValidCache=new HashSet<String>();  
	    HashMap<String,UnknownDTCAlertReportRespContract> comRepoEnhancedCache=new HashMap<String,UnknownDTCAlertReportRespContract>();  
	
		ArrayList<UnknownDTCAlertReportRespContract> listOfUnknownDTCCodes=new ArrayList<UnknownDTCAlertReportRespContract> ();
		connectionWiseTrace=connectmysql.getConnection_wise_traceability3306();
		conn_Wise3306=connectmysql.getConnection();
		//connectionWiseTrace=getConnectionWiseTrace3306();
		if( connectionWiseTrace==null )
		{
			return null;
		}

		try
		{
			//iLogger.info(Serial_Number+" : getDTCUnknownReportFromComRepOemEnhanced query : "+query);

			 stmtWiseTrace = connectionWiseTrace.createStatement();
			rs_tWiseTrace = stmtWiseTrace.executeQuery(query.toString());

		
			while(rs_tWiseTrace.next()){
				UnknownDTCAlertReportRespContract dtcnknownreport=new UnknownDTCAlertReportRespContract();

				 dtcnknownreport.AssetID=rs_tWiseTrace.getString("AssetID");
				 if(vinNumberNotValidCache.contains(dtcnknownreport.AssetID))
				 {
					 //System.out.println("skipping the check as that vin not having model ,profile ");
					 continue;
				 }
				 //if we are already having that vin  in com_rep_oem_enhanced  we get from local hashmap 
				 if(comRepoEnhancedCache.containsKey(dtcnknownreport.AssetID))
				 {
					 UnknownDTCAlertReportRespContract dtcnknownreportFromHash=comRepoEnhancedCache.get(dtcnknownreport.AssetID);
					 dtcnknownreport.AssetID=dtcnknownreportFromHash.AssetID;
					 dtcnknownreport.DealerName=dtcnknownreportFromHash.DealerName;
					 dtcnknownreport.CustomerName=dtcnknownreportFromHash.CustomerName;
					 dtcnknownreport.ModelName=dtcnknownreportFromHash.ModelName;
					 dtcnknownreport.ProfileName=dtcnknownreportFromHash.ProfileName;
					 dtcnknownreport.ZonalName=dtcnknownreportFromHash.ZonalName;
					 dtcnknownreport.isValidModelOrProfile=true;
				 }
				 else
				 {
					 ComRepoData comrepocallable= new ComRepoData(dtcnknownreport.AssetID,dtcnknownreport,models,profiles);
					Future<UnknownDTCAlertReportRespContract> submitData =executor.submit(comrepocallable);
					dtcnknownreport=submitData.get();
				 }
				//IF that vin is not registered to selected model or profile then not going to add to list
				if(!(dtcnknownreport.isValidModelOrProfile))
				{
					// System.out.println("is not valid  model ,profile ");
					vinNumberNotValidCache.add(dtcnknownreport.AssetID);
					continue;
				}
				
				//so adding to comRepoEnhancedCache if not exist
				if(!comRepoEnhancedCache.containsKey(dtcnknownreport.AssetID))
				{
					comRepoEnhancedCache.put(dtcnknownreport.AssetID,dtcnknownreport);
				}
				 dtcnknownreport.Machine_Hours=rs_tWiseTrace.getString("Machine_Hours");
				 dtcnknownreport.DTCAlertCode=rs_tWiseTrace.getString("DTCAlertCode");
				 dtcnknownreport.SPN=rs_tWiseTrace.getString("SPN");
				 dtcnknownreport.FMI=rs_tWiseTrace.getString("FMI");
				 dtcnknownreport.SourceAddress_Decimal=rs_tWiseTrace.getString("SourceAddress_Decimal");
				 dtcnknownreport.SourceAddress_Hexadecimal=rs_tWiseTrace.getString("SourceAddress_Hexadecimal");
				 dtcnknownreport.DtcAlertGenerationTime=rs_tWiseTrace.getString("DTC_Generated_Timestamp");
				 dtcnknownreport.DTC_status=rs_tWiseTrace.getString("DTC_status");
				 dtcnknownreport.Location=rs_tWiseTrace.getString("Location");
				 listOfUnknownDTCCodes.add(dtcnknownreport);
				
			}
			
			iLogger.info(" UnknownDTCFetchData: getUnknownDTCReport is completed ");


		}catch(Exception e)
		{
			fLogger.error(query+" : error in UnknownDTCFetchData ",e);
		}
		finally
		{
					
			try { conn_Wise3306.close(); } catch (Exception e) { /* Ignored */ }
			try { connectionWiseTrace.close(); } catch (Exception e) { /* Ignored */ }
			try { stmt_com_rep_oem.close(); } catch (Exception e) { /* Ignored */ }
			try { stmtWiseTrace.close(); } catch (Exception e) { /* Ignored */ }
			try { rs_com_rep_oem.close(); } catch (Exception e) { /* Ignored */ }
			try { rs_tWiseTrace.close(); } catch (Exception e) { /* Ignored */ }
		}
		return listOfUnknownDTCCodes;
	}
}

//CR344.en