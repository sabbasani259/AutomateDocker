/**
 * 
 */
package remote.wise.businessobject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import remote.wise.dal.DynamicAMS_Doc_DAL;
import remote.wise.dal.DynamicRemoteFactDay_DAL;
import remote.wise.dal.DynamicTAMD_DAL;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AMSDoc_DAO;
import remote.wise.pojo.AmhDAO;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.businessentity.AssetMonitoringFactDataEntity;

/**
 * @author ROOPN5
 *
 */
public class ETLfactDataJsonBOExtended implements Runnable{

	Thread t;
	String serial_number;
	Timestamp maxOlapDt;
	int segment_ID;
	String transactionTable;
	Timestamp nextday;
	String Time_Key;

	int tenancy_dimension_id_json=0;
	int acc_id_json=0;
	int asset_class_id_json=0;
	String NickName= null;
	String manualOlapDate;

	SimpleDateFormat simpleDtStr = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");
	DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd");

	List<String> aggregateParameters;
	List<String> accumulatedParameters;
	Map common_parameters_map;

	String regionCode = null;
	String regionName = null;
	String customerCode=null;
	String CustomerName=null;
	String dealerCode=null;
	String DealerName=null;
	String ZonalCode=null;
	String ZonalName=null;
	String PrimaryOwner=null;
	String Customer_Number=null;
	String Dealer_Number=null;

	public ETLfactDataJsonBOExtended() {

	}

	public ETLfactDataJsonBOExtended(String Serial_Number,
			Timestamp maxOlapDt, int seg_ID,String transactionTable, Timestamp nextday, String Time_Key,List<String> aggregateParameters, List<String> accumulatedParameters, Map common_parameters_map, String manualOlapDate) {


		t= new Thread(this, "TableSpecific Call");
		this.serial_number=Serial_Number;
		this.maxOlapDt=maxOlapDt;
		this.segment_ID=seg_ID;
		this.transactionTable=transactionTable;
		this.nextday=nextday;
		this.Time_Key=Time_Key;

		this.aggregateParameters = aggregateParameters;
		this.accumulatedParameters = accumulatedParameters;
		this.common_parameters_map = common_parameters_map;
		this.manualOlapDate=manualOlapDate;

		t.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		processTableSpecificData();
	}

	public String processTableSpecificData(){

		String response="SUCCESS";

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;



		long startTime = 0l;
		long endTime = 0l;
		List lst = null;
		Iterator itr = null;

		int count2Process = 0;
		String vinIdentified = null;
		String vin2Process = "";
		String vinProcessed = "";
		Timestamp tranTime = null;
		Timestamp lastOlapDt = null;


		DynamicTAMD_DAL dalObject = new DynamicTAMD_DAL();

		startTime = System.currentTimeMillis();
		lst = dalObject.getAMhDataOnCreatedTS(serial_number, maxOlapDt, segment_ID, transactionTable, nextday);
		endTime = System.currentTimeMillis();
		iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+serial_number+" setETL STEP 5 AMHTables executed  getAMhDataOnCreatedTS in :"+(endTime - startTime)+" ms");
		if(lst == null || lst.size()==0)
		{
			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+serial_number+" setETL STEP 5 AMHTables after calling getAMhDataOnCreatedTS size: 0");
			return response;
		}

		itr = lst.iterator();


		iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+serial_number+" setETL STEP 5 AMHTables after calling getAMhDataOnCreatedTS size:"+lst.size());




		count2Process = lst.size();

		endTime = System.currentTimeMillis();

		iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN " + serial_number+" has "+count2Process+" records");

		iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN Get distinct transaction dates for the given SerialNumber "+serial_number+" query executed time in ms: "
				+ (endTime - startTime));

		while (itr.hasNext()) 
		{
			//S Suresh DAL Layer inclusion

			try
			{
				//

				AmhDAO AmhDaoObject = (AmhDAO)itr.next(); 
				vinIdentified = AmhDaoObject.getSerial_Number();

				tranTime = AmhDaoObject.getTransaction_Timestamp();

				iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+vinIdentified+" on TransTS "+tranTime+" insertOlapNew before  getEngineRunningBands");
				vin2Process = vinIdentified;

				startTime = System.currentTimeMillis();

				//DF20170516 @Roopa Query tweaking to get the VIN specific information(Removing hibernate)

				getVINspecificinfo(vin2Process, tranTime);

				endTime = System.currentTimeMillis();
				iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+vin2Process+" Get VIN specific data for "
						+ vin2Process
						+ " Time in ms: "
						+ (endTime - startTime));



				if ( !(vin2Process.equalsIgnoreCase(vinProcessed)) ) {
					//PreviousDayBandList.clear();

					if (Time_Key != null && Time_Key.length()!=0)  {
						lastOlapDt=	Timestamp.valueOf(Time_Key);
					}

					else{

						Calendar cal1  = Calendar.getInstance();
						cal1.setTime(tranTime);
						cal1.add(Calendar.DATE, -1);
						//set last OLAP date to 1 day previous to txn date so that it goes to the insertion block
						lastOlapDt = new Timestamp(cal1.getTime().getTime());

						iLogger.info("ETLfactDataJsonBO: VIN Communicating for the First Time after OLAP Hit. lastOlapDt modified to :"+lastOlapDt);
					}


				}


				//DF20170517 @Roopa handling updates of any date(removing static check of updating only previous day updates + handling first time communicating machines)
				//if ( lastOlapDt.before(tranTime) || (lastOlapDt.equals(tranTime)) ) {
				startTime = System.currentTimeMillis();

				if (manualOlapDate.equalsIgnoreCase("false") && (lastOlapDt.after(tranTime) || ((dateStr.format(lastOlapDt)).equalsIgnoreCase(dateStr.format(tranTime)))) ) {
					//if ((dateStr.format(lastOlapDt)).equalsIgnoreCase(dateStr.format(tranTime)) ) {	
					iLogger.info("ETLfactDataJsonBO: VIN for update"
							+ vin2Process
							+ " Tran Dt: " 
							+ dateStr.format(tranTime)
							+ " Last Olap Date: "
							+ dateStr.format(lastOlapDt));
					//PreviousDayBandList.clear();

					insertOlapUsingJSON(vin2Process, tranTime, lastOlapDt, lastOlapDt, true,segment_ID);
				} else {
					iLogger.info("ETLfactDataJsonBO: VIN for insert"
							+ vin2Process
							+ " Tran Dt: " 
							+ dateStr.format(tranTime)
							+ " Last Olap Date: "
							+ dateStr.format(lastOlapDt));

					insertOlapUsingJSON(vin2Process, tranTime, lastOlapDt, lastOlapDt, false,segment_ID);					
				}

				vinProcessed = vin2Process;
				endTime = System.currentTimeMillis();
				iLogger.info("ETLfactDataJsonBO: VIN Processed"
						+ vinProcessed
						+ " Tran Time: " 
						+ tranTime
						+ " Time in ms: "									+ (endTime - startTime)
						+ " | "
						+ simpleDtStr.format(Calendar.getInstance()
								.getTime()));	
				/*} else {
					vinProcessed = vin2Process;
					endTime = System.currentTimeMillis();
					iLogger.info("ETLfactDataJsonBO: VIN / Tran skipped"
							+ vinProcessed
							+ " Tran Time: " 
							+ tranTime
							+ " Last Olap Dt: " 
							+ lastOlapDt
							+ " Time in ms: "
							+ (endTime - startTime)
							+ " | "
							+ simpleDtStr.format(Calendar.getInstance()
									.getTime()));
				}*/

			} // End of Try block inside while

			//DF20141211 - Rajani Nagaraju - Continue with next set of VINs if the exception is thrown for a VIN - Hence try-catch block
			catch(Exception e) 
			{
				fLogger.fatal("ETLfactDataJsonBO: setETLfactData inner catch Fatal error processing VIN: "
						+ vin2Process + " " + tranTime + " "
						+ " Message: "
						+ e.getMessage());

			}


		} // End of while loop for itrVin2Process.hasNext()



		return response;
	}

	//DF20170516 @Roopa query performance improvement to get vin specific info

	public void getVINspecificinfo(String Serial,Timestamp runDt) {

		Logger fLogger = FatalLoggerClass.logger;

		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		String assetOwnersQuery=null;
		String dimensionsQuery=null;

		int account_id_assetowner=0;

		int account_id_final=0;
		String aosQuery = null;

		ConnectMySQL connMySql = new ConnectMySQL();

		String account_type=null;
		Date ownershipStartDate=null;
		
		Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
		Matcher match;

		try{
			prodConnection = connMySql.getETLConnection();

			aosQuery ="select aos.account_type,aos.Ownership_Start_Date,a.Account_Name, a.Account_Code,a.Mobile,a.mapping_code from account a,asset_owner_snapshot aos where aos.Serial_Number='"+Serial+"' and aos.Account_ID=a.Account_ID order by aos.ownership_start_date";
			//iLogger.info("AOS Query: "+aosQuery);

			statement = prodConnection.createStatement();
			rs = statement.executeQuery(aosQuery);
			while(rs.next()){
				account_type=rs.getString("account_type");
				ownershipStartDate=rs.getDate("Ownership_Start_Date");
				if(account_type!=null){
					if(account_type.equalsIgnoreCase("OEM") && ownershipStartDate.before(runDt)){
						regionName = rs.getString("Account_Name");
						  match= pt.matcher(regionName);
					        while(match.find())
					        {
					            String s= match.group();
					            regionName=regionName.replaceAll("\\"+s, "");
					        }
						regionCode = rs.getString("Account_Code");	
					}
					if(account_type.equalsIgnoreCase("OEM RO") && ownershipStartDate.before(runDt)){
						ZonalName = rs.getString("Account_Name");
						  match= pt.matcher(ZonalName);
					        while(match.find())
					        {
					            String s= match.group();
					            ZonalName=ZonalName.replaceAll("\\"+s, "");
					        }
						ZonalCode = rs.getString("Account_Code");
					}
					if(account_type.equalsIgnoreCase("Dealer") && ownershipStartDate.before(runDt)){
						DealerName = rs.getString("Account_Name");
						 match= pt.matcher(DealerName);
					        while(match.find())
					        {
					            String s= match.group();
					            DealerName=DealerName.replaceAll("\\"+s, "");
					        }
						dealerCode = rs.getString("Account_Code");
						Dealer_Number=rs.getString("Mobile");
					}
					if(account_type.equalsIgnoreCase("Customer") && ownershipStartDate.before(runDt)){
						CustomerName = rs.getString("Account_Name");
						match= pt.matcher(CustomerName);
				        while(match.find())
				        {
				            String s= match.group();
				            CustomerName=CustomerName.replaceAll("\\"+s, "");
				        }
						//customerCode = rs.getString("Account_Code");
				        //Df20180124 @Roopa Multiple BP code changes-sending mapping code instead of account code
				        customerCode = rs.getString("mapping_code");
						Customer_Number=rs.getString("Mobile");
					}
					if(account_type.equalsIgnoreCase("Customer"))
					PrimaryOwner=rs.getString("Account_Code");
					else
						PrimaryOwner=rs.getString("mapping_code");
				}

			}

			assetOwnersQuery="select a.primary_owner_id, b.Account_ID from (select primary_owner_id from asset where serial_number='"+Serial+"')a left outer join (select Account_ID from wise.asset_owners where serial_number='"+Serial+"' and Ownership_Start_Date=(SELECT max(Ownership_Start_Date) FROM wise.asset_owners where serial_number='"+Serial+"' and  Ownership_Start_Date <='"+runDt+"')) b on a.primary_owner_id=b.Account_ID";

			//ConnectMySQL connMySql = new ConnectMySQL();
			//prodConnection = connMySql.getETLConnection();
			//statement = prodConnection.createStatement();
			rs = statement.executeQuery(assetOwnersQuery);


			while(rs.next()){
				account_id_final=rs.getInt("primary_owner_id");
				account_id_assetowner=rs.getInt("Account_ID");
			}

			if(account_id_assetowner!=0){
				account_id_final=account_id_assetowner;	
			}

			if(account_id_final==0){
				return;
			}

			//dimensionsQuery="SELECT ad.Account_Dimension_ID,td.Tenancy_Dimension_ID,ac.Asset_Class_Dimension_ID,a.Engine_Number FROM account_dimension ad, asset_class_dimension ac,tenancy_dimension td, asset a where ad.Account_ID="+account_id_final+" and td.Tenancy_ID=ad.Tenancy_ID and a.serial_number='"+Serial+"' and ac.Product_ID=a.Product_ID";

			//DF20170519 @Roopa getting the result even either of the table has the records
			dimensionsQuery="SELECT a.Account_Dimension_ID,a.Tenancy_Dimension_ID,b.Asset_Class_Dimension_ID,b.Engine_Number FROM (select ad.account_id, ad.Account_Dimension_ID,td.Tenancy_Dimension_ID from account_dimension ad, tenancy_dimension td where ad.Account_ID="+account_id_final+" and td.Tenancy_ID=ad.Tenancy_ID)a left outer join (select a.primary_owner_id,a.Engine_Number, ac.Asset_Class_Dimension_ID from asset_class_dimension ac, asset a where a.serial_number='"+Serial+"' and ac.Product_ID=a.Product_ID)b ON a.account_id=b.primary_owner_id union SELECT a.Account_Dimension_ID,a.Tenancy_Dimension_ID,b.Asset_Class_Dimension_ID,b.Engine_Number FROM (select ad.account_id, ad.Account_Dimension_ID,td.Tenancy_Dimension_ID from account_dimension ad, tenancy_dimension td where ad.Account_ID="+account_id_final+" and td.Tenancy_ID=ad.Tenancy_ID)a right outer join (select a.primary_owner_id,a.Engine_Number, ac.Asset_Class_Dimension_ID from asset_class_dimension ac, asset a where a.serial_number='"+Serial+"' and ac.Product_ID=a.Product_ID)b ON a.account_id=b.primary_owner_id";


			rs = statement.executeQuery(dimensionsQuery);

			while(rs.next()){
				acc_id_json=rs.getInt("Account_Dimension_ID");
				tenancy_dimension_id_json=rs.getInt("Tenancy_Dimension_ID");
				asset_class_id_json=rs.getInt("Asset_Class_Dimension_ID");
				NickName=rs.getString("Engine_Number");

			}

		} 

		catch (SQLException e) {

			fLogger.fatal("ETLfactDataJsonBO: SQL error getting VIN data: "
					+ Serial
					+ "Message: "
					+ e.getMessage());
			e.printStackTrace();
		} 

		catch(Exception e)
		{
			fLogger.fatal("ETLfactDataJsonBO: Fatal error getting VIN data: "
					+ Serial
					+ "Message: "
					+ e.getMessage());
			e.printStackTrace();
		}

		finally {
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
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
	}

	public void insertOlapUsingJSON(String Serial,
			Timestamp tranTime,
			Timestamp lastOlapDt,
			Timestamp prevlastOlapDt,
			boolean update,int segmentId) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		int tranNo = 0;
		long startTime = 0l;
		long endTime = 0l;

		double FuelIdle = 0.0;
		double FuelWorking = 0.0;
		double TotalFuelUsed = 0.0;
		double MachineHours = 0.0;
		double EngineOffHours = 0.0;

		String Longitude = null;
		String Latitude = null;
		//DefectId:2015-01-05 @Suprava 
		String engine_Status =null;
		List<Double> EngineRunningBandList = new LinkedList<Double>();

		Timestamp Last_Engine_Run = null;
		Timestamp minCreatedDt = null;
		Timestamp maxCreatedDt = null;
		Object result[] = null;
		Map new_aggregated_parameter_map = new HashMap();;
		Map commom_aggregated_parameter_map = new HashMap();;
		Map new_accumulated_parameter_map = new HashMap();;
		Map commom_accumulated_parameter_map = new HashMap();;



		try
		{
			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" insertOlapNew start ");
			EngineRunningBandList.clear();
			//	Keerthi : rounding engine off hours to 2 places : 10/03/14
			DecimalFormat df2 = new DecimalFormat("###.##");


			startTime = System.currentTimeMillis();

			String transaction_timestamp =dateStr.format(tranTime);
			//S Suresh  

			//instead of query, calling the DAL layer method to do the same job 
			//as we have seperated the database operations from  business logic 

			//DynamicAMH_DAL amhDAL = new DynamicAMH_DAL();

			//S Suresh  
			//get the partitioned segementID which is combination of segmentID of serialNumber and transaction data
			//eg: 120160617 which is a partition key of all the dynamic tables amh,amd and amde 
			//if we want use the functionality of partitioning we have to query the partitioned table on partition table
			//other wise there is no use of partitioning so thats why getting the partitioned key of that serialnumber on that trasanctional date
			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" insertOlapNew before  getEngineRunningBands");
			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" on TransTS "+transaction_timestamp+" insertOlapNew before  getEngineRunningBands");
			int seg_ID = new CommonUtil().convertSegment_TxnDate(segmentId+"", transaction_timestamp);
			startTime = System.currentTimeMillis();
			//EngineRunningBandList = amhDAL.getEngineRunningBands(Serial, tranTime, seg_ID);

			//S Suresh  

			/* passing two extra parameters aggregateParameters,common_parameters_map which got received from the constructor 
			 *  new ETLfactDataJsonBO(segementIDThread,maxOlapDate,aggregateParameters,accumulatedParameters,common_parameters_map  )
			 *  since the Transaction table data population has chenged so as ETLDesign has changed according to the new Transaction table approach
			 *  aggregateParameters - is a Map which has newly addded Aggregated Parameters which can be used in reports 
			 *  accumulatedParameters - is a Map which has newly addded Accumulated Parameters which can be used in reports 
			 *  common_parameters_map - is map which has both common Aggregated and Accumulated parameter list 
			 *  new coloumn has added in the remote__fact_day_agg which is a json datatype for newly added parameters (both aggregated and accumulated parameters)
			 *  so we follow the same old approach for ETL population inaddition we create a json for new parameters and store it in new column along with old paramters 
			 */

			Map aggregated_txn_data_map = new DynamicTAMD_DAL().getAggregatedDataOnTxnDate(Serial, tranTime, seg_ID, aggregateParameters, common_parameters_map, accumulatedParameters);

			//aggregated_txn_data_map is a TxnData map which has both new aggregated and common aggregated values for all parameters is stored with 
			//keys New_Aggregated , Common 
			new_aggregated_parameter_map = (Map)aggregated_txn_data_map.get("New_Aggregated");

			new_accumulated_parameter_map = (Map)aggregated_txn_data_map.get("New_Accumulated");


			commom_aggregated_parameter_map = (Map)aggregated_txn_data_map.get("Common");

			//common_parameters_map is a map for both common accumulated and aggregated parameter keys  which is the master data with keys 
			//Common_Aggregated_Parameters for common aggregated paramters 
			Iterator common_parameters_keys_itr = ((List)common_parameters_map.get("Common_Aggregated_Parameters")).iterator();

			while(common_parameters_keys_itr.hasNext()){
				String parameter_key = (String)(common_parameters_keys_itr.next());
				EngineRunningBandList.add((Double)commom_aggregated_parameter_map.get(parameter_key));
			}

			endTime = System.currentTimeMillis();

			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" insertOlapNew getEngineRunningBands executed in :"+(endTime - startTime));
			if(EngineRunningBandList != null)
				iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" insertOlapNew after  getEngineRunningBands size :"+EngineRunningBandList.size());

			/*List ampParameterIDList = new LinkedList();
			ampParameterIDList.add(longitudeId);
			ampParameterIDList.add(latitudeId);
			ampParameterIDList.add(engineHoursId);
			ampParameterIDList.add(engineONId);*/

			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" insertOlapNew before getEngineRunningBands :");
			startTime = System.currentTimeMillis();

			//List<AssetMonitoringParametersDAO> lst = amhDAL.getAMPValues(Serial, tranTime, seg_ID, ampParameterIDList);

			Map accumulated_txn_data_map = new DynamicTAMD_DAL().getAccumulatedDataOnTxnDate(Serial, tranTime, seg_ID, common_parameters_map);



			//aggregated_txn_data_map is a TxnData map which has both new aggregated and common aggregated values for all parameters is stored with 
			//keys New_Aggregated , Common 

			//Df20170522 @Roopa commenting the below logic because the accululated parameters comes only in last log pkt for BHL and CAN parameters

			//new_accumulated_parameter_map = (Map)accumulated_txn_data_map.get("New_Accumulated");


			commom_accumulated_parameter_map = (Map)accumulated_txn_data_map.get("Common");

			//common_parameters_map is a map for both common accumulated and aggregated parameter keys  which is the master data with keys 
			//Common_Accumulated_Parameters for common accumulated paramters 
			common_parameters_keys_itr = ((List)common_parameters_map.get("Common_Accumulated_Parameters")).iterator();

			if(new_aggregated_parameter_map!=null && new_accumulated_parameter_map!=null)
				new_aggregated_parameter_map.putAll(new_accumulated_parameter_map);

			String newTxndetails=null;

			//Df20170623 @Roopa calculation for the below params has do be done at each individual pkt level, so moved to common place CommonUtil class

			//start the server side calculation's for the CAN parameters and add it to json column(new_aggregated_parameter_map)
			/*double FuelUsedInLPB=0.0;
			double FuelUsedInMPB=0.0;
			double FuelUsedInHPB=0.0;
			double AvgFuelConsumption=0.0;
			double FuelLoss=0.0;
			double FuelUsedInWorking=0.0;
			String Fuel_rate=null;
			String LEPB=null;
			String MEPB=null;
			String HEPB=null;
			String IEPB=null;*/
			String TFL=null;
			String FuelPerct=null;

			if(new_aggregated_parameter_map!=null && new_aggregated_parameter_map.size()>0){

				/*if(new_aggregated_parameter_map.get("TFL")!=null){
					TFL=new_aggregated_parameter_map.get("TFL").toString();

					AvgFuelConsumption=(Double.parseDouble(TFL)/24.0)*100; 
				}*/

				/*if(new_aggregated_parameter_map.get("Fuel_rate")!=null)
					Fuel_rate=new_aggregated_parameter_map.get("Fuel_rate").toString();

				if(new_aggregated_parameter_map.get("LEPB")!=null)
					LEPB=new_aggregated_parameter_map.get("LEPB").toString();

				if(new_aggregated_parameter_map.get("MEPB")!=null)
					MEPB=new_aggregated_parameter_map.get("MEPB").toString();

				if(new_aggregated_parameter_map.get("HEPB")!=null)
					HEPB=new_aggregated_parameter_map.get("HEPB").toString();

				if(new_aggregated_parameter_map.get("IEPB")!=null)
					IEPB=new_aggregated_parameter_map.get("IEPB").toString();

				if(Fuel_rate!=null){
					if(LEPB!=null){
						FuelUsedInLPB=Double.parseDouble(Fuel_rate)*Double.parseDouble(LEPB);
					}
					if(MEPB!=null){
						FuelUsedInMPB=Double.parseDouble(Fuel_rate)*Double.parseDouble(MEPB);
					}
					if(HEPB!=null){
						FuelUsedInHPB=Double.parseDouble(Fuel_rate)*Double.parseDouble(HEPB);
					}
					if(IEPB!=null){
						FuelLoss=Double.parseDouble(Fuel_rate)*Double.parseDouble(IEPB);
					}

					FuelUsedInWorking=FuelUsedInLPB+FuelUsedInMPB+FuelUsedInHPB;
				}*/

				//AvgFuelConsumption

				//yet to get the business logic

				/*	new_aggregated_parameter_map.put("FuelUsedInLPB", FuelUsedInLPB);
				new_aggregated_parameter_map.put("FuelUsedInMPB", FuelUsedInMPB);
				new_aggregated_parameter_map.put("FuelUsedInHPB", FuelUsedInHPB);
				new_aggregated_parameter_map.put("AvgFuelConsumption", AvgFuelConsumption);
				new_aggregated_parameter_map.put("FuelLoss", FuelLoss);
				new_aggregated_parameter_map.put("FuelUsedInWorking", FuelUsedInWorking);*/

				//EngineOnCount(No of times the Eng ON has come)

				String eng_onoff_count=new DynamicTAMD_DAL().getEngineONAndOFFCount(Serial, tranTime, seg_ID);

				new_aggregated_parameter_map.put("EngineOnCount", eng_onoff_count.split(",")[0]);
				//EngineOffCount (No of times the Eng OFF has come)
				new_aggregated_parameter_map.put("EngineOffCount", eng_onoff_count.split(",")[1]);

				/*if(new_aggregated_parameter_map.get("FUEL_PERCT")!=null){
				if(commom_accumulated_parameter_map.get("FUEL_PERCT")!=null)
				FuelPerct= commom_accumulated_parameter_map.get("FUEL_PERCT").toString();

				new_aggregated_parameter_map.put("FUEL_PERCT", FuelPerct);
				}*/

				new_aggregated_parameter_map.put("regionCode", regionCode);
				new_aggregated_parameter_map.put("regionName", regionName);
				new_aggregated_parameter_map.put("customerCode", customerCode);
				new_aggregated_parameter_map.put("CustomerName", CustomerName);
				new_aggregated_parameter_map.put("dealerCode", dealerCode);
				new_aggregated_parameter_map.put("DealerName", DealerName);
				new_aggregated_parameter_map.put("ZonalCode", ZonalCode);
				new_aggregated_parameter_map.put("ZonalName", ZonalName);
				new_aggregated_parameter_map.put("PrimaryOwner", PrimaryOwner);
				new_aggregated_parameter_map.put("Customer_Number", Customer_Number);
				new_aggregated_parameter_map.put("Dealer_Number", Dealer_Number);

				//Df20171109 @Roopa logic for below List of the Parameters to be accumulated at server end

				List<AMSDoc_DAO> amsList=new DynamicRemoteFactDay_DAL().getAMSData("ETL", Serial);

				if(amsList!=null && amsList.size()>0){

					HashMap<String,String> payloadMap=amsList.get(0).getTxnData();

					if(payloadMap!=null && payloadMap.size()>0){

						if(payloadMap.containsKey("No_AutoIdle_Events"))
							new_aggregated_parameter_map.put("No_AutoIdle_Events", payloadMap.get("No_AutoIdle_Events"));
						if(payloadMap.containsKey("HAC"))
							new_aggregated_parameter_map.put("HAC", payloadMap.get("HAC"));
						if(payloadMap.containsKey("LEIC"))
							new_aggregated_parameter_map.put("LEIC", payloadMap.get("LEIC"));
						if(payloadMap.containsKey("HESDC"))
							new_aggregated_parameter_map.put("HESDC", payloadMap.get("HESDC"));
						if(payloadMap.containsKey("No_AutoOFF_Events"))
							new_aggregated_parameter_map.put("No_AutoOFF_Events", payloadMap.get("No_AutoOFF_Events"));
						if(payloadMap.containsKey("Hyd_Choke_Events"))
							new_aggregated_parameter_map.put("Hyd_Choke_Events", payloadMap.get("Hyd_Choke_Events"));
						
						//Adding MSG_ID to the map for BHL model identification at report side 
						
						//Df20170104 Storing common log msg id, if end of the packet is received event,(Required for (joining)aggregation for multi vin when we want to fetch data from two diff tables)
						
						if(payloadMap.containsKey("MSG_ID")){
							
							if(payloadMap.get("MSG_ID").equalsIgnoreCase("003")){
							new_aggregated_parameter_map.put("MSG_ID", "030");
							}
							else if(payloadMap.get("MSG_ID").equalsIgnoreCase("002")){
								new_aggregated_parameter_map.put("MSG_ID", "020");
								}
							else if(payloadMap.get("MSG_ID").equalsIgnoreCase("001")){
								new_aggregated_parameter_map.put("MSG_ID", "010");
								}
							else if(payloadMap.get("MSG_ID").equalsIgnoreCase("004")){ //JENSET integration
								new_aggregated_parameter_map.put("MSG_ID", "040");
								}
							else{
								new_aggregated_parameter_map.put("MSG_ID", payloadMap.get("MSG_ID"));
							}
						}
						
						
						//DF20180104 @Roopa server side accumulation logic for WLS and CMS parameters
						if(payloadMap.containsKey("CMS_DJ"))
							new_aggregated_parameter_map.put("CMS_DJ_SA", payloadMap.get("CMS_DJ"));
						if(payloadMap.containsKey("CMS_VSOO"))
							new_aggregated_parameter_map.put("CMS_VSOO_SA", payloadMap.get("CMS_VSOO"));
						if(payloadMap.containsKey("CMS_AMS"))
							new_aggregated_parameter_map.put("CMS_AMS_SA", payloadMap.get("CMS_AMS"));
						if(payloadMap.containsKey("CMS_MMS"))
							new_aggregated_parameter_map.put("CMS_MMS_SA", payloadMap.get("CMS_MMS"));
						
						//END
						
						
					}
				}


				//Df20171109 END
			}

			//end

			if(new_aggregated_parameter_map!=null && new_aggregated_parameter_map.size()>0)
				newTxndetails= new JSONObject(new_aggregated_parameter_map).toString();


			endTime = System.currentTimeMillis();

			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" insertOlapNew -> getEngineRunningBands executed in :"+(endTime - startTime));

			//Iterator itr = lst.iterator();

			int flag = 0;


			while (common_parameters_keys_itr.hasNext()) 
			{
				flag++;


				String parameter_key = (String)(common_parameters_keys_itr.next());

				if ( parameter_key.equals("LONG")  ){
					if(commom_accumulated_parameter_map!=null){
						Longitude = ((Double)commom_accumulated_parameter_map.get(parameter_key)).toString();
					}
				}



				if ( parameter_key.equals("LAT")  ){
					if(commom_accumulated_parameter_map!=null){
						Latitude = ((Double)commom_accumulated_parameter_map.get(parameter_key)).toString();
					}
				}



				if ( parameter_key.equals("CMH")  ){
					if(commom_accumulated_parameter_map!=null){
						MachineHours = ((Double)commom_accumulated_parameter_map.get(parameter_key));
					}
				}

				if ( parameter_key.equals("ENG_STATUS")  ){

					//DF20170317 @Roopa ENG_STATUS will be not there for old machines instead we have to check for EVT_ENG

					if(commom_accumulated_parameter_map!=null){
						if(commom_accumulated_parameter_map.get(parameter_key)!=null){
							engine_Status = (((Double)commom_accumulated_parameter_map.get(parameter_key)).intValue())+"";

						}
						else if(commom_accumulated_parameter_map.get("EVT_ENG")!=null){
							engine_Status = (((Double)commom_accumulated_parameter_map.get("EVT_ENG")).intValue())+"";
						}

						else{
							engine_Status="0";
						}

					}
					else{
						engine_Status="0";
					}
					if ( Integer.parseInt( engine_Status ) == 1 ) {
						Last_Engine_Run = tranTime;
					} else {


						Last_Engine_Run = new DynamicTAMD_DAL().getLatestEngineONTxn(Serial, tranTime, seg_ID);
						if(Last_Engine_Run==null){
							//Last_Engine_Run= Timestamp.valueOf("1000-01-01 00:00:00"); //setting default value to avoid the Incorrect datetime value: 'null' for column 'Last_Engine_Run' eror	
							Last_Engine_Run=tranTime;
						}

					}
				}

			}

			for (int i = 0; i < EngineRunningBandList.size(); i++) {
				EngineOffHours = EngineOffHours + EngineRunningBandList.get(i);
			}
			iLogger.info("EngineOffHours "+ EngineOffHours);
			EngineOffHours = 24 - EngineOffHours;
			iLogger.info("24 - EngineOffHours  "+ EngineOffHours);
			if ( EngineRunningBandList.size() == 0) {
				for ( int i = 1; i <= 8; i++){
					EngineRunningBandList.add(0.0);
				}
			}

			endTime = System.currentTimeMillis();
			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" Get current details for VIN "
					+ Serial
					+ " time in ms: "
					+ (endTime - startTime));

			startTime = System.currentTimeMillis();


			TotalFuelUsed = (Double)(commom_accumulated_parameter_map.get("FUEL_PERCT"));



			if ( TotalFuelUsed > 0 && EngineOffHours < 24 ) {
				FuelIdle  = (TotalFuelUsed/(24 - EngineOffHours)) * (EngineRunningBandList.get(0) + EngineRunningBandList.get(1));
				FuelWorking = TotalFuelUsed - FuelIdle;
			} else {
				FuelIdle  = 0.0;
				FuelWorking = 0.0;
			}


			endTime = System.currentTimeMillis();
			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" Get fuel usage for VIN "
					+ Serial
					+ " time in ms: "
					+ (endTime - startTime));



			SimpleDateFormat simpleDtStr = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			Timestamp created_timestamp = new Timestamp(cal.getTime().getTime());



			//Start check for the record exist for the given vin_transaction time

			//to avoid the session update error, if the record doesn't exist for the given date

			DynamicRemoteFactDay_DAL remoteFactDayObj=new DynamicRemoteFactDay_DAL();

			//lastOlapDt=Timestamp.valueOf(dateStr.format(lastOlapDt) + " 00:00:00");

			/*if (update){
				String query="select Time_Key from remote_monitoring_fact_data_dayagg_json_new where Segment_ID_TxnDate = "+seg_ID+" and Serial_number = '"+ Serial + "' and Time_Key='"+Timestamp.valueOf(dateStr.format(tranTime) + " 00:00:00")+"'";


				String Time_Key= remoteFactDayObj.getTimeKey(query);
				if (Time_Key != null && Time_Key.length()!=0)  {
					update=true;	
				}
				else{
					update=false;
				}

		}*/



			//End

			AssetMonitoringFactDataEntity assetMonitoringFactDataEntity = new AssetMonitoringFactDataEntity();
			assetMonitoringFactDataEntity.setCreated_Timestamp(created_timestamp);
			/*if ( update ){
				assetMonitoringFactDataEntity.setTimeKey( Timestamp.valueOf(dateStr.format(lastOlapDt) + " 00:00:00") );
			} else {*/
			assetMonitoringFactDataEntity.setTimeKey( Timestamp.valueOf(dateStr.format(tranTime) + " 00:00:00") );
			//}

			//assetMonitoringFactDataEntity.setTenancyId(tenancy_dimension_id);
			//assetMonitoringFactDataEntity.setAccountDimensionId(accountDimensionId);
			assetMonitoringFactDataEntity.setSerialNumber(Serial);

			assetMonitoringFactDataEntity.setMachineHours(Double.valueOf(df2.format((Double)(MachineHours))));
			assetMonitoringFactDataEntity.setFuelUsedIdle(FuelIdle);
			assetMonitoringFactDataEntity.setFuelUsedWorking(FuelWorking);
			assetMonitoringFactDataEntity.setLastReported(tranTime);
			assetMonitoringFactDataEntity.setLastEngineRun(Last_Engine_Run);
			assetMonitoringFactDataEntity.setLocation(Latitude + "," + Longitude);
			iLogger.info("Double.valueOf(df2.format((Double)(EngineOffHours))) "+Double.valueOf(df2.format((Double)(EngineOffHours))));
			assetMonitoringFactDataEntity.setEngineOffHours(Double.valueOf(df2.format((Double)(EngineOffHours))));
			assetMonitoringFactDataEntity.setEngineRunningBand1(EngineRunningBandList.get(0));
			assetMonitoringFactDataEntity.setEngineRunningBand2(EngineRunningBandList.get(1));
			assetMonitoringFactDataEntity.setEngineRunningBand3(EngineRunningBandList.get(2));
			assetMonitoringFactDataEntity.setEngineRunningBand4(EngineRunningBandList.get(3));
			assetMonitoringFactDataEntity.setEngineRunningBand5(EngineRunningBandList.get(4));
			assetMonitoringFactDataEntity.setEngineRunningBand6(EngineRunningBandList.get(5));
			assetMonitoringFactDataEntity.setEngineRunningBand7(EngineRunningBandList.get(6));
			if(EngineRunningBandList.size()>7)
				assetMonitoringFactDataEntity.setEngineRunningBand8(EngineRunningBandList.get(7));
			else
				assetMonitoringFactDataEntity.setEngineRunningBand8(0.0);	
			//assetMonitoringFactDataEntity.setAssetClassDimensionId(Asset_Class_Id);
			assetMonitoringFactDataEntity.setMachineName(NickName);
			assetMonitoringFactDataEntity.setAddress(null);
			assetMonitoringFactDataEntity.setEngineStatus(engine_Status);
			assetMonitoringFactDataEntity.setAggregate_param_data(newTxndetails);

			String status= remoteFactDayObj.updateRemoteFactDetails(assetMonitoringFactDataEntity, update,seg_ID,tenancy_dimension_id_json,acc_id_json,asset_class_id_json);

			iLogger.info("ETLfactDataJsonBO: insertorUpdateOlapNew for SegmentID "+segment_ID+" VIN "+Serial+" TxnKey "+tranTime+" Status::"+status);

		} // End of main try bloock

		catch (Exception e) {


			fLogger.fatal("ETLfactDataJsonBO: insertOlapNew SegmentID"+segmentId+" VIN "+Serial+" Fatal error processing VIN: "
					+ Serial 
					+ "Message: "
					+ e.getMessage());

			Writer result1 = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result1);
			e.printStackTrace(printWriter);
			String err = result1.toString();
			fLogger.fatal("ETLfactDataJsonBO: insertOlapNew SegmentID"+segmentId+" VIN "+Serial+" TxnKey "+tranTime+" Exception trace: "+err);
		} 

		finally {

		}

	}//*** end of insertOlap ***



}
