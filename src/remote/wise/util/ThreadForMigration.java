package remote.wise.util;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

//import com.ibm.icu.text.DecimalFormat;

public class ThreadForMigration implements Callable<String>{

	/*public static ConcurrentHashMap< String, Integer> statesMap = getStatesMap();
	public static ConcurrentHashMap< String, Integer> citiesMap = getCitiesMap();*/
	String time_key;
	String VIN;
	public ThreadForMigration(String time_key, String VIN){
		this.time_key = time_key;
		this.VIN = VIN;
		Thread.currentThread().setName(this.time_key);
	}

	/*private static ConcurrentHashMap<String, Integer> getCitiesMap() {
		// TODO Auto-generated method stub
		ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
		if(citiesMap == null){
			Connection connection = new ConnectMySQL().getProdDb2Connection();
			try {
				Statement st = connection.createStatement();
				ResultSet citiesRS = st.executeQuery("select * from City");

				while(citiesRS.next()){
					int cityID = citiesRS.getInt("CityID");
					String city = citiesRS.getString("CityName");
					map.put(city, cityID);
				}
				//citiesMap = map;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				if(connection != null){
					try {
						connection.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return map;
	}


	private static ConcurrentHashMap<String, Integer> getStatesMap() {
		// TODO Auto-generated method stub
		ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
		if(statesMap == null){
			Connection connection = new ConnectMySQL().getProdDb2Connection();
			try {
				Statement st = connection.createStatement();
				ResultSet citiesRS = st.executeQuery("select * from State");

				while(citiesRS.next()){
					int stateID = citiesRS.getInt("StateID");
					String state = citiesRS.getString("StateName");
					map.put(state, stateID);
				}
				//citiesMap = map;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				if(connection != null){
					try {
						connection.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return map;
	}*/

	@Override
	public String call() throws Exception {
		// TODO Auto-generated method stub
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info(Thread.currentThread().getName()+" is running...");
		String status = getDayAGGData(time_key,VIN);
		//DF20180212 @Roopa returning failure if any of the address is null, so that not migrating to extended table also.
		if(! status.equalsIgnoreCase("FAILURE")){
		String bhlInsertCount= getDayAGGDataBHL(time_key,VIN);
		getDayAGGDataUpdate(time_key,VIN);
		getDayAGGDataUpdateBHL(time_key,VIN);
		}
		return status;
	}

	public String getDayAGGDataBHL(String timeKey, String VIN){
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String result = timeKey;
		Connection prodConnection = null;
		Statement statement = null;
		Statement localStatement = null;
		ResultSet rs = null;

		int week = 0;
		int month = 0;
		int year = 0;
		int quarter = 0;

		String asset_type = null;
		String group_id = null;
		String customer_code = null;
		String dealer_code = null;
		String zonal_code = null;
		String regionCode=null;

		String msgID=null;

		Connection db2Connection = null;
		int row=1;
		final int partition_ID =1;
		List<String> faultStatements = new LinkedList<String>();
		PreparedStatement pst= null;

		String ac_type = null;	
		DecimalFormat decformat=new DecimalFormat("0.000000");
		double CMH=0;


		//Start reading json data for BHL

		long threadStart1 = System.currentTimeMillis();


		try {

			int bhlrow=1;

			iLogger.info(timeKey+" BHL is executing day agg New for BHL... with time_key "+timeKey);
			iLogger.info("BHL getting proddb2 connection");

			iLogger.info("BHL connected to proddb2 connection BHL");



			long queryStartTime = System.currentTimeMillis();


			iLogger.info(" BHL thread "+timeKey+" running the query BHL");
			String jsonMigrationQuery=null;


			jsonMigrationQuery="select firstDayAgg.*,"+
					"seconddayAgg.*"+
					" from("+
					" select dayagg.Serial_Number,"+
					"dayagg.Time_Key as timeKey,"+
					"Week(dayagg.Time_Key)+1 as week_no,"+
					"MONTH(dayagg.Time_Key) as month,"+
					"QUARTER(dayagg.Time_Key) as quarter,"+
					"YEAR(dayagg.Time_Key) as year,"+
					"dayagg.agg_param_data,"+
					"dayagg.Machine_Hours as EndEHours,"+
					"a_t.Asset_Type_Code as asset_type,agp.asset_grp_code as group_code" +
					//",ac.Account_Code as customerCode,pa.Account_Code as parentCode,za.Account_Code as ZonalCode,td.Tenancy_Type_Name as ac_type" +
					" from remote_monitoring_fact_data_dayagg_json_new dayagg,"+
					"asset a,products p,"+
					"asset_type a_t,asset_group ag,asset_group_profile agp "+
					//"tenancy_dimension td,account_tenancy act,"+
					//"account ac,account pa left outer join account za on za.account_id = pa.Parent_ID "+ 
					" where dayagg.time_key = '"+timeKey+"' " +
					"and dayagg.Engine_Off_Hours >=0 " +
					"and " + 
					"dayagg.Serial_Number = a.Serial_Number and "+
					"p.Product_ID = a.Product_ID and "+ 
					"ag.Asset_Group_ID = p.Asset_Group_ID and "+ 
					"agp.asset_grp_id = p.Asset_Group_ID and "+ 
					"a_t.Asset_Type_ID = p.Asset_Type_ID " +
					//"and "+
					//"td.tenancy_dimension_id = dayagg.tenancy_id and "+ 
					//"act.tenancy_id = td.tenancy_id and "+ 
					//"ac.account_id = act.account_id and "+ 
					//"pa.account_id = ac.Parent_ID "+  
					") firstDayAgg left outer join"+
					" (select prevdayAgg.serial_number as lastSerial_Number, prevdayAgg.agg_param_data  as lastagg_param_data, prevdayAgg.time_key"+
					" from remote_monitoring_fact_data_dayagg_json_new prevdayAgg"+ 
					" left outer join "+
					"(select max(sn.Time_key) tk,sn.serial_number ser"+
					"	from remote_monitoring_fact_data_dayagg_json_new sn"+
					" where sn.time_key < '"+timeKey+"'"+
					" group by sn.serial_number"+
					") nextdayAgg"+
					" on prevdayAgg.serial_number=nextdayAgg.ser"+ 
					" where prevdayAgg.Time_Key=nextdayAgg.tk"+
					") seconddayAgg"+
					" on firstDayAgg.serial_number=seconddayAgg.lastSerial_Number ";

			if(VIN!=null && (VIN.trim().length()==7 || VIN.trim().length()==17)){
				if(VIN.trim().length()==7){

					jsonMigrationQuery="select firstDayAgg.*,"+
							"seconddayAgg.*"+
							" from("+
							" select dayagg.Serial_Number,"+
							"dayagg.Time_Key as timeKey,"+
							"Week(dayagg.Time_Key)+1 as week_no,"+
							"MONTH(dayagg.Time_Key) as month,"+
							"QUARTER(dayagg.Time_Key) as quarter,"+
							"YEAR(dayagg.Time_Key) as year,"+
							"dayagg.agg_param_data,"+
							"dayagg.Machine_Hours as EndEHours,"+
							"a_t.Asset_Type_Code as asset_type,agp.asset_grp_code as group_code" +
							//",ac.Account_Code as customerCode,pa.Account_Code as parentCode,za.Account_Code as ZonalCode,td.Tenancy_Type_Name as ac_type" +
							" from remote_monitoring_fact_data_dayagg_json_new dayagg,"+
							"asset a,products p,"+
							"asset_type a_t,asset_group ag,asset_group_profile agp "+
							//"tenancy_dimension td,account_tenancy act,"+
							//"account ac,account pa left outer join account za on za.account_id = pa.Parent_ID "+ 
							" where dayagg.Serial_Number like '%"+VIN+"' and dayagg.time_key = '"+timeKey+"' " +
							"and dayagg.Engine_Off_Hours >=0 " +
							"and " + 
							"dayagg.Serial_Number = a.Serial_Number and "+
							"p.Product_ID = a.Product_ID and "+ 
							"ag.Asset_Group_ID = p.Asset_Group_ID and "+ 
							"agp.asset_grp_id = p.Asset_Group_ID and "+ 
							"a_t.Asset_Type_ID = p.Asset_Type_ID " +
							//"and "+
							//"td.tenancy_dimension_id = dayagg.tenancy_id and "+ 
							//"act.tenancy_id = td.tenancy_id and "+ 
							//"ac.account_id = act.account_id and "+ 
							//"pa.account_id = ac.Parent_ID "+  
							") firstDayAgg left outer join"+
							" (select prevdayAgg.serial_number as lastSerial_Number, prevdayAgg.agg_param_data  as lastagg_param_data, prevdayAgg.time_key"+
							" from remote_monitoring_fact_data_dayagg_json_new prevdayAgg"+ 
							" left outer join "+
							"(select max(sn.Time_key) tk,sn.serial_number ser"+
							"	from remote_monitoring_fact_data_dayagg_json_new sn"+
							" where sn.time_key < '"+timeKey+"' and " +
							"sn.Serial_Number like '%" +VIN+"' "+
							" group by sn.serial_number"+
							") nextdayAgg"+
							" on prevdayAgg.serial_number=nextdayAgg.ser"+ 
							" where prevdayAgg.Time_Key=nextdayAgg.tk"+
							") seconddayAgg"+
							" on firstDayAgg.serial_number=seconddayAgg.lastSerial_Number ";



				}
				else if(VIN.trim().length()==17){

					jsonMigrationQuery="select firstDayAgg.*,"+
							"seconddayAgg.*"+
							" from("+
							" select dayagg.Serial_Number,"+
							"dayagg.Time_Key as timeKey,"+
							"Week(dayagg.Time_Key)+1 as week_no,"+
							"MONTH(dayagg.Time_Key) as month,"+
							"QUARTER(dayagg.Time_Key) as quarter,"+
							"YEAR(dayagg.Time_Key) as year,"+
							"dayagg.agg_param_data,"+
							"dayagg.Machine_Hours as EndEHours,"+
							"a_t.Asset_Type_Code as asset_type,agp.asset_grp_code as group_code " +
							//",ac.Account_Code as customerCode,pa.Account_Code as parentCode,za.Account_Code as ZonalCode,td.Tenancy_Type_Name as ac_type" +
							" from remote_monitoring_fact_data_dayagg_json_new dayagg,"+
							"asset a,products p,"+
							"asset_type a_t,asset_group ag,asset_group_profile agp "+
							//"tenancy_dimension td,account_tenancy act,"+
							//"account ac,account pa left outer join account za on za.account_id = pa.Parent_ID "+ 
							" where dayagg.Serial_Number = '"+VIN+"' and dayagg.time_key = '"+timeKey+"' " +
							"and dayagg.Engine_Off_Hours >=0 " +
							"and " + 
							"dayagg.Serial_Number = a.Serial_Number and "+
							"p.Product_ID = a.Product_ID and "+ 
							"ag.Asset_Group_ID = p.Asset_Group_ID and "+ 
							"agp.asset_grp_id = p.Asset_Group_ID and "+ 
							"a_t.Asset_Type_ID = p.Asset_Type_ID " +
							//"and "+
							//"td.tenancy_dimension_id = dayagg.tenancy_id and "+ 
							//"act.tenancy_id = td.tenancy_id and "+ 
							//"ac.account_id = act.account_id and "+ 
							//"pa.account_id = ac.Parent_ID "+  
							") firstDayAgg left outer join"+
							" (select prevdayAgg.serial_number as lastSerial_Number, prevdayAgg.agg_param_data  as lastagg_param_data, prevdayAgg.time_key"+
							" from remote_monitoring_fact_data_dayagg_json_new prevdayAgg"+ 
							" left outer join "+
							"(select max(sn.Time_key) tk,sn.serial_number ser"+
							"	from remote_monitoring_fact_data_dayagg_json_new sn"+
							" where sn.time_key < '"+timeKey+"' and " +
							"sn.Serial_Number = '" +VIN+"' "+
							" group by sn.serial_number"+
							") nextdayAgg"+
							" on prevdayAgg.serial_number=nextdayAgg.ser"+ 
							" where prevdayAgg.Time_Key=nextdayAgg.tk"+
							") seconddayAgg"+
							" on firstDayAgg.serial_number=seconddayAgg.lastSerial_Number ";



				}
			}



			iLogger.info("jsonMigrationQuery BHL::"+jsonMigrationQuery);
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			rs = statement.executeQuery(jsonMigrationQuery);
			long queryEndTime = System.currentTimeMillis();
			if(rs != null){
				rs.last();
				int rowCount = rs.getRow();
				rs.beforeFirst();
				//iLogger.info("total records for thread "+timeKey+" are:"+rowCount);
				iLogger.info("BHL query for total records for thread BHL "+timeKey+" are:"+rowCount+" exceuted in "+(queryEndTime - queryStartTime));
				iLogger.info("thread BHL "+timeKey+" started inserting BHL :"+rowCount+" records");
			}



			/*String insertStatement = "  insert into factInsight_dayAgg_extended(AssetID,TxnDate"+
					",DSC_CON_HRS,ENG_IDL_HRS,EXA_ECO_MOD_HRS,EXA_ACT_MOD_HRS,EXA_PWR_MOD_HRS,FIX_FLOW_HRS,FWD_DIR_HRS,LOAD_EC_MOD_HRS,LOAD_JOB_HRS,LOAD_PWR_MOD_HRS,Min_Hyd_HRS,NEU_DIR_HRS,REV_DIR_HRS"+
					",Hyd_Choke_Events,Load_Mode_First_gear,Load_Mode_ForwardDirection,Load_Mode_NeutralDirection,Load_Mode_NeutralGear,Load_Mode_ReverseDirection,Load_Mode_Second_gear,No_AutoIdle_Events,No_AutoOFF_Events,No_Kick_Load_Mode,PRB1,PRB2,PRB3,PRB4,PRB5,PRB6,PRBEM1,PRBEM2,PRBEM3,PRBEM4,PRBEM5,PRBEM6,PDRBEM1,PDRBEM2,PDRBEM3,PDRBEM4,PDRBEM5,PDRBEM6,PDRBLM1,PDRBLM2,PDRBLM3,PDRBLM4,PDRBLM5,PDRBLM6,Road_Mode_First_Gear,Road_Mode_Forard_Direction,Road_Mode_Fourth_Gear,Road_Mode_Neutral_Gear,Road_Mode_Reverse_Direction,Road_Mode_Second_Gear,Road_Mode_Third_Gear,Time_ActiveMode_Ex_Mode,Time_EcMode_Ex_Mode,Time_Hammer_Operation,Time_PwrMode_Ex_Mode"+
					",FuelLevelPerct,FuelRate,StartFuelInLtrs,FinishFuelInLtrs,FuelUsedInLtrs,FuelUsedInLPB,FuelUsedInMPB,FuelUsedInHPB,AvgFuelConsumption,FuelLoss,FuelUsedInWorking,StartLMode,FinishLMode,LModeInHrs,StartGMode,FinishGMode,GModeInHrs,StartHMode,FinishHMode,HModeInHrs,StartHPlusMode,FinishHPlusMode,HPlusModeInHrs,StartTravellingTime,FinishTravellingTime,TravellingTimeInHrs,StartSlewTime,FinishSlewTime,SlewTimeInHrs,StartHammerUseTime,FinishHammerUseTime,HammerUseTimeInHrs,HammerAbuseCount,CarbonEmission,PowerBoostTime,RegenerationTime,AverageVehicleSpeed,EngineManufacturer,Gear1FwdUtilization,Gear1BkwdUtilization,Gear1LockupUtilization,Gear2FwdUtilization,Gear2BkwdUtilization,Gear2LockupUtilization,Gear31FwdUtilization,Gear3BkwdUtilization,Gear3LockupUtilization,Gear4FwdUtilization,Gear4BkwdUtilization,Gear4LockupUtilization,Gear5FwdUtilization,Gear5BkwdUtilization,Gear5LockupUtilization,Gear6FwdUtilization,Gear6BkwdUtilization,Gear6LockupUtilization,NeutralGearUtilization,EngineOnCount,EngineOffCount"+
					",PartitionID,Column1,Column2,Column3,Column4,Column5,Column6,Column7,Column8,Column9,Column10,Column11,Column12,Column13,Column14,Column15,Column16,Column17,Column18,Column19,Column20,Column21" +
					",Column22,Column23,Column24,Column25,Column26,Column27,Column28,Column29,Column30,Column31,Column32,Column33,Column34,Column35,Column36,Column37,Column38,Column39)" +
					" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
					",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";*/
			
			String insertStatement = "  insert into factInsight_dayAgg_extended(AssetID,TxnDate"+
					",DSC_CON_HRS,ENG_IDL_HRS,EXA_ECO_MOD_HRS,EXA_ACT_MOD_HRS,EXA_PWR_MOD_HRS,FIX_FLOW_HRS,FWD_DIR_HRS,LOAD_EC_MOD_HRS,LOAD_JOB_HRS,LOAD_PWR_MOD_HRS,Min_Hyd_HRS,NEU_DIR_HRS,REV_DIR_HRS"+
					",Hyd_Choke_Events,Load_Mode_First_gear,Load_Mode_ForwardDirection,Load_Mode_NeutralDirection,Load_Mode_NeutralGear,Load_Mode_ReverseDirection,Load_Mode_Second_gear,No_AutoIdle_Events,No_AutoOFF_Events,No_Kick_Load_Mode,PRB1,PRB2,PRB3,PRB4,PRB5,PRB6,PRBEM1,PRBEM2,PRBEM3,PRBEM4,PRBEM5,PRBEM6,PDRBEM1,PDRBEM2,PDRBEM3,PDRBEM4,PDRBEM5,PDRBEM6,PDRBLM1,PDRBLM2,PDRBLM3,PDRBLM4,PDRBLM5,PDRBLM6,Road_Mode_First_Gear,Road_Mode_Forard_Direction,Road_Mode_Fourth_Gear,Road_Mode_Neutral_Gear,Road_Mode_Reverse_Direction,Road_Mode_Second_Gear,Road_Mode_Third_Gear,Time_ActiveMode_Ex_Mode,Time_EcMode_Ex_Mode,Time_Hammer_Operation,Time_PwrMode_Ex_Mode"+
					",FuelLevelPerct,FuelRate,StartFuelInLtrs,FinishFuelInLtrs,FuelUsedInLtrs,FuelUsedInLPB,FuelUsedInMPB,FuelUsedInHPB,AvgFuelConsumption,FuelLoss,FuelUsedInWorking,StartLMode,FinishLMode,LModeInHrs,StartGMode,FinishGMode,GModeInHrs,StartHMode,FinishHMode,HModeInHrs,StartHPlusMode,FinishHPlusMode,HPlusModeInHrs,StartTravellingTime,FinishTravellingTime,TravellingTimeInHrs,StartSlewTime,FinishSlewTime,SlewTimeInHrs,StartHammerUseTime,FinishHammerUseTime,HammerUseTimeInHrs,HammerAbuseCount,CarbonEmission,PowerBoostTime,RegenerationTime,AverageVehicleSpeed,EngineManufacturer,Gear1FwdUtilization,Gear1BkwdUtilization,Gear1LockupUtilization,Gear2FwdUtilization,Gear2BkwdUtilization,Gear2LockupUtilization,Gear31FwdUtilization,Gear3BkwdUtilization,Gear3LockupUtilization,Gear4FwdUtilization,Gear4BkwdUtilization,Gear4LockupUtilization,Gear5FwdUtilization,Gear5BkwdUtilization,Gear5LockupUtilization,Gear6FwdUtilization,Gear6BkwdUtilization,Gear6LockupUtilization,NeutralGearUtilization,EngineOnCount,EngineOffCount"+
					",PartitionID,Column1,Column2,Column3,Column4,Column5,Column6,Column7,Column8,Column9,Column10,Column11,Column12,Column13,Column14,Column15,Column16,Column17,Column18,Column19,Column20,Column21" +
					",Column22,Column23,Column24,Column25,Column26,Column27,Column28,Column29,Column30,Column31,Column32,Column33,Column34,Column35,Column36,Column37,Column38,Column39" +
					",Column40,Column41,Column42,Column43,Column44,Column45,Column46,Column47,Column48,Column49,Column50)" +
					" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
					",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			long endQueryTime = System.currentTimeMillis();


			iLogger.info(timeKey+" BHL thread execution time for query BHL"+((int)(((endQueryTime-queryStartTime)/1000)%60))+"");
			//iLogger.info(timeKey+" thread execution time for query"+((int)(((endQueryTime-queryStartTime)/1000)%60))+"");
			if(db2Connection == null || db2Connection.isClosed())
				db2Connection = new ConnectMySQL().getProdDb2Connection();
			pst = db2Connection.prepareStatement(insertStatement);

			iLogger.info(timeKey+" BHL before executing while loop for query BHL");
			//iLogger.info(timeKey+"before executing while loop for query");
			long iteratorStart = System.currentTimeMillis();

			HashMap<String,String> txnDataMap=new HashMap<String, String>();

			String txnData=null;
			Date txnDate=null;
			String Serial_Number1=null;

			String prevDayTxnData=null;

			HashMap<String,String> preTtxnDataMap=new HashMap<String, String>();

			DateUtil dateUtililty = new DateUtil();
			DateUtil dateUtililty2 = null;

			while(rs.next()){

				try{
					Serial_Number1 = rs.getString("Serial_Number");
					txnDate = rs.getDate("timeKey");

					dateUtililty2 = dateUtililty.getCurrentDateUtility(txnDate);

					week = dateUtililty2.week;

					month = rs.getInt("month");

					quarter = rs.getInt("quarter");

					year = rs.getInt("year");

					asset_type = rs.getString("asset_type");

					group_id = rs.getString("group_code");


					if(rs.getObject("agg_param_data")!=null)
						txnData=rs.getObject("agg_param_data").toString();


					if(rs.getObject("lastagg_param_data")!=null)
						prevDayTxnData=rs.getObject("lastagg_param_data").toString();

					if(txnData!=null)
						txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, String>>() {}.getType());

					if( txnDataMap.size()>0){
						customer_code = txnDataMap.get("customerCode");

						dealer_code = txnDataMap.get("customerCode");

						zonal_code = txnDataMap.get("ZonalCode");

						regionCode=txnDataMap.get("regionCode");

						msgID=txnDataMap.get("MSG_ID");
					}


					if(prevDayTxnData!=null)
						preTtxnDataMap = new Gson().fromJson(prevDayTxnData, new TypeToken<HashMap<String, String>>() {}.getType());

					if(pst.isClosed()){
						if(db2Connection==null || db2Connection.isClosed()){
							db2Connection = db2Connection = new ConnectMySQL().getProdDb2Connection();
						}
						iLogger.info(" BHL pst has closed so reopening the statement BHL------");
						pst = db2Connection.prepareStatement(insertStatement);
					}
					if(! (txnDataMap.size()>0))
						continue;

					pst.setString(1, Serial_Number1);
					pst.setDate(2, new java.sql.Date(txnDate.getTime()));
					pst.setString(3, txnDataMap.get("DSC_CON_HRS"));
					pst.setString(4, txnDataMap.get("ENG_IDL_HRS"));
					pst.setString(5, txnDataMap.get("EXA_ECO_MOD_HRS"));
					pst.setString(6, txnDataMap.get("EXA_ACT_MOD_HRS"));
					pst.setString(7, txnDataMap.get("EXA_PWR_MOD_HRS"));
					pst.setString(8, txnDataMap.get("FIX_FLOW_HRS"));
					pst.setString(9, txnDataMap.get("FWD_DIR_HRS"));
					pst.setString(10, txnDataMap.get("LOAD_EC_MOD_HRS"));
					pst.setString(11, txnDataMap.get("LOAD_JOB_HRS"));
					pst.setString(12, txnDataMap.get("LOAD_PWR_MOD_HRS"));
					pst.setString(13, txnDataMap.get("Min_Hyd_HRS"));
					pst.setString(14, txnDataMap.get("NEU_DIR_HRS"));
					pst.setString(15, txnDataMap.get("REV_DIR_HRS"));
					pst.setString(16, String.valueOf(txnDataMap.get("Hyd_Choke_Events")));
					pst.setString(17, String.valueOf(txnDataMap.get("Load_Mode_First_gear")));
					pst.setString(18, String.valueOf(txnDataMap.get("Load_Mode_ForwardDirection")));
					pst.setString(19, String.valueOf(txnDataMap.get("Load_Mode_NeutralDirection")));
					pst.setString(20, String.valueOf(txnDataMap.get("Load_Mode_NeutralGear")));
					pst.setString(21, String.valueOf(txnDataMap.get("Load_Mode_ReverseDirection")));
					pst.setString(22, String.valueOf(txnDataMap.get("Load_Mode_Second_gear")));
					pst.setString(23, String.valueOf(txnDataMap.get("No_AutoIdle_Events")));
					pst.setString(24, String.valueOf(txnDataMap.get("No_AutoOFF_Events")));
					pst.setString(25, String.valueOf(txnDataMap.get("No_Kick_Load_Mode")));
					pst.setString(26, String.valueOf(txnDataMap.get("PRB1")));
					pst.setString(27, String.valueOf(txnDataMap.get("PRB2")));
					pst.setString(28, String.valueOf(txnDataMap.get("PRB3")));
					pst.setString(29, txnDataMap.get("PRB4"));
					pst.setString(30, txnDataMap.get("PRB5"));
					pst.setString(31, txnDataMap.get("PRB6"));
					pst.setString(32, txnDataMap.get("PRBEM1"));
					pst.setString(33, txnDataMap.get("PRBEM2"));
					pst.setString(34, txnDataMap.get("PRBEM3"));
					pst.setString(35, txnDataMap.get("PRBEM4"));
					pst.setString(36, txnDataMap.get("PRBEM5"));
					pst.setString(37, txnDataMap.get("PRBEM6"));
					pst.setString(38, txnDataMap.get("PDRBEM1"));
					pst.setString(39, txnDataMap.get("PDRBEM2"));
					pst.setString(40, txnDataMap.get("PDRBEM3"));
					pst.setString(41, txnDataMap.get("PDRBEM4"));
					pst.setString(42, txnDataMap.get("PDRBEM5"));
					pst.setString(43, txnDataMap.get("PDRBEM6"));
					pst.setString(44, txnDataMap.get("PDRBLM1"));
					pst.setString(45, txnDataMap.get("PDRBLM2"));
					pst.setString(46, txnDataMap.get("PDRBLM3"));
					pst.setString(47, txnDataMap.get("PDRBLM4"));
					pst.setString(48, txnDataMap.get("PDRBLM5"));
					pst.setString(49, txnDataMap.get("PDRBLM6"));
					pst.setString(50, txnDataMap.get("Road_Mode_First_Gear"));
					pst.setString(51, txnDataMap.get("Road_Mode_Forard_Direction"));
					pst.setString(52, txnDataMap.get("Road_Mode_Fourth_Gear"));
					pst.setString(53, txnDataMap.get("Road_Mode_Neutral_Gear"));
					pst.setString(54, txnDataMap.get("Road_Mode_Reverse_Direction"));
					pst.setString(55, txnDataMap.get("Road_Mode_Second_Gear"));
					pst.setString(56, txnDataMap.get("Road_Mode_Third_Gear"));
					pst.setString(57, txnDataMap.get("Time_ActiveMode_Ex_Mode"));
					pst.setString(58, txnDataMap.get("Time_EcMode_Ex_Mode"));
					pst.setString(59, txnDataMap.get("Time_Hammer_Operation"));
					pst.setString(60, txnDataMap.get("Time_PwrMode_Ex_Mode"));

					pst.setString(61, txnDataMap.get("FUEL_PERCT"));
					pst.setString(62, txnDataMap.get("Fuel_rate"));

					pst.setString(63, preTtxnDataMap.get("TFU"));
					pst.setString(64, txnDataMap.get("TFU"));
					pst.setString(65, txnDataMap.get("TFU"));

					pst.setString(66, txnDataMap.get("FuelUsedInLPB"));
					pst.setString(67, txnDataMap.get("FuelUsedInMPB"));
					pst.setString(68, txnDataMap.get("FuelUsedInHPB"));

					double AvgFuelConsumption=0.0;

					try{
						if(txnDataMap.get("TFU")!=null && rs.getString("EndEHours")!=null){
							CMH=Double.parseDouble(rs.getString("EndEHours"));



							AvgFuelConsumption=Double.parseDouble(txnDataMap.get("TFU"))/CMH;
						}

						/*if(txnDataMap.get("TFU")!=null && preTtxnDataMap.get("TFU")!=null){
					peridTFU=Double.parseDouble(txnDataMap.get("TFU"))-Double.parseDouble(preTtxnDataMap.get("TFU"));
						}
					if(txnDataMap.get("FuelUsedInWorking")!=null){
					AvgFuelConsumption=peridTFU/(Double.parseDouble(txnDataMap.get("FuelUsedInWorking")));
					}*/

					}
					catch(Exception e){
						fLogger.fatal("BHL-Error in calculating AvgFuelConsumption"+e.getMessage());

					}

					pst.setString(69, String.valueOf(AvgFuelConsumption));

					pst.setString(70, txnDataMap.get("FuelLoss"));
					pst.setString(71, txnDataMap.get("FuelUsedInWorking"));
					pst.setString(72, preTtxnDataMap.get("L_band"));
					pst.setString(73, txnDataMap.get("L_band"));
					pst.setString(74, txnDataMap.get("L_band"));
					pst.setString(75, preTtxnDataMap.get("G_band"));
					pst.setString(76, txnDataMap.get("G_band"));
					pst.setString(77, txnDataMap.get("G_band"));
					pst.setString(78, preTtxnDataMap.get("H_band"));
					pst.setString(79, txnDataMap.get("H_band"));
					pst.setString(80, txnDataMap.get("H_band"));
					pst.setString(81, preTtxnDataMap.get("HPLUS_band"));
					pst.setString(82, txnDataMap.get("HPLUS_band"));
					pst.setString(83, txnDataMap.get("HPLUS_band"));
					pst.setString(84, preTtxnDataMap.get("TH"));
					pst.setString(85, txnDataMap.get("TH"));
					pst.setString(86, txnDataMap.get("TH"));
					pst.setString(87, preTtxnDataMap.get("SH"));
					pst.setString(88, txnDataMap.get("SH"));
					pst.setString(89, txnDataMap.get("SH"));
					pst.setString(90, preTtxnDataMap.get("Total_Hammer_Hours"));
					pst.setString(91, txnDataMap.get("Total_Hammer_Hours"));
					pst.setString(92, txnDataMap.get("Total_Hammer_Hours"));
					pst.setString(93, txnDataMap.get("HAC"));
					pst.setString(94, txnDataMap.get("Carbon_emission"));

					//pst.setString(95, txnDataMap.get("Power_boost_hours"));
					pst.setString(95, String.valueOf(decformat.format(Double.parseDouble(txnDataMap.get("Power_boost_hours")))));
					pst.setString(96, txnDataMap.get("RTT4E"));
					pst.setString(97, txnDataMap.get("WBVS"));
					pst.setString(98, txnDataMap.get("EMT"));
					pst.setString(99, txnDataMap.get("GFDU"));
					pst.setString(100, txnDataMap.get("GRDU"));
					pst.setString(101, txnDataMap.get("GLU"));
					pst.setString(102, txnDataMap.get("G2FDU"));
					pst.setString(103, txnDataMap.get("G2RDU"));
					pst.setString(104, txnDataMap.get("G2LU"));
					pst.setString(105, txnDataMap.get("G3FDU"));
					pst.setString(106, txnDataMap.get("G3RDU"));
					pst.setString(107, txnDataMap.get("G3LU"));
					pst.setString(108, txnDataMap.get("G4FDU"));
					pst.setString(109, txnDataMap.get("G4RDU"));
					pst.setString(110, txnDataMap.get("G4LU"));
					pst.setString(111, txnDataMap.get("G5FDU"));
					pst.setString(112, txnDataMap.get("G5RDU"));
					pst.setString(113, txnDataMap.get("G5LU"));
					pst.setString(114, txnDataMap.get("G6FDU"));
					pst.setString(115, txnDataMap.get("G6RDU"));
					pst.setString(116, txnDataMap.get("G6LU"));
					pst.setString(117, txnDataMap.get("Neutral_gear"));
					pst.setString(118, txnDataMap.get("EngineOnCount"));
					pst.setString(119, txnDataMap.get("EngineOffCount"));

					pst.setInt(120, partition_ID);

					pst.setInt(121, week);
					pst.setInt(122, month);
					pst.setInt(123, quarter);
					pst.setInt(124, year);
					pst.setString(125,asset_type);
					pst.setString(126,group_id);
					pst.setString(127,zonal_code);
					pst.setString(128,dealer_code);
					pst.setString(129,customer_code);

					pst.setString(130,txnDataMap.get("AHIAOHO"));
					pst.setString(131,txnDataMap.get("GGID_1"));
					pst.setString(132,txnDataMap.get("GGID_2"));
					pst.setString(133,txnDataMap.get("GGID_3"));
					pst.setString(134,txnDataMap.get("GGID_4"));
					pst.setString(135,txnDataMap.get("GGID_5"));
					pst.setString(136,txnDataMap.get("AHIEJ"));
					pst.setString(137,txnDataMap.get("AHIRJ"));

					//Df20170821 @Roopa New parameter changes Hot Engine Shut Down Count and Long Engine Idling Count

					pst.setString(138,txnDataMap.get("LEIC"));
					pst.setString(139,txnDataMap.get("HESDC"));

					pst.setString(140,regionCode); //DF20170828 @Roopa including region code in the extended table for region filter

					//Df20171113 @Roopa including msgID for BHL differentiation for reports
					pst.setString(141, msgID);

					//Df20171129 @Roopa below parameters are required to take the data at day level for accumulated parameters(VDboard)

					pst.setString(142, preTtxnDataMap.get("ENG_IDL_HRS"));
					pst.setString(143, preTtxnDataMap.get("AHIAOHO"));
					pst.setString(144, preTtxnDataMap.get("AHIRJ"));
					pst.setString(145, preTtxnDataMap.get("LOAD_JOB_HRS"));
					pst.setString(146, preTtxnDataMap.get("AHIEJ"));
					pst.setString(147, preTtxnDataMap.get("EXA_ECO_MOD_HRS"));
					pst.setString(148, preTtxnDataMap.get("EXA_ACT_MOD_HRS"));
					pst.setString(149, preTtxnDataMap.get("EXA_PWR_MOD_HRS"));
					pst.setString(150, preTtxnDataMap.get("FWD_DIR_HRS"));
					pst.setString(151, preTtxnDataMap.get("NEU_DIR_HRS"));
					pst.setString(152, preTtxnDataMap.get("REV_DIR_HRS"));
					pst.setString(153, preTtxnDataMap.get("DSC_CON_HRS"));
					pst.setString(154, preTtxnDataMap.get("Hyd_Choke_Events"));
					pst.setString(155, preTtxnDataMap.get("No_AutoIdle_Events"));
					pst.setString(156, preTtxnDataMap.get("No_AutoOFF_Events"));
					pst.setString(157, preTtxnDataMap.get("HESDC"));
					pst.setString(158, preTtxnDataMap.get("LEIC"));
					pst.setString(159, preTtxnDataMap.get("HAC"));
					
					//DF20180111 @Roopa CMS and WLS parameter's integration
					
					pst.setString(160,preTtxnDataMap.get("WMS_LBW"));
					pst.setString(161,txnDataMap.get("WMS_LBW"));
					
					pst.setString(162,preTtxnDataMap.get("WMS_CLW"));
					pst.setString(163,txnDataMap.get("WMS_CLW"));
					
					pst.setString(164,preTtxnDataMap.get("WMS_NOB"));
					pst.setString(165,txnDataMap.get("WMS_NOB"));
					
					pst.setString(166,txnDataMap.get("WMS_MNC"));
					
					pst.setString(167,preTtxnDataMap.get("CMS_ACV"));
					pst.setString(168,txnDataMap.get("CMS_ACV"));
					
					pst.setString(169,preTtxnDataMap.get("CMS_AMS_SA")); //prev day value
					pst.setString(170,txnDataMap.get("CMS_AMS_SA")); //server side accumulated value
					//END

					faultStatements.add(Serial_Number1);
					
					
					

					pst.addBatch();
				}catch(SQLException innerSQL){
					String faultInsertStmt = "insert into fault_statements(serial_number,time_key,fault_details) values('Query Exception','"+timeKey+"','"+innerSQL.getMessage()+"')";

					fLogger.fatal(" BHL fault statemet:"+innerSQL.getMessage());
					try {

						if(db2Connection==null || db2Connection.isClosed()){
							db2Connection = new ConnectMySQL().getProdDb2Connection();
						}
						localStatement = db2Connection.createStatement();
						localStatement.executeUpdate(faultInsertStmt);

						//db2Connection.commit();

					} catch (SQLException se) {
						// TODO Auto-generated catch block
						se.printStackTrace();
						fLogger.fatal("Error occured in migration:"+se.getMessage());
					} 
					innerSQL.printStackTrace();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					iLogger.info(" BHL inner Normal Exception BHL------"+e.getMessage());
					fLogger.fatal(" BHL inner fatal exception BHL"+e.getMessage());
				}
				if(bhlrow%1000 == 0)
				{
					//iLogger.info(timeKey+"roww:"+row);
					long iteratorEnd = System.currentTimeMillis();
					iLogger.info(timeKey+" BHL 1000 iterations executed in BHL: "+(iteratorEnd-iteratorStart));
					iteratorStart = System.currentTimeMillis();
					try{
						iLogger.info(timeKey+" BHL before executingbatch insertions for 1000 records BHL");
						long preparedStmtStart = System.currentTimeMillis();
						if(pst.isClosed()){
							iLogger.info(" BHL pst has closed so reopening the statement BHL------");

							if(db2Connection==null || db2Connection.isClosed()){
								db2Connection = db2Connection = new ConnectMySQL().getProdDb2Connection();
							}
							pst = db2Connection.prepareStatement(insertStatement);
						}

						pst.executeBatch();

						pst.clearBatch();

						long preparedStmtEnd = System.currentTimeMillis();
						iLogger.info(timeKey+" BHL executing batch insertions for BHL "+bhlrow+" records in "+(preparedStmtEnd-preparedStmtStart));


					}catch(BatchUpdateException bue){
						iLogger.info(" BHL inner BatchUpdateexception BHL:"+bue.getMessage());
						fLogger.fatal("Error occured in migration:"+bue.getMessage());
						bue.printStackTrace();
						int[] updatedCount = bue.getUpdateCounts();


					}
					catch(Exception e){
						e.printStackTrace();

						fLogger.fatal("Error occured in migration:"+e.getMessage());
					}

					if(db2Connection == null || db2Connection.isClosed())
						db2Connection = new ConnectMySQL().getProdDb2Connection();
					if(pst==null || pst.isClosed())
						pst = db2Connection.prepareStatement(insertStatement);
				}

				bhlrow++;

			}
			iLogger.info(timeKey+" BHL after executing while loop for query BHL");

			if(pst==null || pst.isClosed()){
				iLogger.info(" BHL pst has closed so reopening the statement BHL------");

				if(db2Connection==null || db2Connection.isClosed()){
					db2Connection = new ConnectMySQL().getProdDb2Connection();
				}
				pst = db2Connection.prepareStatement(insertStatement);
			}
			int[] updateVins = pst.executeBatch();

			pst.clearBatch();
			iLogger.info(timeKey+" BHL after executing batch insertions BHL");
			result += ": "+row;


		}
		catch(BatchUpdateException bue){
			iLogger.info("BHL outer BatchUpdateexception BHL:"+bue.getMessage());
			fLogger.fatal("Error occured in migration:"+bue.getMessage());
			bue.printStackTrace();
			int[] updatedCount = bue.getUpdateCounts();
		}
		catch (SQLException e) {

			iLogger.info("SQL Exception ------BHL"+e.getMessage());
			fLogger.fatal("Error occured in migration:"+e.getMessage());
			e.printStackTrace();
		} 
		catch(Exception e)
		{
			iLogger.info("Normal Exception ------BHL"+e.getMessage());
			fLogger.fatal("Error occured in migration:"+e.getMessage());

			String faultInsertStmt = "insert into fault_statements(serial_number,time_key,fault_details) values('normal exception','"+timeKey+"','"+e.getMessage()+"')";


			try {

				if(db2Connection==null || db2Connection.isClosed()){
					db2Connection = new ConnectMySQL().getProdDb2Connection();
				}
				localStatement = db2Connection.createStatement();
				localStatement.executeUpdate(faultInsertStmt);

				//db2Connection.commit();

			} catch (SQLException se) {
				fLogger.fatal("Error occured in migration:"+e.getMessage());
				se.printStackTrace();
			} 

			e.printStackTrace();
		}
		finally{
			try { if (rs != null) rs.close(); } catch (Exception e) {};

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("Error occured in migration:"+e.getMessage());
					e.printStackTrace();
				}
			}

			if(localStatement!=null){
				try {
					localStatement.close();
				} catch (SQLException e) {
					fLogger.fatal("Error occured in migration:"+e.getMessage());
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					fLogger.fatal("Error occured in migration:"+e.getMessage());
					e.printStackTrace();
				}
			}

			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("Error occured in migration:"+e.getMessage());
					e.printStackTrace();
				}
			}
			if(db2Connection!=null)
			{

				try {
					//db2Connection.commit();
					db2Connection.close();
				} catch (SQLException e) {
					fLogger.fatal("Error occured in migration:"+e.getMessage());
					e.printStackTrace();
				}
			}


		}
		long threadEnd1 = System.currentTimeMillis();
		int threadExecutionTime1 = (int) (((threadEnd1-threadStart1) / (1000*60)) % 60);
		iLogger.info("BHL Thread"+timeKey+" is finished... with "+result+" machines in "+threadExecutionTime1+" mins BHL");
		//iLogger.info("Thread"+timeKey+" is finished... with "+result+" machines in "+threadExecutionTime+" mins");




		//end BHL
		return result;	
	}

	public String getDayAGGData(String timeKey, String VIN){


		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String result = timeKey;
		Connection prodConnection = null;
		Statement statement = null;
		Statement localStatement = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		//iLogger.info(Thread.currentThread().getName()+" is executing day agg...");
		long threadStart = System.currentTimeMillis();
		String Serial_Number = null;
		int week = 0;
		int month = 0;
		int year = 0;
		int quarter = 0;
		double ERB1 = 0;
		double ERB2 = 0;
		double ERB3 = 0;
		double ERB4 = 0;
		double ERB5 = 0;
		double ERB6 = 0;
		double ERB7 = 0;
		String asset_type = null;
		String type_name = null;
		String group_id = null;
		String group_name = null;
		String customer_code = null;
		String customer_name = null;
		String dealer_code = null;
		String dealer_name = null;
		String zonal_code = null;
		String zonal_name=null;
		String primary_owner_code=null;
		String customer_number= null;
		String dealer_number = null;

		String regionCode=null;
		String regionName=null;
		double startEHours = 0;
		double endEHours = 0;
		double period_hours = 0;
		double latitude = 0;
		double longitude = 0;
		String address = null;
		String city = null;
		String state = null;
		double AVG_Uitlization = 0;
		double LPB = 0;
		double MPB = 0;
		double HPB = 0;
		double idle_band = 0;
		double  LPB_Perc = 0;
		double  MPB_Perc = 0;
		double  HPB_Perc = 0;
		double  idle_Perc = 0;
		double idle_time = 0;
		double working_time = 0;
		double idle_time_perc = 0;
		double working_time_perc = 0;
		double engine_on = 0;
		double engine_off = 0;
		double engine_on_perc = 0;
		double engine_off_perc = 0;
		///DF20170510 - Rajani Nagaraju - Computing nonRpmHours - Adjustment value for the difference in period HMR and running band values
		double nonRpmHours;
		int total_period_hours = 24;
		final String country = "INDIA";
		int cityId = 0;
		int stateId = 0;
		Connection db2Connection = null;
		int row=1;
		final int partition_ID =1;
		String machineNumber = null;
		List<String> faultStatements = new LinkedList<String>();
		PreparedStatement pst= null;
		Statement db2Statement = null;
		HashMap<String,Integer> statesMap = new HashMap<String, Integer>();
		HashMap<String,Integer> citiesMap = new HashMap<String, Integer>();
		String ac_type = null;
		String msgID=null;

		DecimalFormat decformat=new DecimalFormat("0.000000");
		String prevDayTxnData=null;
		HashMap<String,String> preTtxnDataMap=new HashMap<String, String>();


		try {
			iLogger.info(timeKey+" is executing day agg... with time_key "+timeKey);
			iLogger.info("getting proddb2 connection");
			db2Connection = new ConnectMySQL().getProdDb2Connection();
			iLogger.info("connected to proddb2 connection");

			try {
				Statement st = db2Connection.createStatement();
				ResultSet citiesRS = st.executeQuery("select * from City");

				while(citiesRS.next()){
					int cityID = citiesRS.getInt("CityID");
					String City = citiesRS.getString("CityName");
					citiesMap.put(City, cityID);
				}
				//citiesMap = map;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(db2Connection.isClosed())
					db2Connection = new ConnectMySQL().getProdDb2Connection();

				Statement st = db2Connection.createStatement();
				ResultSet citiesRS = st.executeQuery("select * from State");

				while(citiesRS.next()){
					int stateID = citiesRS.getInt("StateID");
					String State = citiesRS.getString("StateName");
					statesMap.put(State, stateID);
				}
				//citiesMap = map;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(db2Connection!=null)
			{

				try {
					//db2Connection.commit();
					db2Connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			long queryStartTime = System.currentTimeMillis();


			iLogger.info("thread "+timeKey+" running the query");


			String migrationQuery = "select finalDayAgg.*,"+
					"(finalDayAgg.ERB1+finalDayAgg.ERB2) as LPB,"+
					"(finalDayAgg.ERB3+finalDayAgg.ERB4+finalDayAgg.ERB5) as MPB,"+
					"(finalDayAgg.ERB6+finalDayAgg.ERB7) as HPB,"+
					"(finalDayAgg.ERB1+finalDayAgg.ERB2) as Idle_Time,"+
					"(finalDayAgg.ERB3+finalDayAgg.ERB4+finalDayAgg.ERB5+finalDayAgg.ERB6+finalDayAgg.ERB7+finalDayAgg.ERB8) as WorkingTime  , cc.*"+
					" from ("+
					" select dayagg.Serial_Number as Serial_Number,a.Machine_Number,"+
					"dayagg.Time_Key as timeKey,"+
					"Week(dayagg.Time_Key)+1 as week_no,"+
					"MONTH(dayagg.Time_Key) as month,"+
					"QUARTER(dayagg.Time_Key) as quarter,"+
					"YEAR(dayagg.Time_Key) as year,"+
					"dayagg.agg_param_data,"+
					"dayagg.EngineRunningBand1 as ERB1,dayagg.EngineRunningBand2 as ERB2,dayagg.EngineRunningBand3 as ERB3,dayagg.EngineRunningBand4 as ERB4,dayagg.EngineRunningBand5 as ERB5,dayagg.EngineRunningBand6 as ERB6,dayagg.EngineRunningBand7 as ERB7,dayagg.EngineRunningBand8 as ERB8,"+
					"a_t.Asset_Type_Code as asset_type,a_t.Asset_Type_Name as type_name,agp.asset_grp_code as group_code,ag.Asseet_Group_Name as group_name,"+
					//"ac.Account_Code as customerCode,ac.account_name as CustomerName,pa.Account_Code as parentCode,pa.account_name as DealerName,za.Account_Code as ZonalCode,za.account_name as ZonalName,ac.Account_Code as  PrimaryOwner,ac.Mobile as Customer_Number,pa.Mobile as Dealer_Number,"+
					"dayagg.Machine_Hours as EndEHours,"+
					"dayagg.Location as location,dayagg.Address as address,dayagg.City as city,dayagg.State as state" +
					//",td.Tenancy_Type_Name as ac_type" +
					" ,dayagg.Last_Engine_Run,dayagg.Last_Reported,dayagg.Engine_Status "+ 
					" from remote_monitoring_fact_data_dayagg_json_new dayagg,"+
					"asset a,products p,"+
					"asset_type a_t,asset_group ag,asset_group_profile agp "+
					//"tenancy_dimension td,account_tenancy act,"+
					//"account ac,account pa left outer join account za on za.account_id = pa.Parent_ID "+ 
					"where dayagg.time_key = '"+timeKey+"' " +
					/*		"and " +
					" Date(Created_Timestamp) = '"+timeKey+"' " +*/
					"and dayagg.Engine_Off_Hours >=0 " +
					"and " + 
					"dayagg.Serial_Number = a.Serial_Number and "+
					"p.Product_ID = a.Product_ID and "+ 
					"ag.Asset_Group_ID = p.Asset_Group_ID and "+ 
					"agp.asset_grp_id = p.Asset_Group_ID and "+ 
					"a_t.Asset_Type_ID = p.Asset_Type_ID " +
					//"and "+
					//"td.tenancy_dimension_id = dayagg.tenancy_id and "+ 
					//"act.tenancy_id = td.tenancy_id and "+ 
					//"ac.account_id = act.account_id and "+ 
					//"pa.account_id = ac.Parent_ID "+  
					") finalDayAgg left outer join"+
					" (select prevdayAgg.serial_number as lastSerial_Number, prevdayAgg.machine_hours as startEHours, prevdayAgg.time_key, prevdayAgg.agg_param_data  as lastagg_param_data"+
					" from remote_monitoring_fact_data_dayagg_json_new prevdayAgg"+ 
					" left outer join "+
					"(select max(sn.Time_key) tk,sn.serial_number ser"+
					"	from remote_monitoring_fact_data_dayagg_json_new sn"+
					" where sn.time_key < '"+timeKey+"'"+
					" group by sn.serial_number"+
					") bb"+
					" on prevdayAgg.serial_number=bb.ser"+ 
					" where prevdayAgg.Time_Key=bb.tk"+
					") cc"+
					" on finalDayAgg.serial_number=cc.lastSerial_Number ";



			if(VIN!=null && (VIN.trim().length()==7 || VIN.trim().length()==17)){
				if(VIN.trim().length()==7){
					migrationQuery = "select finalDayAgg.*,"+
							"(finalDayAgg.ERB1+finalDayAgg.ERB2) as LPB,"+
							"(finalDayAgg.ERB3+finalDayAgg.ERB4+finalDayAgg.ERB5) as MPB,"+
							"(finalDayAgg.ERB6+finalDayAgg.ERB7) as HPB,"+
							"(finalDayAgg.ERB1+finalDayAgg.ERB2) as Idle_Time,"+
							"(finalDayAgg.ERB3+finalDayAgg.ERB4+finalDayAgg.ERB5+finalDayAgg.ERB6+finalDayAgg.ERB7+finalDayAgg.ERB8) as WorkingTime  , cc.*"+
							" from ("+
							" select dayagg.Serial_Number as Serial_Number,a.Machine_Number,"+
							"dayagg.Time_Key as timeKey,"+
							"Week(dayagg.Time_Key)+1 as week_no,"+
							"MONTH(dayagg.Time_Key) as month,"+
							"QUARTER(dayagg.Time_Key) as quarter,"+
							"YEAR(dayagg.Time_Key) as year,"+
							"dayagg.agg_param_data,"+
							"dayagg.EngineRunningBand1 as ERB1,dayagg.EngineRunningBand2 as ERB2,dayagg.EngineRunningBand3 as ERB3,dayagg.EngineRunningBand4 as ERB4,dayagg.EngineRunningBand5 as ERB5,dayagg.EngineRunningBand6 as ERB6,dayagg.EngineRunningBand7 as ERB7,dayagg.EngineRunningBand8 as ERB8,"+
							"a_t.Asset_Type_Code as asset_type,a_t.Asset_Type_Name as type_name,agp.asset_grp_code as group_code,ag.Asseet_Group_Name as group_name,"+
							//"ac.Account_Code as customerCode,ac.account_name as CustomerName,pa.Account_Code as parentCode,pa.account_name as DealerName,za.Account_Code as ZonalCode,za.account_name as ZonalName,ac.Account_Code as  PrimaryOwner,ac.Mobile as Customer_Number,pa.Mobile as Dealer_Number,"+
							"dayagg.Machine_Hours as EndEHours,"+
							"dayagg.Location as location,dayagg.Address as address,dayagg.City as city,dayagg.State as state" +
							//",td.Tenancy_Type_Name as ac_type" +
							" ,dayagg.Last_Engine_Run,dayagg.Last_Reported,dayagg.Engine_Status "+ 
							" from remote_monitoring_fact_data_dayagg_json_new dayagg,"+
							"asset a,products p,"+
							"asset_type a_t,asset_group ag,asset_group_profile agp "+
							//"tenancy_dimension td,account_tenancy act,"+
							//"account ac,account pa left outer join account za on za.account_id = pa.Parent_ID "+ 
							"where dayagg.time_key = '"+timeKey+"' and " +
							/*		"and " +
								" Date(Created_Timestamp) = '"+timeKey+"' " +*/
								"dayagg.Serial_Number like '%" +VIN+"' and "+ 
								"dayagg.Engine_Off_Hours >=0 " +
								"and " + 
								"dayagg.Serial_Number = a.Serial_Number and "+
								"p.Product_ID = a.Product_ID and "+ 
								"ag.Asset_Group_ID = p.Asset_Group_ID and "+ 
								"agp.asset_grp_id = p.Asset_Group_ID and "+ 
								"a_t.Asset_Type_ID = p.Asset_Type_ID " +
								//"and "+
								//"td.tenancy_dimension_id = dayagg.tenancy_id and "+ 
								//"act.tenancy_id = td.tenancy_id and "+ 
								//"ac.account_id = act.account_id and "+ 
								//"pa.account_id = ac.Parent_ID "+  
								") finalDayAgg left outer join"+
								" (select prevdayAgg.serial_number as lastSerial_Number, prevdayAgg.machine_hours as startEHours, prevdayAgg.time_key, prevdayAgg.agg_param_data  as lastagg_param_data"+
								" from remote_monitoring_fact_data_dayagg_json_new prevdayAgg"+ 
								" left outer join "+
								"(select max(sn.Time_key) tk,sn.serial_number ser"+
								"	from remote_monitoring_fact_data_dayagg_json_new sn"+
								" where sn.time_key < '"+timeKey+"'"+
								" group by sn.serial_number"+
								") bb"+
								" on prevdayAgg.serial_number=bb.ser"+ 
								" where prevdayAgg.Time_Key=bb.tk"+
								") cc"+
								" on finalDayAgg.serial_number=cc.lastSerial_Number ";
				}

				else if(VIN.trim().length()==17){
					migrationQuery = "select finalDayAgg.*,"+
							"(finalDayAgg.ERB1+finalDayAgg.ERB2) as LPB,"+
							"(finalDayAgg.ERB3+finalDayAgg.ERB4+finalDayAgg.ERB5) as MPB,"+
							"(finalDayAgg.ERB6+finalDayAgg.ERB7) as HPB,"+
							"(finalDayAgg.ERB1+finalDayAgg.ERB2) as Idle_Time,"+
							"(finalDayAgg.ERB3+finalDayAgg.ERB4+finalDayAgg.ERB5+finalDayAgg.ERB6+finalDayAgg.ERB7+finalDayAgg.ERB8) as WorkingTime  , cc.*"+
							" from ("+
							" select dayagg.Serial_Number as Serial_Number,a.Machine_Number,"+
							"dayagg.Time_Key as timeKey,"+
							"Week(dayagg.Time_Key)+1 as week_no,"+
							"MONTH(dayagg.Time_Key) as month,"+
							"QUARTER(dayagg.Time_Key) as quarter,"+
							"YEAR(dayagg.Time_Key) as year,"+
							"dayagg.agg_param_data,"+
							"dayagg.EngineRunningBand1 as ERB1,dayagg.EngineRunningBand2 as ERB2,dayagg.EngineRunningBand3 as ERB3,dayagg.EngineRunningBand4 as ERB4,dayagg.EngineRunningBand5 as ERB5,dayagg.EngineRunningBand6 as ERB6,dayagg.EngineRunningBand7 as ERB7,dayagg.EngineRunningBand8 as ERB8,"+
							"a_t.Asset_Type_Code as asset_type,a_t.Asset_Type_Name as type_name,agp.asset_grp_code as group_code,ag.Asseet_Group_Name as group_name,"+
							//"ac.Account_Code as customerCode,ac.account_name as CustomerName,pa.Account_Code as parentCode,pa.account_name as DealerName,za.Account_Code as ZonalCode,za.account_name as ZonalName,ac.Account_Code as  PrimaryOwner,ac.Mobile as Customer_Number,pa.Mobile as Dealer_Number,"+
							"dayagg.Machine_Hours as EndEHours,"+
							"dayagg.Location as location,dayagg.Address as address,dayagg.City as city,dayagg.State as state" +
							//",td.Tenancy_Type_Name as ac_type" +
							" ,dayagg.Last_Engine_Run,dayagg.Last_Reported,dayagg.Engine_Status "+ 
							" from remote_monitoring_fact_data_dayagg_json_new dayagg,"+
							"asset a,products p,"+
							"asset_type a_t,asset_group ag,asset_group_profile agp "+
							//"tenancy_dimension td,account_tenancy act,"+
							//"account ac,account pa left outer join account za on za.account_id = pa.Parent_ID "+ 
							"where dayagg.time_key = '"+timeKey+"' and " +
							/*		"and " +
								" Date(Created_Timestamp) = '"+timeKey+"' " +*/
								"dayagg.Serial_Number = '" +VIN+"' and "+
								"dayagg.Engine_Off_Hours >=0 " +
								"and " + 
								"dayagg.Serial_Number = a.Serial_Number and "+
								"p.Product_ID = a.Product_ID and "+ 
								"ag.Asset_Group_ID = p.Asset_Group_ID and "+ 
								"agp.asset_grp_id = p.Asset_Group_ID and "+ 
								"a_t.Asset_Type_ID = p.Asset_Type_ID " +
								//"and "+
								//"td.tenancy_dimension_id = dayagg.tenancy_id and "+ 
								//"act.tenancy_id = td.tenancy_id and "+ 
								//"ac.account_id = act.account_id and "+ 
								//"pa.account_id = ac.Parent_ID "+  
								") finalDayAgg left outer join"+
								" (select prevdayAgg.serial_number as lastSerial_Number, prevdayAgg.machine_hours as startEHours, prevdayAgg.time_key, prevdayAgg.agg_param_data  as lastagg_param_data"+
								" from remote_monitoring_fact_data_dayagg_json_new prevdayAgg"+ 
								" left outer join "+
								"(select max(sn.Time_key) tk,sn.serial_number ser"+
								"	from remote_monitoring_fact_data_dayagg_json_new sn"+
								" where sn.time_key < '"+timeKey+"'"+
								" group by sn.serial_number"+
								") bb"+
								" on prevdayAgg.serial_number=bb.ser"+ 
								" where prevdayAgg.Time_Key=bb.tk"+
								") cc"+
								" on finalDayAgg.serial_number=cc.lastSerial_Number ";
				}
			}

			iLogger.info("migrationQuery Roopa:: "+migrationQuery);
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			rs = statement.executeQuery(migrationQuery);
			long queryEndTime = System.currentTimeMillis();
			if(rs != null){
				rs.last();
				int rowCount = rs.getRow();
				rs.beforeFirst();
				//System.out.println("total records for thread "+timeKey+" are:"+rowCount);
				iLogger.info("query for total records for thread "+timeKey+" are:"+rowCount+" exceuted in "+(queryEndTime - queryStartTime));
				iLogger.info("thread "+timeKey+" started inserting :"+rowCount+" records");
			}

			//DF20170510 - Rajani Nagaraju - Computing nonRpmHours - Adjustment value for the difference in period HMR and running band values
			
			//Df20180112 @Roopa CMS and WLS integration
			/*String insertStatement = "  insert into factInsight_dayAgg(AssetID,TxnDate,TxnWeek,TxnMonth,TxnQuarter,TxnYear,"+
					"ModelCode,ModelName,ProfileCode,ProfileName,ZonalCode,ZonalName,DealerCode,DealerName,CustCode,CustomerName,DealerContact,CustContact,PrimaryOwner," +
					"Latitude,Longitude,Address,StateId,State,CityId,City,Country," +
					"StartEngRunHrs,FinishEngRunHrs,PeriodHMR,AvgUtilizationPerct,ERB1,ERB2,ERB3,ERB4,ERB5,ERB6,ERB7,"+
					"PowerBandLow,PowerBandMed,PowerBandHigh,PowerBandIdle,IdleTime,WorkingTime,EngineOnTime,EngineOffTime,"+
					"EngineOnPerct,EngineOffPerct,IdleTimePerct,WorkingTimePerct,LowPowerBandPerct,MedPowerBandPerct,HighPowerBandPerct,IdlePowerBandPerct,PartitionID,MachineNum" +
					",LastEngineRun,LastReported,LastEngineStatus,Column1,Column2,Column3,Column4" +
					",Column5,Column6,Column7,Column8,Column9,Column10,Column11,Column12,Column13,Column14,Column15,Column16,Column17,Column18,Column19,Column20,FuelRate,StartFuelInLtrs,FinishFuelInLtrs,FuelUsedInLtrs) "+
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			*/
			
			String insertStatement = "  insert into factInsight_dayAgg(AssetID,TxnDate,TxnWeek,TxnMonth,TxnQuarter,TxnYear,"+
					"ModelCode,ModelName,ProfileCode,ProfileName,ZonalCode,ZonalName,DealerCode,DealerName,CustCode,CustomerName,DealerContact,CustContact,PrimaryOwner," +
					"Latitude,Longitude,Address,StateId,State,CityId,City,Country," +
					"StartEngRunHrs,FinishEngRunHrs,PeriodHMR,AvgUtilizationPerct,ERB1,ERB2,ERB3,ERB4,ERB5,ERB6,ERB7,"+
					"PowerBandLow,PowerBandMed,PowerBandHigh,PowerBandIdle,IdleTime,WorkingTime,EngineOnTime,EngineOffTime,"+
					"EngineOnPerct,EngineOffPerct,IdleTimePerct,WorkingTimePerct,LowPowerBandPerct,MedPowerBandPerct,HighPowerBandPerct,IdlePowerBandPerct,PartitionID,MachineNum" +
					",LastEngineRun,LastReported,LastEngineStatus,Column1,Column2,Column3,Column4" +
					",Column5,Column6,Column7,Column8,Column9,Column10,Column11,Column12,Column13,Column14,Column15,Column16,Column17,Column18,Column19,Column20,FuelRate,StartFuelInLtrs,FinishFuelInLtrs,FuelUsedInLtrs"+
					",FuelUsedInLPB,FuelUsedInMPB,FuelUsedInHPB,AvgFuelConsumption,FuelLoss,FuelUsedInWorking,StartLMode,FinishLMode"+
					",LModeInHrs,StartGMode,FinishGMode,GModeInHrs,StartHMode,FinishHMode,HModeInHrs,StartHPlusMode,FinishHPlusMode,HPlusModeInHrs,StartTravellingTime"+
					",FinishTravellingTime,TravellingTimeInHrs,StartSlewTime,FinishSlewTime,SlewTimeInHrs,StartHammerUseTime,FinishHammerUseTime"+
					",HammerUseTimeInHrs,HammerAbuseCount,CarbonEmission,PowerBoostTime,RegenerationTime,OperatorOutOfSeatCount,AverageVehicleSpeed,EngineManufacturer"+
					",Gear1FwdUtilization,Gear1BkwdUtilization,Gear1LockupUtilization,Gear2FwdUtilization,Gear2BkwdUtilization,Gear2LockupUtilization"+
					",Gear31FwdUtilization,Gear3BkwdUtilization,Gear3LockupUtilization,Gear4FwdUtilization,Gear4BkwdUtilization,Gear4LockupUtilization,Gear5FwdUtilization,Gear5BkwdUtilization,Gear5LockupUtilization,Gear6FwdUtilization) "+
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
					"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			
			long endQueryTime = System.currentTimeMillis();
			
			//CMS and WLS integration inserting into unused columns
			
			/*Frequency Value(CMS_FV)Column5-> Column6
			Amplitude Value(CMS_AV) Column7-> Column8
			Compaction Value(CMS_CV) Column9-> Column10
			Double Jump(CMS_DJ) Column11->Column12(server accumulated value)
			High Frequency Vibration Hours(CMS_HFVH) Column13 -> Column14
			Low Frequency Vibration Hours(CMS_LFVH) Column15 -> Column16
			Direction Index(CMS_DI) Column17
			Vibration Status ON/OFF(CMS_VSOO) Column18 -> Column19(server accumulated value)
			Roller Pass Number(CMS_RPN) Column20
			Manual Mode Status(CMS_MMS) FuelRate -> StartFuelInLtrs(server accumulated value for Manual Mode Status)
			Static Pass Hours(CMS_SPH) FinishFuelInLtrs-> FuelUsedInLtrs(finish Static Pass Hours)*/

			iLogger.info(timeKey+" thread execution time for query"+((int)(((endQueryTime-queryStartTime)/1000)%60))+"");
			//System.out.println(timeKey+" thread execution time for query"+((int)(((endQueryTime-queryStartTime)/1000)%60))+"");
			if(db2Connection == null || db2Connection.isClosed())
				db2Connection = new ConnectMySQL().getProdDb2Connection();
			pst = db2Connection.prepareStatement(insertStatement);

			iLogger.info(timeKey+"before executing while loop for query");
			//System.out.println(timeKey+"before executing while loop for query");
			long iteratorStart = System.currentTimeMillis();

			DateUtil dateUtililty = new DateUtil();
			DateUtil dateUtililty2 = null;

			HashMap<String,String> txnDataMap=new HashMap<String, String>();

			String txnData=null;

			while(rs.next()){
				//iLogger.info(timeKey+"Inside rs.next Roopa");	


				try{
					Serial_Number = rs.getString("Serial_Number");
					machineNumber = rs.getString("Machine_Number");
					Date txnDate = rs.getDate("timeKey");
					dateUtililty2 = dateUtililty.getCurrentDateUtility(txnDate);

					week = dateUtililty2.week;
					//week = rs.getInt("week_no");

					month = rs.getInt("month");

					quarter = rs.getInt("quarter");

					year = rs.getInt("year");

					//DF20170510 @Roopa removing decimal truncation

				


					ERB1=Double.parseDouble(decformat.format(rs.getDouble("ERB1")));

					ERB2 = Double.parseDouble(decformat.format(rs.getDouble("ERB2")));

					ERB3 = Double.parseDouble(decformat.format(rs.getDouble("ERB3")));

					ERB4 = Double.parseDouble(decformat.format(rs.getDouble("ERB4")));

					ERB5 = Double.parseDouble(decformat.format(rs.getDouble("ERB5")));

					ERB6 = Double.parseDouble(decformat.format(rs.getDouble("ERB6")));

					ERB7 = Double.parseDouble(decformat.format(rs.getDouble("ERB7")));




					asset_type = rs.getString("asset_type");

					type_name = rs.getString("type_name");

					group_id = rs.getString("group_code");

					group_name = rs.getString("group_name");

					

					//DF20170510 @Roopa removing decimal truncation

					startEHours =rs.getDouble("StartEHours");

				

					endEHours = rs.getDouble("EndEHours");

					if(startEHours != 0){
						//period_hours = new BigDecimal((endEHours - startEHours)).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();
						period_hours = endEHours - startEHours;
					}
					else{
						
						period_hours = endEHours;
					}
					AVG_Uitlization =(period_hours/24)*100;



					LPB = Double.parseDouble(decformat.format(rs.getDouble("LPB")));
					MPB = Double.parseDouble(decformat.format(rs.getDouble("MPB")));
					HPB = Double.parseDouble(decformat.format(rs.getDouble("HPB")));
					idle_band = LPB;


					idle_time = Double.parseDouble(decformat.format(rs.getDouble("Idle_Time")));

					working_time = Double.parseDouble(decformat.format(rs.getDouble("WorkingTime")));

					//DF20170821 @Roopa adding ERB8 to ERB 11 for working time calculations for the machines which has data

					if(rs.getObject("agg_param_data")!=null)
						txnData=rs.getObject("agg_param_data").toString();

					if(txnData!=null)
						txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, String>>() {}.getType());
					
					if(rs.getObject("lastagg_param_data")!=null)
						prevDayTxnData=rs.getObject("lastagg_param_data").toString();

					if(prevDayTxnData!=null)
						preTtxnDataMap = new Gson().fromJson(prevDayTxnData, new TypeToken<HashMap<String, String>>() {}.getType());

					if(txnDataMap!=null && txnDataMap.size()>0){

						if(txnDataMap.get("ERB9")!=null && txnDataMap.get("ERB10")!=null && txnDataMap.get("ERB11")!=null){
							working_time=working_time+Double.parseDouble(txnDataMap.get("ERB9"))+Double.parseDouble(txnDataMap.get("ERB10"))+Double.parseDouble(txnDataMap.get("ERB11"));	
							working_time=Double.parseDouble(decformat.format(working_time));
						}

						customer_code = txnDataMap.get("customerCode");

						customer_name = txnDataMap.get("CustomerName");

						dealer_code = txnDataMap.get("dealerCode");

						dealer_name = txnDataMap.get("DealerName");


						zonal_code = txnDataMap.get("ZonalCode");

						zonal_name = txnDataMap.get("ZonalName");


						primary_owner_code = txnDataMap.get("PrimaryOwner");

						customer_number =txnDataMap.get("Customer_Number");

						dealer_number = txnDataMap.get("Dealer_Number");

						regionCode=txnDataMap.get("regionCode");
						regionName=txnDataMap.get("regionName");
						msgID=txnDataMap.get("MSG_ID");

					}

					engine_on = Double.parseDouble(decformat.format(idle_time + working_time));
					engine_off = Double.parseDouble(decformat.format(total_period_hours - engine_on));

					if(period_hours<0 || period_hours>=24)
						period_hours = engine_on;
					
					if(engine_on != 0)
					{

						LPB_Perc = Double.parseDouble(decformat.format((LPB/engine_on)*100));
						MPB_Perc = Double.parseDouble(decformat.format((MPB/engine_on)*100));
						HPB_Perc = Double.parseDouble(decformat.format((HPB/engine_on)*100));

						idle_Perc = LPB_Perc;
					}


					if(rs.getString("location")!=null)
					{
						String location = rs.getString("location");
						String[] lat_long = location.split(",");
						latitude = Double.parseDouble(lat_long[0]);
						longitude = Double.parseDouble(lat_long[1]);
					}
					address = rs.getString("address");
					city = rs.getString("city");
					state = rs.getString("state");
					if(address == null || state == null){
						iLogger.info("getDayAGGData : state is null so abnormal termination ");
					//	break;
						return "FAILURE"; //DF20180212 @Roopa returning failure if any of the address is null, so that not migrating to extended table also.
					}
					if(state !=null && city != null){

						if(state!=null){
							if(!statesMap.isEmpty() && statesMap.containsKey(state)){

								stateId = statesMap.get(state);
								//System.out.println("Map stateId:"+state);
							}
							else{
								try{

									if(db2Connection.isClosed())
										db2Connection = new ConnectMySQL().getProdDb2Connection();

									db2Statement = db2Connection.createStatement();
									db2Statement.executeUpdate("insert into State(StateName,Country) values('"+state+"','"+country+"')");

									rs1 = db2Statement.executeQuery("select * from State where StateName like '"+state+"'");
									if(rs1.next()){
										stateId = rs1.getInt("StateID");
										//System.out.println("DB stateId:"+state);
										statesMap.put(state,stateId);
									}
								}catch(SQLException e){
									try {

										localStatement = db2Connection.createStatement();
										rs1 = localStatement.executeQuery("select * from State where StateName like '"+state+"'");
										if(rs1.next()){
											stateId = rs1.getInt("StateID");
											//System.out.println("DB stateId:"+state);
											statesMap.put(state,stateId);
										}

									} catch (SQLException se) {
										// TODO Auto-generated catch block
										se.printStackTrace();
									} 
								}
							}
						}
						if(city!=null){
							if(!citiesMap.isEmpty() && citiesMap.containsKey(city)){
								cityId = citiesMap.get(city);
								//System.out.println("Map cityId:"+city);
							}
							else {
								try{

									if(db2Connection.isClosed())
										db2Connection = new ConnectMySQL().getProdDb2Connection();

									db2Statement = db2Connection.createStatement();
									db2Statement.executeUpdate("insert into City(CityName,StateID) values('"+city+"',"+stateId+")");

									rs2 = db2Statement.executeQuery("select c.StateID,c.CityID from City c where c.CityName like '"+city+"'");
									if(rs2.next())
									{
										stateId = rs2.getInt("StateID");
										cityId = rs2.getInt("CityID");
										//System.out.println("DB cityId:"+city);
										citiesMap.put(city, cityId);
									}
								}catch(SQLException e){


									try {

										localStatement = db2Connection.createStatement();
										rs2 = db2Statement.executeQuery("select c.StateID,c.CityID from City c where c.CityName like '"+city+"'");
										if(rs2.next()){

											stateId = rs2.getInt("StateID");
											cityId = rs2.getInt("CityID");
											//System.out.println("DB cityId:"+city);
											citiesMap.put(city, cityId);
										}

									} catch (SQLException se) {
										// TODO Auto-generated catch block
										se.printStackTrace();
									} 
								}
							}
						}
					}



					idle_time_perc = Double.parseDouble(decformat.format((idle_time/total_period_hours)*100));
					working_time_perc = Double.parseDouble(decformat.format((working_time_perc/total_period_hours)*100));

					engine_on_perc = Double.parseDouble(decformat.format((engine_on/total_period_hours)*100));
					engine_off_perc = Double.parseDouble(decformat.format(100 - engine_on_perc));


					if(pst.isClosed()){
						if(db2Connection.isClosed()){
							db2Connection = db2Connection = new ConnectMySQL().getProdDb2Connection();
						}
						iLogger.info("pst has closed so reopening the statement ------");
						pst = db2Connection.prepareStatement(insertStatement);
					}

					pst.setString(1, Serial_Number);
					//	stmtValues += "'"+Serial_Number+"',";
					pst.setDate(2, new java.sql.Date(txnDate.getTime()));
					//stmtValues += "'"+new java.sql.Date(sdf.parse(timeKey).getTime())+"',";

					pst.setInt(3, week);
					//stmtValues += week+",";
					pst.setInt(4, month);
					//stmtValues += month+",";
					pst.setInt(5, quarter);
					//stmtValues += quarter+",";
					pst.setInt(6, year);
					//stmtValues += year+",";
					pst.setString(7, asset_type);
					//stmtValues += asset_type+",";
					pst.setString(8, type_name);
					//stmtValues += "'"+type_name+"',";
					pst.setString(9, group_id);
					//stmtValues += group_id+",";
					pst.setString(10,group_name);
				

					pst.setString(11, zonal_code);
					pst.setString(12, zonal_name);
					pst.setString(13, dealer_code);
					pst.setString(14, dealer_name);
					pst.setString(15, customer_code);
					pst.setString(16, customer_name);
					pst.setString(17, dealer_number);
					pst.setString(18, customer_number);
					pst.setString(19, primary_owner_code);



					pst.setDouble(20, latitude);
					//stmtValues += latitude+",";
					pst.setDouble(21, longitude);
					//stmtValues += longitude+",";
					pst.setString(22,address);
					//stmtValues += "'"+address+"',";
					pst.setInt(23,stateId);
					//stmtValues += stateId+",";
					pst.setString(24,state);
					pst.setInt(25,cityId);
					//stmtValues += cityId+",";
					pst.setString(26,city);
					//stmtValues += "'"+state+"',";
					pst.setString(27,country);
					//stmtValues += "'"+country+"',";
					pst.setDouble(28, startEHours);
					//stmtValues += startEHours+",";
					pst.setDouble(29, endEHours);
					//stmtValues += endEHours+",";
					pst.setDouble(30, period_hours);
					//stmtValues += period_hours+",";
					pst.setDouble(31, AVG_Uitlization);
					//stmtValues += AVG_Uitlization+",";
					pst.setDouble(32, ERB1);
					//stmtValues += ERB1+",";
					pst.setDouble(33, ERB2);
					//stmtValues += ERB2+",";
					pst.setDouble(34, ERB3);
					//stmtValues += ERB3+",";
					pst.setDouble(35, ERB4);
					//stmtValues += ERB4+",";
					pst.setDouble(36, ERB5);
					//stmtValues += ERB5+",";
					pst.setDouble(37, ERB6);
					//stmtValues += ERB6+",";
					pst.setDouble(38, ERB7);
					//stmtValues += ERB7+",";

					pst.setDouble(39, LPB);
					//stmtValues += LPB+",";
					pst.setDouble(40, MPB);
					//stmtValues += MPB+",";
					pst.setDouble(41, HPB);
					//stmtValues += HPB+",";
					pst.setDouble(42, idle_band);
					//	stmtValues += idle_band+",";
					pst.setDouble(43, idle_time);
					//stmtValues += idle_time+",";
					pst.setDouble(44, working_time);
					//stmtValues += working_time+",";
					pst.setDouble(45, engine_on);
					//stmtValues += engine_on+",";
					pst.setDouble(46, engine_off);
					//stmtValues += engine_off+",";
					pst.setDouble(47,engine_on_perc);
					//stmtValues += engine_on_perc+",";
					pst.setDouble(48,engine_off_perc);
					//stmtValues += engine_off_perc+",";
					pst.setDouble(49, idle_time_perc);
					//stmtValues += idle_time_perc+",";
					pst.setDouble(50,working_time_perc);
					//stmtValues += working_time_perc+",";
					pst.setDouble(51,LPB_Perc);
					//stmtValues += LPB_Perc+",";
					pst.setDouble(52,MPB_Perc);
					//stmtValues += MPB_Perc+",";
					pst.setDouble(53,HPB_Perc);
					//stmtValues += HPB_Perc+",";
					pst.setDouble(54,idle_Perc);
					//stmtValues += idle_Perc+",";
					//stmtValues += idle_Perc;
					pst.setInt(55, partition_ID);
					//stmtValues += ","+partition_ID;
					pst.setString(56, machineNumber);
					pst.setTimestamp(57, rs.getTimestamp("Last_Engine_Run"));

					pst.setTimestamp(58, rs.getTimestamp("Last_Reported"));

					pst.setInt(59, rs.getInt("Engine_Status"));

					//DF20170510 - Rajani Nagaraju - Computing nonRpmHours - Adjustment value for the difference in period HMR and running band values
					nonRpmHours = period_hours - (ERB1+ERB2+ERB3+ERB4+ERB5+ERB6+ERB7);
					pst.setDouble(60, nonRpmHours);

					//DF20170823 @ROOpa inclusion of region

					pst.setString(61, regionCode);
					pst.setString(62, regionName);

					//DF20171113 @Roopa Including msg ID for BHL differentiation at reports side

					pst.setString(63, msgID);
					
					//Df20180112 @Roopa CMS and WLS integration
					
					pst.setString(64, preTtxnDataMap.get("CMS_FV"));
					pst.setString(65, txnDataMap.get("CMS_FV"));
					
					pst.setString(66, preTtxnDataMap.get("CMS_AV"));
					pst.setString(67, txnDataMap.get("CMS_AV"));
					
					pst.setString(68, preTtxnDataMap.get("CMS_CV"));
					pst.setString(69, txnDataMap.get("CMS_CV"));
					
					pst.setString(70, preTtxnDataMap.get("CMS_DJ_SA")); //prev day value
					pst.setString(71, txnDataMap.get("CMS_DJ_SA")); //server side accumulated value
					
					pst.setString(72, preTtxnDataMap.get("CMS_HFVH"));
					pst.setString(73, txnDataMap.get("CMS_HFVH"));
					
					pst.setString(74, preTtxnDataMap.get("CMS_LFVH"));
					pst.setString(75, txnDataMap.get("CMS_LFVH"));
					
					pst.setString(76, txnDataMap.get("CMS_DI"));
					
					pst.setString(77, preTtxnDataMap.get("CMS_VSOO_SA")); //prev day value
					pst.setString(78, txnDataMap.get("CMS_VSOO_SA")); //server side accumulated value
					
					
					pst.setString(79, txnDataMap.get("CMS_RPN"));
					
					pst.setString(80, preTtxnDataMap.get("CMS_MMS_SA")); //prev day value
					pst.setString(81, txnDataMap.get("CMS_MMS_SA")); //server side accumulated value
					
					pst.setString(82, preTtxnDataMap.get("CMS_SPH"));
					pst.setString(83, txnDataMap.get("CMS_SPH"));
					
					//DF20180227 @Roopa GENSET param integration
					
					pst.setString(84, txnDataMap.get("LOAD_BLOCK1"));
					pst.setString(85, txnDataMap.get("LOAD_BLOCK2"));
					pst.setString(86, txnDataMap.get("LOAD_BLOCK3"));
					pst.setString(87, txnDataMap.get("LOAD_BLOCK4"));
					pst.setString(88, txnDataMap.get("LOAD_BLOCK5"));
					pst.setString(89, txnDataMap.get("LOAD_BLOCK6"));
					pst.setString(90, txnDataMap.get("UTIL_BC_OFF"));
					pst.setString(91, txnDataMap.get("UTIL_BC_ON"));
					pst.setString(92, preTtxnDataMap.get("KVA_HOURS"));
					pst.setString(93, txnDataMap.get("KVA_HOURS"));
					pst.setString(94, preTtxnDataMap.get("KW_HOURS"));
					pst.setString(95, txnDataMap.get("KW_HOURS"));
					pst.setString(96, preTtxnDataMap.get("Fuel_Used_at_the_Reporting_Time"));
					pst.setString(97, txnDataMap.get("Fuel_Used_at_the_Reporting_Time"));
					pst.setString(98, preTtxnDataMap.get("Fuel_used_at_Very_Low_Load"));
					pst.setString(99, txnDataMap.get("Fuel_used_at_Very_Low_Load"));
					pst.setString(100, preTtxnDataMap.get("Fuel_used_at_Low_Load"));
					pst.setString(101, txnDataMap.get("Fuel_used_at_Low_Load"));
					pst.setString(102, preTtxnDataMap.get("Fuel_used_at_Medium_Load"));
					pst.setString(103, txnDataMap.get("Fuel_used_at_Medium_Load"));
					pst.setString(104, preTtxnDataMap.get("Fuel_used_at_High_Load"));
					pst.setString(105, txnDataMap.get("Fuel_used_at_High_Load"));
					pst.setString(106, preTtxnDataMap.get("Fuel_used_at_Very_High_Load"));
					pst.setString(107, txnDataMap.get("Fuel_used_at_Very_High_Load"));
					pst.setString(108, preTtxnDataMap.get("Fuel_level_at_the_end_of_the_reporting_period"));
					pst.setString(109, txnDataMap.get("Fuel_level_at_the_end_of_the_reporting_period"));
					pst.setString(110, txnDataMap.get("GEN_BLK_VOLTAGE_PHASEA"));
					pst.setString(111, txnDataMap.get("GEN_VOLTAGE_PHASEB"));
					pst.setString(112, txnDataMap.get("GEN_VOLTAGE_PHASEC"));
					pst.setString(113, txnDataMap.get("GEN_CURRENT_PHASEA"));
					pst.setString(114, txnDataMap.get("GEN_CURRENT_PHASEB"));
					pst.setString(115, txnDataMap.get("GEN_CURRENT_PHASEC"));
					pst.setString(116, txnDataMap.get("GEN_AC_VOLTAGE"));
					pst.setString(117, txnDataMap.get("GEN_NEUTRAL_AC_RMS_VOLTAGE"));
					pst.setString(118, txnDataMap.get("GEN_PHASEA_LINE_VOLTAGE"));
					pst.setString(119, txnDataMap.get("GEN_PHASEB_LINE_VOLTAGE"));
					pst.setString(120, txnDataMap.get("GEN_PHASEC_LINE_VOLTAGE"));
					pst.setString(121, txnDataMap.get("GEN_PHASEC_LINE_NEUTRAL_VOLTAGE"));
					pst.setString(122, txnDataMap.get("GEN_FREQUENCY"));
					pst.setString(123, txnDataMap.get("overall_Power_Factor"));
					pst.setString(124, txnDataMap.get("load_on_phaseA"));
					pst.setString(125, txnDataMap.get("load_on_phaseB"));
					pst.setString(126, txnDataMap.get("load_on_phaseC"));
					
					pst.setString(127, txnDataMap.get("GEN_PHASEB_LINE_NEUTRAL_VOLTAGE"));
					pst.setString(128, txnDataMap.get("Engine_Coolant_temp"));
					pst.setString(129, txnDataMap.get("Gen_Oil_Pressure"));
					
					//DF20180314 @Roopa Adding missed columns for the CAN model
					pst.setString(130, txnDataMap.get("LEPB"));
					pst.setString(131, txnDataMap.get("IEPB"));
					pst.setString(132, txnDataMap.get("MEPB"));
					pst.setString(133, txnDataMap.get("HEPB"));
					
					
					faultStatements.add(Serial_Number);
					


					pst.addBatch();
				}catch(SQLException innerSQL){
					String faultInsertStmt = "insert into fault_statements(serial_number,time_key,fault_details) values('Query Exception','"+timeKey+"','"+innerSQL.getMessage()+"')";

					fLogger.fatal("fault statemet:"+innerSQL.getMessage());
					try {
						//ConnectMySQL connMySql = new ConnectMySQL();
						//devConnection = connMySql.getConnection();
						if(db2Connection.isClosed()){
							db2Connection = new ConnectMySQL().getProdDb2Connection();
						}
						localStatement = db2Connection.createStatement();
						localStatement.executeUpdate(faultInsertStmt);

						//db2Connection.commit();

					} catch (SQLException se) {
						// TODO Auto-generated catch block
						se.printStackTrace();
						fLogger.fatal("Error occured in migration:"+se.getMessage());
					} 
					innerSQL.printStackTrace();
				}
				catch(Exception e)
				{
					iLogger.info("inner Normal Exception ------");
					fLogger.fatal("inner fatal exception "+e.getMessage());
					e.printStackTrace();
				}
				if(row%1000 == 0)
				{
					//System.out.println(timeKey+"roww:"+row);
					long iteratorEnd = System.currentTimeMillis();
					iLogger.info(timeKey+" 1000 iterations executed in: "+(iteratorEnd-iteratorStart));
					iteratorStart = System.currentTimeMillis();
					try{
						iLogger.info(timeKey+"before executingbatch insertions for 1000 records");
						long preparedStmtStart = System.currentTimeMillis();
						if(pst.isClosed()){
							iLogger.info("pst has closed so reopening the statement ------");

							if(db2Connection.isClosed()){
								db2Connection = db2Connection = new ConnectMySQL().getProdDb2Connection();
							}
							pst = db2Connection.prepareStatement(insertStatement);
						}

						pst.executeBatch();

						pst.clearBatch();


						long preparedStmtEnd = System.currentTimeMillis();
						iLogger.info(timeKey+" executing batch insertions for "+row+" records in "+(preparedStmtEnd-preparedStmtStart));

					}catch(BatchUpdateException bue){
						iLogger.info(" inner BatchUpdateexception::"+bue.getMessage());
						bue.printStackTrace();
						fLogger.fatal("Error occured in migration:"+bue.getMessage());

					}
					catch(Exception e){
						e.printStackTrace();
						fLogger.fatal("Error occured in migration:"+e.getMessage());
					}

					if(db2Connection == null || db2Connection.isClosed())
						db2Connection = new ConnectMySQL().getProdDb2Connection();
					if(pst==null || pst.isClosed())
						pst = db2Connection.prepareStatement(insertStatement);

				}

				row++;


			}
			iLogger.info(timeKey+"after executing while loop for query");

			if(db2Connection == null || db2Connection.isClosed())
				db2Connection = new ConnectMySQL().getProdDb2Connection();
			if(pst==null || pst.isClosed())
				pst = db2Connection.prepareStatement(insertStatement);

			int[] updateVins = pst.executeBatch();

			pst.clearBatch();
			iLogger.info(timeKey+"after executing batch insertions");
			result += ": "+row;

		}
		catch(BatchUpdateException bue){
			iLogger.info("outer BatchUpdateexception:"+bue.getMessage());
			fLogger.fatal("outer BatchUpdateexception:"+bue.getMessage());
			bue.printStackTrace();
			int[] updatedCount = bue.getUpdateCounts();


		}
		catch (SQLException e) {

			iLogger.info("SQL Exception ------"+e.getMessage());
			fLogger.fatal("SQL Exception:"+e.getMessage());
			e.printStackTrace();
		} 
		catch(Exception e)
		{
			iLogger.info("Normal Exception ------"+e.getMessage());
			fLogger.fatal("Normal Exception:"+e.getMessage());

			String faultInsertStmt = "insert into fault_statements(serial_number,time_key,fault_details) values('normal exception','"+timeKey+"','"+e.getMessage()+"')";


			try {

				if(db2Connection.isClosed()){
					db2Connection = new ConnectMySQL().getProdDb2Connection();
				}
				localStatement = db2Connection.createStatement();
				localStatement.executeUpdate(faultInsertStmt);

				//db2Connection.commit();

			} catch (SQLException se) {
				// TODO Auto-generated catch block
				se.printStackTrace();
				fLogger.fatal("Exception:"+e.getMessage());
			} 

			e.printStackTrace();
		}
		finally{
			try { if (rs != null) rs.close(); } catch (Exception e) {};
			try { if (rs1 != null) rs1.close(); } catch (Exception e) {};
			try { if (rs2 != null) rs2.close(); } catch (Exception e) {};
			if(localStatement!=null){
				try {
					localStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fLogger.fatal("Error occured in migration:"+e.getMessage());
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fLogger.fatal("Error occured in migration:"+e.getMessage());
				}
			}

			if(db2Statement!=null){
				try {
					db2Statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fLogger.fatal("Error occured in migration:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fLogger.fatal("Error occured in migration:"+e.getMessage());
				}
			}
			if(db2Connection!=null)
			{

				try {
					//db2Connection.commit();
					db2Connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fLogger.fatal("Error occured in migration:"+e.getMessage());
				}
			}


		}
		long threadEnd = System.currentTimeMillis();
		int threadExecutionTime = (int) (((threadEnd-threadStart) / (1000*60)) % 60);
		iLogger.info("Thread"+timeKey+" is finished... with "+result+" machines in "+threadExecutionTime+" mins");
		//System.out.println("Thread"+timeKey+" is finished... with "+result+" machines in "+threadExecutionTime+" mins");

		//end BHL


		return result;
	}



	public String getDayAGGDataUpdateBHL(String timeKey, String VIN){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String result = timeKey;
		Connection prodConnection = null;
		Statement statement = null;
		Statement queryByCreatedTSstatement = null;
		Statement localStatement = null;
		ResultSet rs = null;

		int week = 0;
		int month = 0;
		int year = 0;
		int quarter = 0;

		String asset_type = null;
		String group_id = null;
		String customer_code = null;

		String dealer_code = null;
		String zonal_code = null;
		String regionCode=null;

		Connection db2Connection = null;
		final int partition_ID =1;

		List<String> faultStatements = new LinkedList<String>();
		PreparedStatement pst= null;
		PreparedStatement pst1= null;
		Statement db2Statement = null;

		String ac_type = null;
		double CMH=0;

		//start BHL update

		long threadStart1 = System.currentTimeMillis();
		int bhlrow=1;

		ResultSet updateRS=null;
		Statement updatest=null;
		double AvgFuelConsumption=0.0;
		ResultSet queryByCreatedTSRS=null;

		DateUtil dateUtililty = new DateUtil();
		DateUtil dateUtililty2 = null;

		HashMap<String,String> txnDataMap=new HashMap<String, String>();

		String txnData=null;
		String Serial_Number1=null;
		Date txnDate=null;
		String prevDayTxnData=null;
		HashMap<String,String> preTtxnDataMap=new HashMap<String, String>();

		DecimalFormat decformat=new DecimalFormat("0.000000");

		String msgID=null;

		try {
			iLogger.info(timeKey+" BHL is executing day agg... with time_key "+timeKey);

			long queryStartTime = System.currentTimeMillis();


			String SerialNumber = null;
			Date TxnDate = null;
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			queryByCreatedTSstatement = prodConnection.createStatement();



			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

			Date txnday=sdf1.parse(timeKey);
			Calendar cal  = Calendar.getInstance();
			cal.setTime(txnday);
			cal.add(Calendar.DATE, +1);
			Timestamp etlstartDateNext = new Timestamp(cal.getTime().getTime());

			String updatequery="select Serial_Number,Time_Key " +
					"from remote_monitoring_fact_data_dayagg_json_new where Date(Created_Timestamp) = '"+etlstartDateNext+"' and time_key < '"+timeKey+"' and Engine_Off_Hours >=0 order by Time_Key";


			if(VIN!=null && (VIN.trim().length()==7 || VIN.trim().length()==17)){
				if(VIN.trim().length()==7){
					updatequery="select Serial_Number,Time_Key " +
							"from remote_monitoring_fact_data_dayagg_json_new where Serial_Number like '%"+VIN+"' and Date(Created_Timestamp) = '"+etlstartDateNext+"' and time_key < '"+timeKey+"' and Engine_Off_Hours >=0 order by Time_Key";


				}
				else if(VIN.trim().length()==17){

					updatequery="select Serial_Number,Time_Key " +
							"from remote_monitoring_fact_data_dayagg_json_new where Serial_Number = '"+VIN+"' and Date(Created_Timestamp) = '"+etlstartDateNext+"' and time_key < '"+timeKey+"' and Engine_Off_Hours >=0 order by Time_Key";


				}
			}
			queryByCreatedTSRS = queryByCreatedTSstatement.executeQuery(updatequery);


			/*String insertStatement = "  insert into factInsight_dayAgg_extended(AssetID,TxnDate"+
					",DSC_CON_HRS,ENG_IDL_HRS,EXA_ECO_MOD_HRS,EXA_ACT_MOD_HRS,EXA_PWR_MOD_HRS,FIX_FLOW_HRS,FWD_DIR_HRS,LOAD_EC_MOD_HRS,LOAD_JOB_HRS,LOAD_PWR_MOD_HRS,Min_Hyd_HRS,NEU_DIR_HRS,REV_DIR_HRS"+
					",Hyd_Choke_Events,Load_Mode_First_gear,Load_Mode_ForwardDirection,Load_Mode_NeutralDirection,Load_Mode_NeutralGear,Load_Mode_ReverseDirection,Load_Mode_Second_gear,No_AutoIdle_Events,No_AutoOFF_Events,No_Kick_Load_Mode,PRB1,PRB2,PRB3,PRB4,PRB5,PRB6,PRBEM1,PRBEM2,PRBEM3,PRBEM4,PRBEM5,PRBEM6,PDRBEM1,PDRBEM2,PDRBEM3,PDRBEM4,PDRBEM5,PDRBEM6,PDRBLM1,PDRBLM2,PDRBLM3,PDRBLM4,PDRBLM5,PDRBLM6,Road_Mode_First_Gear,Road_Mode_Forard_Direction,Road_Mode_Fourth_Gear,Road_Mode_Neutral_Gear,Road_Mode_Reverse_Direction,Road_Mode_Second_Gear,Road_Mode_Third_Gear,Time_ActiveMode_Ex_Mode,Time_EcMode_Ex_Mode,Time_Hammer_Operation,Time_PwrMode_Ex_Mode"+
					",FuelLevelPerct,FuelRate,StartFuelInLtrs,FinishFuelInLtrs,FuelUsedInLtrs,FuelUsedInLPB,FuelUsedInMPB,FuelUsedInHPB,AvgFuelConsumption,FuelLoss,FuelUsedInWorking,StartLMode,FinishLMode,LModeInHrs,StartGMode,FinishGMode,GModeInHrs,StartHMode,FinishHMode,HModeInHrs,StartHPlusMode,FinishHPlusMode,HPlusModeInHrs,StartTravellingTime,FinishTravellingTime,TravellingTimeInHrs,StartSlewTime,FinishSlewTime,SlewTimeInHrs,StartHammerUseTime,FinishHammerUseTime,HammerUseTimeInHrs,HammerAbuseCount,CarbonEmission,PowerBoostTime,RegenerationTime,AverageVehicleSpeed,EngineManufacturer,Gear1FwdUtilization,Gear1BkwdUtilization,Gear1LockupUtilization,Gear2FwdUtilization,Gear2BkwdUtilization,Gear2LockupUtilization,Gear31FwdUtilization,Gear3BkwdUtilization,Gear3LockupUtilization,Gear4FwdUtilization,Gear4BkwdUtilization,Gear4LockupUtilization,Gear5FwdUtilization,Gear5BkwdUtilization,Gear5LockupUtilization,Gear6FwdUtilization,Gear6BkwdUtilization,Gear6LockupUtilization,NeutralGearUtilization,EngineOnCount,EngineOffCount"+
					",PartitionID,Column1,Column2,Column3,Column4,Column5,Column6,Column7,Column8,Column9,Column10,Column11,Column12,Column13,Column14,Column15,Column16,Column17,Column18,Column19,Column20,Column21" +
					",Column22,Column23,Column24,Column25,Column26,Column27,Column28,Column29,Column30,Column31,Column32,Column33,Column34,Column35,Column36,Column37,Column38,Column39)" +
					" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
					",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";*/
			String insertStatement = "  insert into factInsight_dayAgg_extended(AssetID,TxnDate"+
					",DSC_CON_HRS,ENG_IDL_HRS,EXA_ECO_MOD_HRS,EXA_ACT_MOD_HRS,EXA_PWR_MOD_HRS,FIX_FLOW_HRS,FWD_DIR_HRS,LOAD_EC_MOD_HRS,LOAD_JOB_HRS,LOAD_PWR_MOD_HRS,Min_Hyd_HRS,NEU_DIR_HRS,REV_DIR_HRS"+
					",Hyd_Choke_Events,Load_Mode_First_gear,Load_Mode_ForwardDirection,Load_Mode_NeutralDirection,Load_Mode_NeutralGear,Load_Mode_ReverseDirection,Load_Mode_Second_gear,No_AutoIdle_Events,No_AutoOFF_Events,No_Kick_Load_Mode,PRB1,PRB2,PRB3,PRB4,PRB5,PRB6,PRBEM1,PRBEM2,PRBEM3,PRBEM4,PRBEM5,PRBEM6,PDRBEM1,PDRBEM2,PDRBEM3,PDRBEM4,PDRBEM5,PDRBEM6,PDRBLM1,PDRBLM2,PDRBLM3,PDRBLM4,PDRBLM5,PDRBLM6,Road_Mode_First_Gear,Road_Mode_Forard_Direction,Road_Mode_Fourth_Gear,Road_Mode_Neutral_Gear,Road_Mode_Reverse_Direction,Road_Mode_Second_Gear,Road_Mode_Third_Gear,Time_ActiveMode_Ex_Mode,Time_EcMode_Ex_Mode,Time_Hammer_Operation,Time_PwrMode_Ex_Mode"+
					",FuelLevelPerct,FuelRate,StartFuelInLtrs,FinishFuelInLtrs,FuelUsedInLtrs,FuelUsedInLPB,FuelUsedInMPB,FuelUsedInHPB,AvgFuelConsumption,FuelLoss,FuelUsedInWorking,StartLMode,FinishLMode,LModeInHrs,StartGMode,FinishGMode,GModeInHrs,StartHMode,FinishHMode,HModeInHrs,StartHPlusMode,FinishHPlusMode,HPlusModeInHrs,StartTravellingTime,FinishTravellingTime,TravellingTimeInHrs,StartSlewTime,FinishSlewTime,SlewTimeInHrs,StartHammerUseTime,FinishHammerUseTime,HammerUseTimeInHrs,HammerAbuseCount,CarbonEmission,PowerBoostTime,RegenerationTime,AverageVehicleSpeed,EngineManufacturer,Gear1FwdUtilization,Gear1BkwdUtilization,Gear1LockupUtilization,Gear2FwdUtilization,Gear2BkwdUtilization,Gear2LockupUtilization,Gear31FwdUtilization,Gear3BkwdUtilization,Gear3LockupUtilization,Gear4FwdUtilization,Gear4BkwdUtilization,Gear4LockupUtilization,Gear5FwdUtilization,Gear5BkwdUtilization,Gear5LockupUtilization,Gear6FwdUtilization,Gear6BkwdUtilization,Gear6LockupUtilization,NeutralGearUtilization,EngineOnCount,EngineOffCount"+
					",PartitionID,Column1,Column2,Column3,Column4,Column5,Column6,Column7,Column8,Column9,Column10,Column11,Column12,Column13,Column14,Column15,Column16,Column17,Column18,Column19,Column20,Column21" +
					",Column22,Column23,Column24,Column25,Column26,Column27,Column28,Column29,Column30,Column31,Column32,Column33,Column34,Column35,Column36,Column37,Column38,Column39" +
					",Column40,Column41,Column42,Column43,Column44,Column45,Column46,Column47,Column48,Column49,Column50)" +
					" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
					",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			long endQueryTime = System.currentTimeMillis();


			String updateStatement = "update factInsight_dayAgg_extended set DSC_CON_HRS = ?,ENG_IDL_HRS = ?,EXA_ECO_MOD_HRS = ?," +
					"EXA_ACT_MOD_HRS = ?,EXA_PWR_MOD_HRS = ?,FIX_FLOW_HRS = ?,FWD_DIR_HRS = ?,LOAD_EC_MOD_HRS = ?,LOAD_JOB_HRS = ?,LOAD_PWR_MOD_HRS = ?,Min_Hyd_HRS=?,NEU_DIR_HRS=?," +
					"REV_DIR_HRS=?,Hyd_Choke_Events=?,Load_Mode_First_gear=?,Load_Mode_ForwardDirection=?,Load_Mode_NeutralDirection=?,Load_Mode_NeutralGear=?," + 
					"Load_Mode_ReverseDirection=?,Load_Mode_Second_gear=?,No_AutoIdle_Events=?,No_AutoOFF_Events=?,No_Kick_Load_Mode=?,PRB1=?,PRB2=?,PRB3=?,PRB4=?,PRB5=?,PRB6=?,PRBEM1=?,PRBEM2=?,PRBEM3=?,PRBEM4=?,PRBEM5=?,PRBEM6=?,PDRBEM1=?,PDRBEM2=?,PDRBEM3=?,PDRBEM4=?,PDRBEM5=?,PDRBEM6=?,PDRBLM1=?,PDRBLM2=?,PDRBLM3=?,PDRBLM4=?,PDRBLM5=?,PDRBLM6=?,Road_Mode_First_Gear=?,Road_Mode_Forard_Direction=?,Road_Mode_Fourth_Gear=?,Road_Mode_Neutral_Gear=?,Road_Mode_Reverse_Direction=?,Road_Mode_Second_Gear=?,Road_Mode_Third_Gear=?,Time_ActiveMode_Ex_Mode=?,Time_EcMode_Ex_Mode=?,Time_Hammer_Operation=?,Time_PwrMode_Ex_Mode=?,"+
					"FuelLevelPerct=?,FuelRate=?,StartFuelInLtrs=?,FinishFuelInLtrs=?,FuelUsedInLtrs=?,FuelUsedInLPB=?,FuelUsedInMPB=?,FuelUsedInHPB=?,AvgFuelConsumption=?,FuelLoss=?,FuelUsedInWorking=?,StartLMode=?,FinishLMode=?,LModeInHrs=?,StartGMode=?,FinishGMode=?,GModeInHrs=?,StartHMode=?,FinishHMode=?,HModeInHrs=?,StartHPlusMode=?,FinishHPlusMode=?,HPlusModeInHrs=?,StartTravellingTime=?,FinishTravellingTime=?,TravellingTimeInHrs=?,StartSlewTime=?,FinishSlewTime=?,SlewTimeInHrs=?,StartHammerUseTime=?,FinishHammerUseTime=?,HammerUseTimeInHrs=?,HammerAbuseCount=?,CarbonEmission=?,PowerBoostTime=?,RegenerationTime=?,AverageVehicleSpeed=?,EngineManufacturer=?,Gear1FwdUtilization=?,Gear1BkwdUtilization=?,Gear1LockupUtilization=?,Gear2FwdUtilization=?,Gear2BkwdUtilization=?,Gear2LockupUtilization=?,Gear31FwdUtilization=?,Gear3BkwdUtilization=?,Gear3LockupUtilization=?,Gear4FwdUtilization=?,Gear4BkwdUtilization=?,Gear4LockupUtilization=?,Gear5FwdUtilization=?,Gear5BkwdUtilization=?,Gear5LockupUtilization=?,Gear6FwdUtilization=?,Gear6BkwdUtilization=?,Gear6LockupUtilization=?,NeutralGearUtilization=?,EngineOnCount=?,EngineOffCount=?,"+
					"Column10=?,Column11=?,Column12=?,Column13=?,Column14=?,Column15=?,Column16=?,Column17=?,Column18=?,Column19=?"+
					",Column22=?,Column23=?,Column24=?,Column25=?,Column26=?,Column27=?,Column28=?,Column29=?,Column30=?,Column31=?,Column32=?,Column33=?,Column34=?,Column35=?,Column36=?,Column37=?,Column38=?,Column39=?" +
					",Column40=?,Column41=?,Column42=?,Column43=?,Column44=?,Column45=?,Column46=?,Column47=?,Column48=?,Column49=?,Column50=?" +
					" where AssetID = ?" +
					" and TxnDate = ? ";

			//String insertQuery = insertStatement + values;
			iLogger.info(timeKey+" BHL thread execution time for query"+((int)(((endQueryTime-queryStartTime)/1000)%60))+"");
			//iLogger.info(timeKey+" thread execution time for query"+((int)(((endQueryTime-queryStartTime)/1000)%60))+"");
			if(db2Connection==null || db2Connection.isClosed())
				db2Connection = new ConnectMySQL().getProdDb2Connection();
			pst = db2Connection.prepareStatement(insertStatement);
			pst1 = db2Connection.prepareStatement(updateStatement);
			updatest=db2Connection.createStatement();

			while(queryByCreatedTSRS.next()){
				SerialNumber = queryByCreatedTSRS.getString("Serial_Number");
				TxnDate = queryByCreatedTSRS.getDate("Time_Key");



				String jsonMigrationQuery="select firstDayAgg.*,"+
						"seconddayAgg.*"+
						" from("+
						" select dayagg.Serial_Number,"+
						"dayagg.Time_Key as timeKey,"+
						"Week(dayagg.Time_Key)+1 as week_no,"+
						"MONTH(dayagg.Time_Key) as month,"+
						"QUARTER(dayagg.Time_Key) as quarter,"+
						"YEAR(dayagg.Time_Key) as year,"+
						"dayagg.agg_param_data,"+
						"dayagg.Machine_Hours as EndEHours,"+
						"a_t.Asset_Type_Code as asset_type,agp.asset_grp_code as group_code" +
						//",ac.Account_Code as customerCode,pa.Account_Code as parentCode,za.Account_Code as ZonalCode,td.Tenancy_Type_Name as ac_type" +
						" from remote_monitoring_fact_data_dayagg_json_new dayagg,"+
						"asset a,products p,"+
						"asset_type a_t,asset_group ag,asset_group_profile agp "+
						//"tenancy_dimension td,account_tenancy act,"+
						//"account ac,account pa left outer join account za on za.account_id = pa.Parent_ID "+ 
						" where dayagg.Serial_Number = '"+SerialNumber+"' and time_key = '"+TxnDate+"' " +
						"and " + 
						"dayagg.Serial_Number = a.Serial_Number and "+
						"p.Product_ID = a.Product_ID and "+ 
						"ag.Asset_Group_ID = p.Asset_Group_ID and "+ 
						"agp.asset_grp_id = p.Asset_Group_ID and "+ 
						"a_t.Asset_Type_ID = p.Asset_Type_ID " +
						//"and "+
						//"td.tenancy_dimension_id = dayagg.tenancy_id and "+ 
						//"act.tenancy_id = td.tenancy_id and "+ 
						//"ac.account_id = act.account_id and "+ 
						//"pa.account_id = ac.Parent_ID "+  
						") firstDayAgg left outer join"+
						" (select prevdayAgg.serial_number as lastSerial_Number, prevdayAgg.agg_param_data  as lastagg_param_data, prevdayAgg.time_key"+
						" from remote_monitoring_fact_data_dayagg_json_new prevdayAgg"+ 
						" left outer join "+
						"(select max(sn.Time_key) tk,sn.serial_number ser"+
						"	from remote_monitoring_fact_data_dayagg_json_new sn"+
						" where sn.time_key < '"+TxnDate+"' and " +
						"sn.Serial_Number = '" +SerialNumber+"' "+
						" group by sn.serial_number"+
						") nextdayAgg"+
						" on prevdayAgg.serial_number=nextdayAgg.ser"+ 
						" where prevdayAgg.Time_Key=nextdayAgg.tk"+
						") seconddayAgg"+
						" on firstDayAgg.serial_number=seconddayAgg.lastSerial_Number ";



				//	iLogger.info("jsonMigrationQuery BHL::"+jsonMigrationQuery);

				if(statement==null || statement.isClosed())
					statement = prodConnection.createStatement();

				rs = statement.executeQuery(jsonMigrationQuery);

				iLogger.info(timeKey+" BHL before executing while loop for query");
				//iLogger.info(timeKey+"before executing while loop for query");
				long iteratorStart = System.currentTimeMillis();




				if(rs.next()){


					try{
						Serial_Number1 = rs.getString("Serial_Number");
						txnDate = rs.getDate("timeKey");

						dateUtililty2 = dateUtililty.getCurrentDateUtility(txnDate);

						week = dateUtililty2.week;

						month = rs.getInt("month");

						quarter = rs.getInt("quarter");

						year = rs.getInt("year");

						asset_type = rs.getString("asset_type");

						group_id = rs.getString("group_code");

						/*customer_code = rs.getString("customerCode");

						dealer_code = rs.getString("parentCode");

						ac_type = rs.getString("ac_type");

						if(ac_type.equalsIgnoreCase("Customer")){
							zonal_code = rs.getString("ZonalCode");
						}*/


						if(rs.getObject("agg_param_data")!=null)
							txnData=rs.getObject("agg_param_data").toString();


						if(txnData!=null)
							txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, String>>() {}.getType());

						if(txnDataMap.size()>0){
							customer_code =txnDataMap.get("customerCode");
							dealer_code=txnDataMap.get("customerCode");
							zonal_code=txnDataMap.get("ZonalCode");
							regionCode=txnDataMap.get("regionCode");
							msgID=txnDataMap.get("MSG_ID");
						}

						if(rs.getObject("lastagg_param_data")!=null)
							prevDayTxnData=rs.getObject("lastagg_param_data").toString();

						if(prevDayTxnData!=null)
							preTtxnDataMap = new Gson().fromJson(prevDayTxnData, new TypeToken<HashMap<String, String>>() {}.getType());





						try{
							if(txnDataMap.get("TFU")!=null && rs.getString("EndEHours")!=null){
								CMH=Double.parseDouble(rs.getString("EndEHours"));



								AvgFuelConsumption=Double.parseDouble(txnDataMap.get("TFU"))/CMH;
							}

							/*if(txnDataMap.get("TFU")!=null && preTtxnDataMap.get("TFU")!=null){
						peridTFU=Double.parseDouble(txnDataMap.get("TFU"))-Double.parseDouble(preTtxnDataMap.get("TFU"));
							}
						if(txnDataMap.get("FuelUsedInWorking")!=null){
						AvgFuelConsumption=peridTFU/(Double.parseDouble(txnDataMap.get("FuelUsedInWorking")));
						}*/

						}
						catch(Exception e){
							fLogger.fatal("BHL-Error in calculating AvgFuelConsumption"+e.getMessage());

						}


						if(db2Connection==null || db2Connection.isClosed())
							db2Connection = new ConnectMySQL().getProdDb2Connection();

						if(updatest==null || updatest.isClosed())
							updatest = db2Connection.createStatement();

						//iLogger.info("select * from factInsight_dayAgg where where AssetID = '"+Serial_Number+"' and TxnDate = '"+txnDate+"'");
						updateRS = updatest.executeQuery("select * from factInsight_dayAgg_extended where AssetID = '"+Serial_Number1+"' and TxnDate = '"+txnDate+"'");

						if(updateRS.next()){
							iLogger.info("Migration BHL for update for Serial_Number "+Serial_Number1+"' and TxnDate = '"+txnDate);

							if(! (txnDataMap.size()>0))
								continue;

							pst1.setString(1, txnDataMap.get("DSC_CON_HRS"));
							pst1.setString(2, txnDataMap.get("ENG_IDL_HRS"));
							pst1.setString(3, txnDataMap.get("EXA_ECO_MOD_HRS"));
							pst1.setString(4, txnDataMap.get("EXA_ACT_MOD_HRS"));
							pst1.setString(5, txnDataMap.get("EXA_PWR_MOD_HRS"));
							pst1.setString(6, txnDataMap.get("FIX_FLOW_HRS"));
							pst1.setString(7, txnDataMap.get("FWD_DIR_HRS"));
							pst1.setString(8, txnDataMap.get("LOAD_EC_MOD_HRS"));
							pst1.setString(9, txnDataMap.get("LOAD_JOB_HRS"));
							pst1.setString(10, txnDataMap.get("LOAD_PWR_MOD_HRS"));
							pst1.setString(11, txnDataMap.get("Min_Hyd_HRS"));
							pst1.setString(12, txnDataMap.get("NEU_DIR_HRS"));
							pst1.setString(13, txnDataMap.get("REV_DIR_HRS"));
							pst1.setString(14, txnDataMap.get("Hyd_Choke_Events"));
							pst1.setString(15, txnDataMap.get("Load_Mode_First_gear"));
							pst1.setString(16, txnDataMap.get("Load_Mode_ForwardDirection"));
							pst1.setString(17, txnDataMap.get("Load_Mode_NeutralDirection"));
							pst1.setString(18, txnDataMap.get("Load_Mode_NeutralGear"));
							pst1.setString(19, txnDataMap.get("Load_Mode_ReverseDirection"));
							pst1.setString(20, txnDataMap.get("Load_Mode_Second_gear"));
							pst1.setString(21, txnDataMap.get("No_AutoIdle_Events"));
							pst1.setString(22, txnDataMap.get("No_AutoOFF_Events"));
							pst1.setString(23, txnDataMap.get("No_Kick_Load_Mode"));
							pst1.setString(24, txnDataMap.get("PRB1"));
							pst1.setString(25, txnDataMap.get("PRB2"));
							pst1.setString(26, txnDataMap.get("PRB3"));
							pst1.setString(27, txnDataMap.get("PRB4"));
							pst1.setString(28, txnDataMap.get("PRB5"));
							pst1.setString(29, txnDataMap.get("PRB6"));
							pst1.setString(30, txnDataMap.get("PRBEM1"));
							pst1.setString(31, txnDataMap.get("PRBEM2"));
							pst1.setString(32, txnDataMap.get("PRBEM3"));
							pst1.setString(33, txnDataMap.get("PRBEM4"));
							pst1.setString(34, txnDataMap.get("PRBEM5"));
							pst1.setString(35, txnDataMap.get("PRBEM6"));
							pst1.setString(36, txnDataMap.get("PDRBEM1"));
							pst1.setString(37, txnDataMap.get("PDRBEM2"));
							pst1.setString(38, txnDataMap.get("PDRBEM3"));
							pst1.setString(39, txnDataMap.get("PDRBEM4"));
							pst1.setString(40, txnDataMap.get("PDRBEM5"));
							pst1.setString(41, txnDataMap.get("PDRBEM6"));
							pst1.setString(42, txnDataMap.get("PDRBLM1"));
							pst1.setString(43, txnDataMap.get("PDRBLM2"));
							pst1.setString(44, txnDataMap.get("PDRBLM3"));
							pst1.setString(45, txnDataMap.get("PDRBLM4"));
							pst1.setString(46, txnDataMap.get("PDRBLM5"));
							pst1.setString(47, txnDataMap.get("PDRBLM6"));
							pst1.setString(48, txnDataMap.get("Road_Mode_First_Gear"));
							pst1.setString(49, txnDataMap.get("Road_Mode_Forard_Direction"));
							pst1.setString(50, txnDataMap.get("Road_Mode_Fourth_Gear"));
							pst1.setString(51, txnDataMap.get("Road_Mode_Neutral_Gear"));
							pst1.setString(52, txnDataMap.get("Road_Mode_Reverse_Direction"));
							pst1.setString(53, txnDataMap.get("Road_Mode_Second_Gear"));
							pst1.setString(54, txnDataMap.get("Road_Mode_Third_Gear"));
							pst1.setString(55, txnDataMap.get("Time_ActiveMode_Ex_Mode"));
							pst1.setString(56, txnDataMap.get("Time_EcMode_Ex_Mode"));
							pst1.setString(57, txnDataMap.get("Time_Hammer_Operation"));
							pst1.setString(58, txnDataMap.get("Time_PwrMode_Ex_Mode"));

							pst1.setString(59, txnDataMap.get("FUEL_PERCT"));
							pst1.setString(60, txnDataMap.get("Fuel_rate"));
							pst1.setString(61, preTtxnDataMap.get("TFU"));
							pst1.setString(62, txnDataMap.get("TFU"));
							pst1.setString(63, txnDataMap.get("TFU"));
							pst1.setString(64, txnDataMap.get("FuelUsedInLPB"));
							pst1.setString(65, txnDataMap.get("FuelUsedInMPB"));
							pst1.setString(66, txnDataMap.get("FuelUsedInHPB"));


							pst1.setString(67, String.valueOf(AvgFuelConsumption));

							//pst1.setString(67, txnDataMap.get("AvgFuelConsumption"));
							pst1.setString(68, txnDataMap.get("FuelLoss"));
							pst1.setString(69, txnDataMap.get("FuelUsedInWorking"));
							pst1.setString(70, preTtxnDataMap.get("L_band"));
							pst1.setString(71, txnDataMap.get("L_band"));
							pst1.setString(72, txnDataMap.get("L_band"));
							pst1.setString(73, preTtxnDataMap.get("G_band"));
							pst1.setString(74, txnDataMap.get("G_band"));
							pst1.setString(75, txnDataMap.get("G_band"));
							pst1.setString(76, preTtxnDataMap.get("H_band"));
							pst1.setString(77, txnDataMap.get("H_band"));
							pst1.setString(78, txnDataMap.get("H_band"));
							pst1.setString(79, preTtxnDataMap.get("HPLUS_band"));
							pst1.setString(80, txnDataMap.get("HPLUS_band"));
							pst1.setString(81, txnDataMap.get("HPLUS_band"));
							pst1.setString(82, preTtxnDataMap.get("TH"));
							pst1.setString(83, txnDataMap.get("TH"));
							pst1.setString(84, txnDataMap.get("TH"));
							pst1.setString(85, preTtxnDataMap.get("SH"));
							pst1.setString(86, txnDataMap.get("SH"));
							pst1.setString(87, txnDataMap.get("SH"));
							pst1.setString(88, preTtxnDataMap.get("Total_Hammer_Hours"));
							pst1.setString(89, txnDataMap.get("Total_Hammer_Hours"));
							pst1.setString(90, txnDataMap.get("Total_Hammer_Hours"));
							pst1.setString(91, txnDataMap.get("HAC"));
							pst1.setString(92, txnDataMap.get("Carbon_emission"));
							pst1.setString(93, String.valueOf(decformat.format(Double.parseDouble(txnDataMap.get("Power_boost_hours")))));
							pst1.setString(94, txnDataMap.get("RTT4E"));
							pst1.setString(95, txnDataMap.get("WBVS"));
							pst1.setString(96, txnDataMap.get("EMT"));
							pst1.setString(97, txnDataMap.get("GFDU"));
							pst1.setString(98, txnDataMap.get("GRDU"));
							pst1.setString(99, txnDataMap.get("GLU"));
							pst1.setString(100, txnDataMap.get("G2FDU"));
							pst1.setString(101, txnDataMap.get("G2RDU"));
							pst1.setString(102, txnDataMap.get("G2LU"));
							pst1.setString(103, txnDataMap.get("G3FDU"));
							pst1.setString(104, txnDataMap.get("G3RDU"));
							pst1.setString(105, txnDataMap.get("G3LU"));
							pst1.setString(106, txnDataMap.get("G4FDU"));
							pst1.setString(107, txnDataMap.get("G4RDU"));
							pst1.setString(108, txnDataMap.get("G4LU"));
							pst1.setString(109, txnDataMap.get("G5FDU"));
							pst1.setString(110, txnDataMap.get("G5RDU"));
							pst1.setString(111, txnDataMap.get("G5LU"));
							pst1.setString(112, txnDataMap.get("G6FDU"));
							pst1.setString(113, txnDataMap.get("G6RDU"));
							pst1.setString(114, txnDataMap.get("G6LU"));
							pst1.setString(115, txnDataMap.get("Neutral_gear"));
							pst1.setString(116, txnDataMap.get("EngineOnCount"));
							pst1.setString(117, txnDataMap.get("EngineOffCount"));

							pst1.setString(118,txnDataMap.get("AHIAOHO"));
							pst1.setString(119,txnDataMap.get("GGID_1"));
							pst1.setString(120,txnDataMap.get("GGID_2"));
							pst1.setString(121,txnDataMap.get("GGID_3"));
							pst1.setString(122,txnDataMap.get("GGID_4"));
							pst1.setString(123,txnDataMap.get("GGID_5"));
							pst1.setString(124,txnDataMap.get("AHIEJ"));
							pst1.setString(125,txnDataMap.get("AHIRJ"));

							//Df20170821 @Roopa Newparam changes Long Engine Idling Count and Hot Engine Shut Down Count

							pst1.setString(126,txnDataMap.get("LEIC"));
							pst1.setString(127,txnDataMap.get("HESDC"));

							pst1.setString(128, preTtxnDataMap.get("ENG_IDL_HRS"));
							pst1.setString(129, preTtxnDataMap.get("AHIAOHO"));
							pst1.setString(130, preTtxnDataMap.get("AHIRJ"));
							pst1.setString(131, preTtxnDataMap.get("LOAD_JOB_HRS"));
							pst1.setString(132, preTtxnDataMap.get("AHIEJ"));
							pst1.setString(133, preTtxnDataMap.get("EXA_ECO_MOD_HRS"));
							pst1.setString(134, preTtxnDataMap.get("EXA_ACT_MOD_HRS"));
							pst1.setString(135, preTtxnDataMap.get("EXA_PWR_MOD_HRS"));
							pst1.setString(136, preTtxnDataMap.get("FWD_DIR_HRS"));
							pst1.setString(137, preTtxnDataMap.get("NEU_DIR_HRS"));
							pst1.setString(138, preTtxnDataMap.get("REV_DIR_HRS"));
							pst1.setString(139, preTtxnDataMap.get("DSC_CON_HRS"));
							pst1.setString(140, preTtxnDataMap.get("Hyd_Choke_Events"));
							pst1.setString(141, preTtxnDataMap.get("No_AutoIdle_Events"));
							pst1.setString(142, preTtxnDataMap.get("No_AutoOFF_Events"));
							pst1.setString(143, preTtxnDataMap.get("HESDC"));
							pst1.setString(144, preTtxnDataMap.get("LEIC"));
							pst1.setString(145, preTtxnDataMap.get("HAC"));
							
//DF20180111 @Roopa CMS and WLS parameter's integration
							
							pst1.setString(146,preTtxnDataMap.get("WMS_LBW"));
							pst1.setString(147,txnDataMap.get("WMS_LBW"));
							
							pst1.setString(148,preTtxnDataMap.get("WMS_CLW"));
							pst1.setString(149,txnDataMap.get("WMS_CLW"));
							
							pst1.setString(150,preTtxnDataMap.get("WMS_NOB"));
							pst1.setString(151,txnDataMap.get("WMS_NOB"));
							
							pst1.setString(152,txnDataMap.get("WMS_MNC"));
							
							pst1.setString(153,preTtxnDataMap.get("CMS_ACV"));
							pst1.setString(154,txnDataMap.get("CMS_ACV"));
							
							
							pst1.setString(155,preTtxnDataMap.get("CMS_AMS_SA")); //prev day value
							pst1.setString(156,txnDataMap.get("CMS_AMS_SA")); //server side accumulated value
							//END

							pst1.setString(157, Serial_Number1);
							pst1.setDate(158, new java.sql.Date(txnDate.getTime()));
							
							
							pst1.addBatch();
							//continue;

							if(updateRS!=null){
								try{
									updateRS.close();	
								}
								catch(Exception e){
									fLogger.fatal("Error occured in migration:"+e.getMessage());
								}
							}
						}

						else{

							if(pst.isClosed()){
								if(db2Connection==null || db2Connection.isClosed()){
									db2Connection = db2Connection = new ConnectMySQL().getProdDb2Connection();
								}
								iLogger.info("pst BHL has closed so reopening the statement BHL------");
								pst = db2Connection.prepareStatement(insertStatement);
							}
							if(! (txnDataMap.size()>0))
								continue;

							pst.setString(1, Serial_Number1);
							pst.setDate(2, new java.sql.Date(txnDate.getTime()));
							pst.setString(3, txnDataMap.get("DSC_CON_HRS"));
							pst.setString(4, txnDataMap.get("ENG_IDL_HRS"));
							pst.setString(5, txnDataMap.get("EXA_ECO_MOD_HRS"));
							pst.setString(6, txnDataMap.get("EXA_ACT_MOD_HRS"));
							pst.setString(7, txnDataMap.get("EXA_PWR_MOD_HRS"));
							pst.setString(8, txnDataMap.get("FIX_FLOW_HRS"));
							pst.setString(9, txnDataMap.get("FWD_DIR_HRS"));
							pst.setString(10, txnDataMap.get("LOAD_EC_MOD_HRS"));
							pst.setString(11, txnDataMap.get("LOAD_JOB_HRS"));
							pst.setString(12, txnDataMap.get("LOAD_PWR_MOD_HRS"));
							pst.setString(13, txnDataMap.get("Min_Hyd_HRS"));
							pst.setString(14, txnDataMap.get("NEU_DIR_HRS"));
							pst.setString(15, txnDataMap.get("REV_DIR_HRS"));
							pst.setString(16, txnDataMap.get("Hyd_Choke_Events"));
							pst.setString(17, txnDataMap.get("Load_Mode_First_gear"));
							pst.setString(18, txnDataMap.get("Load_Mode_ForwardDirection"));
							pst.setString(19, txnDataMap.get("Load_Mode_NeutralDirection"));
							pst.setString(20, txnDataMap.get("Load_Mode_NeutralGear"));
							pst.setString(21, txnDataMap.get("Load_Mode_ReverseDirection"));
							pst.setString(22, txnDataMap.get("Load_Mode_Second_gear"));
							pst.setString(23, txnDataMap.get("No_AutoIdle_Events"));
							pst.setString(24, txnDataMap.get("No_AutoOFF_Events"));
							pst.setString(25, txnDataMap.get("No_Kick_Load_Mode"));
							pst.setString(26, txnDataMap.get("PRB1"));
							pst.setString(27, txnDataMap.get("PRB2"));
							pst.setString(28, txnDataMap.get("PRB3"));
							pst.setString(29, txnDataMap.get("PRB4"));
							pst.setString(30, txnDataMap.get("PRB5"));
							pst.setString(31, txnDataMap.get("PRB6"));
							pst.setString(32, txnDataMap.get("PRBEM1"));
							pst.setString(33, txnDataMap.get("PRBEM2"));
							pst.setString(34, txnDataMap.get("PRBEM3"));
							pst.setString(35, txnDataMap.get("PRBEM4"));
							pst.setString(36, txnDataMap.get("PRBEM5"));
							pst.setString(37, txnDataMap.get("PRBEM6"));
							pst.setString(38, txnDataMap.get("PDRBEM1"));
							pst.setString(39, txnDataMap.get("PDRBEM2"));
							pst.setString(40, txnDataMap.get("PDRBEM3"));
							pst.setString(41, txnDataMap.get("PDRBEM4"));
							pst.setString(42, txnDataMap.get("PDRBEM5"));
							pst.setString(43, txnDataMap.get("PDRBEM6"));
							pst.setString(44, txnDataMap.get("PDRBLM1"));
							pst.setString(45, txnDataMap.get("PDRBLM2"));
							pst.setString(46, txnDataMap.get("PDRBLM3"));
							pst.setString(47, txnDataMap.get("PDRBLM4"));
							pst.setString(48, txnDataMap.get("PDRBLM5"));
							pst.setString(49, txnDataMap.get("PDRBLM6"));
							pst.setString(50, txnDataMap.get("Road_Mode_First_Gear"));
							pst.setString(51, txnDataMap.get("Road_Mode_Forard_Direction"));
							pst.setString(52, txnDataMap.get("Road_Mode_Fourth_Gear"));
							pst.setString(53, txnDataMap.get("Road_Mode_Neutral_Gear"));
							pst.setString(54, txnDataMap.get("Road_Mode_Reverse_Direction"));
							pst.setString(55, txnDataMap.get("Road_Mode_Second_Gear"));
							pst.setString(56, txnDataMap.get("Road_Mode_Third_Gear"));
							pst.setString(57, txnDataMap.get("Time_ActiveMode_Ex_Mode"));
							pst.setString(58, txnDataMap.get("Time_EcMode_Ex_Mode"));
							pst.setString(59, txnDataMap.get("Time_Hammer_Operation"));
							pst.setString(60, txnDataMap.get("Time_PwrMode_Ex_Mode"));

							pst.setString(61, txnDataMap.get("FUEL_PERCT"));
							pst.setString(62, txnDataMap.get("Fuel_rate"));
							pst.setString(63, preTtxnDataMap.get("TFU"));
							pst.setString(64, txnDataMap.get("TFU"));
							pst.setString(65, txnDataMap.get("TFU"));
							pst.setString(66, txnDataMap.get("FuelUsedInLPB"));
							pst.setString(67, txnDataMap.get("FuelUsedInMPB"));
							pst.setString(68, txnDataMap.get("FuelUsedInHPB"));
							pst.setString(69, String.valueOf(AvgFuelConsumption));
							//pst.setString(69, txnDataMap.get("AvgFuelConsumption"));
							pst.setString(70, txnDataMap.get("FuelLoss"));
							pst.setString(71, txnDataMap.get("FuelUsedInWorking"));
							pst.setString(72, preTtxnDataMap.get("L_band"));
							pst.setString(73, txnDataMap.get("L_band"));
							pst.setString(74, txnDataMap.get("L_band"));
							pst.setString(75, preTtxnDataMap.get("G_band"));
							pst.setString(76, txnDataMap.get("G_band"));
							pst.setString(77, txnDataMap.get("G_band"));
							pst.setString(78, preTtxnDataMap.get("H_band"));
							pst.setString(79, txnDataMap.get("H_band"));
							pst.setString(80, txnDataMap.get("H_band"));
							pst.setString(81, preTtxnDataMap.get("HPLUS_band"));
							pst.setString(82, txnDataMap.get("HPLUS_band"));
							pst.setString(83, txnDataMap.get("HPLUS_band"));
							pst.setString(84, preTtxnDataMap.get("TH"));
							pst.setString(85, txnDataMap.get("TH"));
							pst.setString(86, txnDataMap.get("TH"));
							pst.setString(87, preTtxnDataMap.get("SH"));
							pst.setString(88, txnDataMap.get("SH"));
							pst.setString(89, txnDataMap.get("SH"));
							pst.setString(90, preTtxnDataMap.get("Total_Hammer_Hours"));
							pst.setString(91, txnDataMap.get("Total_Hammer_Hours"));
							pst.setString(92, txnDataMap.get("Total_Hammer_Hours"));
							pst.setString(93, txnDataMap.get("HAC"));
							pst.setString(94, txnDataMap.get("Carbon_emission"));
							pst.setString(95, txnDataMap.get("Power_boost_hours"));
							pst.setString(96, txnDataMap.get("RTT4E"));
							pst.setString(97, txnDataMap.get("WBVS"));
							pst.setString(98, txnDataMap.get("EMT"));
							pst.setString(99, txnDataMap.get("GFDU"));
							pst.setString(100, txnDataMap.get("GRDU"));
							pst.setString(101, txnDataMap.get("GLU"));
							pst.setString(102, txnDataMap.get("G2FDU"));
							pst.setString(103, txnDataMap.get("G2RDU"));
							pst.setString(104, txnDataMap.get("G2LU"));
							pst.setString(105, txnDataMap.get("G3FDU"));
							pst.setString(106, txnDataMap.get("G3RDU"));
							pst.setString(107, txnDataMap.get("G3LU"));
							pst.setString(108, txnDataMap.get("G4FDU"));
							pst.setString(109, txnDataMap.get("G4RDU"));
							pst.setString(110, txnDataMap.get("G4LU"));
							pst.setString(111, txnDataMap.get("G5FDU"));
							pst.setString(112, txnDataMap.get("G5RDU"));
							pst.setString(113, txnDataMap.get("G5LU"));
							pst.setString(114, txnDataMap.get("G6FDU"));
							pst.setString(115, txnDataMap.get("G6RDU"));
							pst.setString(116, txnDataMap.get("G6LU"));
							pst.setString(117, txnDataMap.get("Neutral_gear"));
							pst.setString(118, txnDataMap.get("EngineOnCount"));
							pst.setString(119, txnDataMap.get("EngineOffCount"));

							pst.setInt(120, partition_ID);

							pst.setInt(121, week);
							pst.setInt(122, month);
							pst.setInt(123, quarter);
							pst.setInt(124, year);
							pst.setString(125,asset_type);
							pst.setString(126,group_id);
							pst.setString(127,zonal_code);
							pst.setString(128,dealer_code);
							pst.setString(129,customer_code);

							pst.setString(130,txnDataMap.get("AHIAOHO"));
							pst.setString(131,txnDataMap.get("GGID_1"));
							pst.setString(132,txnDataMap.get("GGID_2"));
							pst.setString(133,txnDataMap.get("GGID_3"));
							pst.setString(134,txnDataMap.get("GGID_4"));
							pst.setString(135,txnDataMap.get("GGID_5"));
							pst.setString(136,txnDataMap.get("AHIEJ"));
							pst.setString(137,txnDataMap.get("AHIRJ"));

							//Df20170821 @Roopa Newparam changes Long Engine Idling Count and Hot Engine Shut Down Count

							pst.setString(138,txnDataMap.get("LEIC"));
							pst.setString(139,txnDataMap.get("HESDC"));

							pst.setString(140,txnDataMap.get("regionCode")); //Df20170828 @Roopa Adding region code for region filter changes

							//Df20171113 @Roopa including msgID for BHL differentiation for reports
							pst.setString(141, msgID);

							pst.setString(142, preTtxnDataMap.get("ENG_IDL_HRS"));
							pst.setString(143, preTtxnDataMap.get("AHIAOHO"));
							pst.setString(144, preTtxnDataMap.get("AHIRJ"));
							pst.setString(145, preTtxnDataMap.get("LOAD_JOB_HRS"));
							pst.setString(146, preTtxnDataMap.get("AHIEJ"));
							pst.setString(147, preTtxnDataMap.get("EXA_ECO_MOD_HRS"));
							pst.setString(148, preTtxnDataMap.get("EXA_ACT_MOD_HRS"));
							pst.setString(149, preTtxnDataMap.get("EXA_PWR_MOD_HRS"));
							pst.setString(150, preTtxnDataMap.get("FWD_DIR_HRS"));
							pst.setString(151, preTtxnDataMap.get("NEU_DIR_HRS"));
							pst.setString(152, preTtxnDataMap.get("REV_DIR_HRS"));
							pst.setString(153, preTtxnDataMap.get("DSC_CON_HRS"));
							pst.setString(154, preTtxnDataMap.get("Hyd_Choke_Events"));
							pst.setString(155, preTtxnDataMap.get("No_AutoIdle_Events"));
							pst.setString(156, preTtxnDataMap.get("No_AutoOFF_Events"));
							pst.setString(157, preTtxnDataMap.get("HESDC"));
							pst.setString(158, preTtxnDataMap.get("LEIC"));
							pst.setString(159, preTtxnDataMap.get("HAC"));
							
							//DF20180111 @Roopa CMS and WLS parameter's integration
							
							pst.setString(160,preTtxnDataMap.get("WMS_LBW"));
							pst.setString(161,txnDataMap.get("WMS_LBW"));
							
							pst.setString(162,preTtxnDataMap.get("WMS_CLW"));
							pst.setString(163,txnDataMap.get("WMS_CLW"));
							
							pst.setString(164,preTtxnDataMap.get("WMS_NOB"));
							pst.setString(165,txnDataMap.get("WMS_NOB"));
							
							pst.setString(166,txnDataMap.get("WMS_MNC"));
							
							pst.setString(167,preTtxnDataMap.get("CMS_ACV"));
							pst.setString(168,txnDataMap.get("CMS_ACV"));
							
							pst.setString(169,preTtxnDataMap.get("CMS_AMS_SA")); //prev day value
							pst.setString(170,txnDataMap.get("CMS_AMS_SA")); //server side accumulated value
							//END

							//faultStatements.add(Serial_Number1);

							pst.addBatch();

						}
					}catch(SQLException innerSQL){
						fLogger.fatal("Error occured in migration:"+innerSQL.getMessage());
						innerSQL.printStackTrace();
					}
					if(bhlrow%1000 == 0)
					{
						//iLogger.info(timeKey+"roww:"+row);
						long iteratorEnd = System.currentTimeMillis();
						iLogger.info(timeKey+" BHL 1000 iterations executed in: "+(iteratorEnd-iteratorStart));
						iteratorStart = System.currentTimeMillis();
						try{
							iLogger.info(timeKey+" BHL before executingbatch updations for 1000 records");
							long preparedStmtStart = System.currentTimeMillis();
							pst.executeBatch();
							pst1.executeBatch();

							pst.clearBatch();
							pst1.clearBatch();
							long preparedStmtEnd = System.currentTimeMillis();
							iLogger.info(timeKey+" BHL executing batch updations for 1000 records in "+(preparedStmtEnd-preparedStmtStart));


						}catch(BatchUpdateException bue){
							iLogger.info(" BHL inner BatchUpdateexception:"+bue.getMessage());
							fLogger.fatal("Error occured in migration:"+bue.getMessage());
							bue.printStackTrace();
							int[] updatedCount = bue.getUpdateCounts();




						}
						catch(Exception e){
							e.printStackTrace();
							fLogger.fatal("Error occured in migration:"+e.getMessage());

						}

						if(db2Connection == null || db2Connection.isClosed())
							db2Connection = new ConnectMySQL().getProdDb2Connection();
						if(pst==null || pst.isClosed())
							pst = db2Connection.prepareStatement(insertStatement);
						if(pst1==null || pst1.isClosed())
							pst1 = db2Connection.prepareStatement(updateStatement);
					}

					bhlrow++;

					//iLogger.info(timeKey+"outside row count for:"+row);

					if(rs!=null){
						try{
							rs.close();	
						}
						catch(Exception e){
							fLogger.fatal("Error occured in migration:"+e.getMessage());
						}
					}
				}
			}
			//iLogger.info("row:"+row);
			iLogger.info(timeKey+" BHL after executing while loop for query");

			if(db2Connection == null || db2Connection.isClosed())
				db2Connection = new ConnectMySQL().getProdDb2Connection();
			if(pst==null || pst.isClosed())
				pst = db2Connection.prepareStatement(insertStatement);
			if(pst1==null || pst1.isClosed())
				pst1 = db2Connection.prepareStatement(updateStatement);

			int[] updateVins = pst.executeBatch();
			pst1.executeBatch();
			/*if(db2Connection.isClosed())
						db2Connection = new ConnectMySQL().getProdDb2Connection();
					db2Connection.commit();*/
			pst.clearBatch();
			pst1.clearBatch();
			iLogger.info(timeKey+" BHL after executing batch insertions");
			result += ": "+bhlrow;
			//iLogger.info(result);
			//Thread.sleep(1000);

		}
		catch(BatchUpdateException bue){
			iLogger.info(" BHL outer BatchUpdateexception:"+bue.getMessage());
			fLogger.fatal("Error occured in migration:"+bue.getMessage());
			bue.printStackTrace();
			int[] updatedCount = bue.getUpdateCounts();


		}
		catch (SQLException e) {

			e.printStackTrace();
			iLogger.info(" BHL SQL Exception ------:"+e.getMessage());
			fLogger.fatal("Error occured in migration:"+e.getMessage());
		} 
		catch(Exception e)
		{
			iLogger.info("BHL Normal Exception ------:"+e.getMessage());
			fLogger.fatal("Error occured in migration:"+e.getMessage());

			e.printStackTrace();
		}
		finally{
			try { if (rs != null) rs.close(); } catch (Exception e) {};


			try { if (queryByCreatedTSRS != null) queryByCreatedTSRS.close(); } catch (Exception e) {};

			if(updateRS!=null){
				try{
					updateRS.close();	
				}
				catch(Exception e){

				}
			}

			if(updatest!=null){
				try{
					updatest.close();	
				}
				catch(Exception e){

				}
			}

			if(queryByCreatedTSstatement!=null){
				try{
					queryByCreatedTSstatement.close();	
				}
				catch(Exception e){

				}
			}

			if(statement!=null){
				try{
					statement.close();	
				}
				catch(Exception e){

				}
			}

			if(db2Statement!=null){
				try{
					db2Statement.close();	
				}
				catch(Exception e){

				}
			}

			if(localStatement!=null){
				try {
					localStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pst1!=null){
				try {
					pst1.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(db2Statement!=null){
				try {
					db2Statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(db2Connection!=null)
			{

				try {
					//db2Connection.commit();
					db2Connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


		}
		long threadEnd1 = System.currentTimeMillis();
		int threadExecutionTime1 = (int) (((threadEnd1-threadStart1) / (1000*60)) % 60);
		iLogger.info("BHL Thread"+timeKey+" is finished... with "+result+" machines in "+threadExecutionTime1+" mins");
		//iLogger.info("Thread"+timeKey+" is finished... with "+result+" machines in "+threadExecutionTime+" mins");


		//end BHL update

		return result;
	}

	public String getDayAGGDataUpdate(String timeKey, String VIN){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String result = timeKey;
		Connection prodConnection = null;
		Statement statement = null;
		Statement queryByCreatedTSstatement = null;
		Statement localStatement = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		//iLogger.info(Thread.currentThread().getName()+" is executing day agg...");
		long threadStart = System.currentTimeMillis();
		String Serial_Number = null;
		int week = 0;
		int month = 0;
		int year = 0;
		int quarter = 0;
		double ERB1 = 0;
		double ERB2 = 0;
		double ERB3 = 0;
		double ERB4 = 0;
		double ERB5 = 0;
		double ERB6 = 0;
		double ERB7 = 0;
		String asset_type = null;
		String type_name = null;
		String group_id = null;
		String group_name = null;
		String customer_code = null;
		String customer_name = null;
		String dealer_code = null;
		String dealer_name = null;
		String zonal_code = null;
		String zonal_name=null;
		String primary_owner_code=null;
		String customer_number= null;
		String dealer_number = null;
		String regionCode=null;
		String regionName=null;
		double startEHours = 0;
		double endEHours = 0;
		double period_hours = 0;
		double latitude = 0;
		double longitude = 0;
		String address = null;
		String city = null;
		String state = null;
		double AVG_Uitlization = 0;
		double LPB = 0;
		double MPB = 0;
		double HPB = 0;
		double idle_band = 0;
		double  LPB_Perc = 0;
		double  MPB_Perc = 0;
		double  HPB_Perc = 0;
		double  idle_Perc = 0;
		double idle_time = 0;
		double working_time = 0;
		double idle_time_perc = 0;
		double working_time_perc = 0;
		double engine_on = 0;
		double engine_off = 0;
		double engine_on_perc = 0;
		double engine_off_perc = 0;

		//DF20170510 - Rajani Nagaraju - Computing nonRpmHours - Adjustment value for the difference in period HMR and running band values
		double nonRpmHours;

		int total_period_hours = 24;
		final String country = "INDIA";
		int cityId = 0;
		int stateId = 0;
		Connection db2Connection = null;
		int row=1;
		final int partition_ID =1;
		String machineNumber = null;
		List<String> faultStatements = new LinkedList<String>();
		PreparedStatement pst= null;
		PreparedStatement pst1= null;
		Statement db2Statement = null;
		HashMap<String,Integer> statesMap = new HashMap<String, Integer>();
		HashMap<String,Integer> citiesMap = new HashMap<String, Integer>();
		String ac_type = null;

		DecimalFormat decformat=new DecimalFormat("0.000000");

		DateUtil dateUtililty = new DateUtil();
		DateUtil dateUtililty2 = null;
		long iteratorStart=0;

		ResultSet citiesRS=null;
		Statement cityst=null;
		ResultSet updateRS=null;
		Statement updatest=null;
		ResultSet queryByCreatedTSRS=null;

		String msgID=null;
		
		String prevDayTxnData=null;
		HashMap<String,String> preTtxnDataMap=new HashMap<String, String>();

		try {
			iLogger.info(timeKey+" is executing day agg... with time_key "+timeKey);
			iLogger.info("getting proddb2 connection");
			db2Connection = new ConnectMySQL().getProdDb2Connection();
			iLogger.info("connected to proddb2 connection");
			//Df20170605 @Roopa Not inserting to state/city in case of updations

			/*		try {
				 cityst = db2Connection.createStatement();
				 citiesRS = cityst.executeQuery("select * from City");

				while(citiesRS.next()){
					int cityID = citiesRS.getInt("CityID");
					String City = citiesRS.getString("CityName");
					citiesMap.put(City, cityID);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally{
				try{
				if(citiesRS!=null){
					citiesRS.close();
				}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				try{
					if(cityst!=null){
						cityst.close();
					}
					}
					catch(Exception e){
						e.printStackTrace();
					}

				try{
					if(db2Connection!=null){
						db2Connection.close();
					}
					}
					catch(Exception e){
						e.printStackTrace();
					}
			}
			try {
				if(db2Connection.isClosed())
					db2Connection = new ConnectMySQL().getProdDb2Connection();

				cityst = db2Connection.createStatement();
				 citiesRS = cityst.executeQuery("select * from State");

				while(citiesRS.next()){
					int stateID = citiesRS.getInt("StateID");
					String State = citiesRS.getString("StateName");
					statesMap.put(State, stateID);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally{
				try{
				if(citiesRS!=null){
					citiesRS.close();
				}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				try{
					if(cityst!=null){
						cityst.close();
					}
					}
					catch(Exception e){
						e.printStackTrace();
					}

				try{
					if(db2Connection!=null){
						db2Connection.close();
					}
					}
					catch(Exception e){
						e.printStackTrace();
					}
			}*/

			String migrationQuery = null;
			long queryStartTime = System.currentTimeMillis();


			String SerialNumber = null;
			Date TxnDate = null;
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			queryByCreatedTSstatement = prodConnection.createStatement();

			String remote_monitoring_fact_data_dayagg=null;



			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

			Date txnday=sdf1.parse(timeKey);
			Calendar cal  = Calendar.getInstance();
			cal.setTime(txnday);
			cal.add(Calendar.DATE, +1);
			Timestamp etlstartDateNext = new Timestamp(cal.getTime().getTime());


			remote_monitoring_fact_data_dayagg="select Serial_Number,Time_Key " +
					"from remote_monitoring_fact_data_dayagg_json_new where Date(Created_Timestamp) = '"+etlstartDateNext+"' and time_key < '"+timeKey+"' and Engine_Off_Hours >=0 order by Time_Key";

			//whereQuery=" Date(dayagg.Created_Timestamp) = '"+timeKey+"' and dayagg.time_key < '"+timeKey+"'";
			if(VIN!=null && (VIN.trim().length()==7 || VIN.trim().length()==17)){

				if(VIN.trim().length()==7){
					remote_monitoring_fact_data_dayagg="select Serial_Number,Time_Key " +
							"from remote_monitoring_fact_data_dayagg_json_new where Serial_Number like '%"+VIN+"' and Date(Created_Timestamp) = '"+etlstartDateNext+"' and time_key < '"+timeKey+"' and Engine_Off_Hours >=0 order by Time_Key";	

					//whereQuery=" dayagg.Serial_Number like '%"+VIN+"' and Date(dayagg.Created_Timestamp) = '"+timeKey+"' and dayagg.time_key < '"+timeKey+"'";
				}
				else if(VIN.trim().length()==17){

					remote_monitoring_fact_data_dayagg="select Serial_Number,Time_Key " +
							"from remote_monitoring_fact_data_dayagg_json_new where Serial_Number = '"+VIN+"' and Date(Created_Timestamp) = '"+etlstartDateNext+"' and time_key < '"+timeKey+"' and Engine_Off_Hours >=0 order by Time_Key";	
					//whereQuery=" dayagg.Serial_Number = '"+VIN+"' and Date(dayagg.Created_Timestamp) = '"+timeKey+"' and dayagg.time_key < '"+timeKey+"'";
				}
			}


			queryByCreatedTSRS = queryByCreatedTSstatement.executeQuery(remote_monitoring_fact_data_dayagg);


			//DF20170510 - Rajani Nagaraju - Computing nonRpmHours - Adjustment value for the difference in period HMR and running band values
			/*String insertStatement = "  insert into factInsight_dayAgg(AssetID,TxnDate,TxnWeek,TxnMonth,TxnQuarter,TxnYear,"+
					"ModelCode,ModelName,ProfileCode,ProfileName,ZonalCode,ZonalName,DealerCode,DealerName,CustCode,CustomerName,DealerContact,CustContact,PrimaryOwner," +
					"Latitude,Longitude,Address,StateId,State,CityId,City,Country," +
					"StartEngRunHrs,FinishEngRunHrs,PeriodHMR,AvgUtilizationPerct,ERB1,ERB2,ERB3,ERB4,ERB5,ERB6,ERB7,"+
					"PowerBandLow,PowerBandMed,PowerBandHigh,PowerBandIdle,IdleTime,WorkingTime,EngineOnTime,EngineOffTime,"+
					"EngineOnPerct,EngineOffPerct,IdleTimePerct,WorkingTimePerct,LowPowerBandPerct,MedPowerBandPerct,HighPowerBandPerct,IdlePowerBandPerct,PartitionID,MachineNum" +
					",LastEngineRun,LastReported,LastEngineStatus,Column1,Column2,Column3,Column4) "+
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";*/
			
			//Df20180112 @Roopa CMS and WLS integration
			/*String insertStatement = "  insert into factInsight_dayAgg(AssetID,TxnDate,TxnWeek,TxnMonth,TxnQuarter,TxnYear,"+
					"ModelCode,ModelName,ProfileCode,ProfileName,ZonalCode,ZonalName,DealerCode,DealerName,CustCode,CustomerName,DealerContact,CustContact,PrimaryOwner," +
					"Latitude,Longitude,Address,StateId,State,CityId,City,Country," +
					"StartEngRunHrs,FinishEngRunHrs,PeriodHMR,AvgUtilizationPerct,ERB1,ERB2,ERB3,ERB4,ERB5,ERB6,ERB7,"+
					"PowerBandLow,PowerBandMed,PowerBandHigh,PowerBandIdle,IdleTime,WorkingTime,EngineOnTime,EngineOffTime,"+
					"EngineOnPerct,EngineOffPerct,IdleTimePerct,WorkingTimePerct,LowPowerBandPerct,MedPowerBandPerct,HighPowerBandPerct,IdlePowerBandPerct,PartitionID,MachineNum" +
					",LastEngineRun,LastReported,LastEngineStatus,Column1,Column2,Column3,Column4" +
					",Column5,Column6,Column7,Column8,Column9,Column10,Column11,Column12,Column13,Column14,Column15,Column16,Column17,Column18,Column19,Column20,FuelRate,StartFuelInLtrs,FinishFuelInLtrs,FuelUsedInLtrs) "+
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";*/
			
			//DF20180228 @Roopa GENSET integration
			String insertStatement = "  insert into factInsight_dayAgg(AssetID,TxnDate,TxnWeek,TxnMonth,TxnQuarter,TxnYear,"+
					"ModelCode,ModelName,ProfileCode,ProfileName,ZonalCode,ZonalName,DealerCode,DealerName,CustCode,CustomerName,DealerContact,CustContact,PrimaryOwner," +
					"Latitude,Longitude,Address,StateId,State,CityId,City,Country," +
					"StartEngRunHrs,FinishEngRunHrs,PeriodHMR,AvgUtilizationPerct,ERB1,ERB2,ERB3,ERB4,ERB5,ERB6,ERB7,"+
					"PowerBandLow,PowerBandMed,PowerBandHigh,PowerBandIdle,IdleTime,WorkingTime,EngineOnTime,EngineOffTime,"+
					"EngineOnPerct,EngineOffPerct,IdleTimePerct,WorkingTimePerct,LowPowerBandPerct,MedPowerBandPerct,HighPowerBandPerct,IdlePowerBandPerct,PartitionID,MachineNum" +
					",LastEngineRun,LastReported,LastEngineStatus,Column1,Column2,Column3,Column4" +
					",Column5,Column6,Column7,Column8,Column9,Column10,Column11,Column12,Column13,Column14,Column15,Column16,Column17,Column18,Column19,Column20,FuelRate,StartFuelInLtrs,FinishFuelInLtrs,FuelUsedInLtrs"+
					",FuelUsedInLPB,FuelUsedInMPB,FuelUsedInHPB,AvgFuelConsumption,FuelLoss,FuelUsedInWorking,StartLMode,FinishLMode"+
					",LModeInHrs,StartGMode,FinishGMode,GModeInHrs,StartHMode,FinishHMode,HModeInHrs,StartHPlusMode,FinishHPlusMode,HPlusModeInHrs,StartTravellingTime"+
					",FinishTravellingTime,TravellingTimeInHrs,StartSlewTime,FinishSlewTime,SlewTimeInHrs,StartHammerUseTime,FinishHammerUseTime"+
					",HammerUseTimeInHrs,HammerAbuseCount,CarbonEmission,PowerBoostTime,RegenerationTime,OperatorOutOfSeatCount,AverageVehicleSpeed,EngineManufacturer"+
					",Gear1FwdUtilization,Gear1BkwdUtilization,Gear1LockupUtilization,Gear2FwdUtilization,Gear2BkwdUtilization,Gear2LockupUtilization"+
					",Gear31FwdUtilization,Gear3BkwdUtilization,Gear3LockupUtilization,Gear4FwdUtilization,Gear4BkwdUtilization,Gear4LockupUtilization,Gear5FwdUtilization,Gear5BkwdUtilization,Gear5LockupUtilization,Gear6FwdUtilization) "+
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
					"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			long endQueryTime = System.currentTimeMillis();

			//DF20170510 - Rajani Nagaraju - Computing nonRpmHours - Adjustment value for the difference in period HMR and running band values
			
			//Df20180112 @Roopa CMS and WLS integration
			//DF20180228 @Roopa GENSET integration
			String updateStatement = "update factInsight_dayAgg set StartEngRunHrs = ?,FinishEngRunHrs = ?,PeriodHMR = ?," +
					"ERB1 = ?,ERB2 = ?,ERB3 = ?,ERB4 = ?,ERB5 = ?,ERB6 = ?,ERB7 = ?,PowerBandLow=?,PowerBandMed=?," +
					"PowerBandHigh=?,PowerBandIdle=?,IdleTime=?,WorkingTime=?,EngineOnTime=?,EngineOffTime=?,Column1=?," +
					"Column5=?,Column6=?,Column7=?,Column8=?,Column9=?,Column10=?,Column11=?,Column12=?,Column13=?,Column14=?,Column15=?,Column16=?,Column17=?,Column18=?,Column19=?,Column20=?,FuelRate=?,StartFuelInLtrs=?,FinishFuelInLtrs=?,FuelUsedInLtrs=? " +
					",FuelUsedInLPB=?,FuelUsedInMPB=?,FuelUsedInHPB=?,AvgFuelConsumption=?,FuelLoss=?,FuelUsedInWorking=?,StartLMode=?,FinishLMode=?"+
					",LModeInHrs=?,StartGMode=?,FinishGMode=?,GModeInHrs=?,StartHMode=?,FinishHMode=?,HModeInHrs=?,StartHPlusMode=?,FinishHPlusMode=?,HPlusModeInHrs=?,StartTravellingTime=?"+
					",FinishTravellingTime=?,TravellingTimeInHrs=?,StartSlewTime=?,FinishSlewTime=?,SlewTimeInHrs=?,StartHammerUseTime=?,FinishHammerUseTime=?"+
					",HammerUseTimeInHrs=?,HammerAbuseCount=?,CarbonEmission=?,PowerBoostTime=?,RegenerationTime=?,OperatorOutOfSeatCount=?,AverageVehicleSpeed=?,EngineManufacturer=?"+
					",Gear1FwdUtilization=?,Gear1BkwdUtilization=?,Gear1LockupUtilization=?,Gear2FwdUtilization=?,Gear2BkwdUtilization=?,Gear2LockupUtilization=?"+
					",Gear31FwdUtilization=?,Gear3BkwdUtilization=?,Gear3LockupUtilization=?,Gear4FwdUtilization=?,Gear4BkwdUtilization=?,Gear4LockupUtilization=?"+
					",Gear5FwdUtilization=?,Gear5BkwdUtilization=?,Gear5LockupUtilization=?,Gear6FwdUtilization=?"+
					" where AssetID = ?" +
					" and TxnDate = ? ";

			//String insertQuery = insertStatement + values;
			iLogger.info(timeKey+" thread execution time for query"+((int)(((endQueryTime-queryStartTime)/1000)%60))+"");
			//System.out.println(timeKey+" thread execution time for query"+((int)(((endQueryTime-queryStartTime)/1000)%60))+"");
			if(db2Connection.isClosed())
				db2Connection = new ConnectMySQL().getProdDb2Connection();
			pst = db2Connection.prepareStatement(insertStatement);
			db2Statement = db2Connection.createStatement();
			pst1 = db2Connection.prepareStatement(updateStatement);
			updatest=db2Connection.createStatement();

			while(queryByCreatedTSRS.next()){
				SerialNumber = queryByCreatedTSRS.getString("Serial_Number");
				TxnDate = queryByCreatedTSRS.getDate("Time_Key");


				migrationQuery = "select finalDayAgg.*,"+
						"(finalDayAgg.ERB1+finalDayAgg.ERB2) as LPB,"+
						"(finalDayAgg.ERB3+finalDayAgg.ERB4+finalDayAgg.ERB5) as MPB,"+
						"(finalDayAgg.ERB6+finalDayAgg.ERB7) as HPB,"+
						"(finalDayAgg.ERB1+finalDayAgg.ERB2) as Idle_Time,"+
						"(finalDayAgg.ERB3+finalDayAgg.ERB4+finalDayAgg.ERB5+finalDayAgg.ERB6+finalDayAgg.ERB7+finalDayAgg.ERB8) as WorkingTime  , cc.*"+
						" from ("+
						" select dayagg.Serial_Number as Serial_Number,a.Machine_Number,"+
						"dayagg.Time_Key as timeKey,"+
						"Week(dayagg.Time_Key)+1 as week_no,"+
						"MONTH(dayagg.Time_Key) as month,"+
						"QUARTER(dayagg.Time_Key) as quarter,"+
						"YEAR(dayagg.Time_Key) as year,"+
						"dayagg.agg_param_data,"+
						"dayagg.EngineRunningBand1 as ERB1,dayagg.EngineRunningBand2 as ERB2,dayagg.EngineRunningBand3 as ERB3,dayagg.EngineRunningBand4 as ERB4,dayagg.EngineRunningBand5 as ERB5,dayagg.EngineRunningBand6 as ERB6,dayagg.EngineRunningBand7 as ERB7,dayagg.EngineRunningBand8 as ERB8,"+
						"a_t.Asset_Type_Code as asset_type,a_t.Asset_Type_Name as type_name,agp.asset_grp_code as group_code,ag.Asseet_Group_Name as group_name,"+
						//"ac.Account_Code as customerCode,ac.account_name as CustomerName,pa.Account_Code as parentCode,pa.account_name as DealerName,za.Account_Code as ZonalCode,za.account_name as ZonalName,ac.Account_Code as  PrimaryOwner,ac.Mobile as Customer_Number,pa.Mobile as Dealer_Number,"+
						"dayagg.Machine_Hours as EndEHours,"+
						"dayagg.Location as location,dayagg.Address as address,dayagg.City as city,dayagg.State as state" +
						//",td.Tenancy_Type_Name as ac_type" +
						" ,dayagg.Last_Engine_Run,dayagg.Last_Reported,dayagg.Engine_Status "+ 
						" from remote_monitoring_fact_data_dayagg_json_new dayagg,"+
						"asset a,products p,"+
						"asset_type a_t,asset_group ag,asset_group_profile agp "+
						//"tenancy_dimension td,account_tenancy act,"+
						//"account ac,account pa left outer join account za on za.account_id = pa.Parent_ID "+ 
						"where time_key = '"+TxnDate+"' and " +
						"a.Serial_Number = '"+SerialNumber+"' and "+ 
						"dayagg.Serial_Number = a.Serial_Number and "+
						"p.Product_ID = a.Product_ID and "+ 
						"ag.Asset_Group_ID = p.Asset_Group_ID and "+ 
						"agp.asset_grp_id = p.Asset_Group_ID and "+ 
						"a_t.Asset_Type_ID = p.Asset_Type_ID " +
						//"and "+
						//"td.tenancy_dimension_id = dayagg.tenancy_id and "+ 
						//"act.tenancy_id = td.tenancy_id and "+ 
						//"ac.account_id = act.account_id and "+ 
						//"pa.account_id = ac.Parent_ID "+  
						") finalDayAgg left outer join"+
						" (select prevdayAgg.serial_number as lastSerial_Number, prevdayAgg.machine_hours as startEHours, prevdayAgg.time_key, prevdayAgg.agg_param_data  as lastagg_param_data "+
						" from remote_monitoring_fact_data_dayagg_json_new prevdayAgg"+ 
						" left outer join "+
						"(select max(sn.Time_key) tk,sn.serial_number ser"+
						"	from remote_monitoring_fact_data_dayagg_json_new sn"+
						" where time_key < '"+TxnDate+"' " +
						"and Serial_Number = '"+SerialNumber+"'"+
						" group by sn.serial_number"+
						") bb"+
						" on prevdayAgg.serial_number=bb.ser"+ 
						" where prevdayAgg.Time_Key=bb.tk"+
						") cc"+
						" on finalDayAgg.serial_number=cc.lastSerial_Number";

				//iLogger.info(migrationQuery);



				if(statement==null || statement.isClosed())
					statement = prodConnection.createStatement();

				rs = statement.executeQuery(migrationQuery);

				iLogger.info(timeKey+" before executing while loop for query");
				//System.out.println(timeKey+"before executing while loop for query");
				iteratorStart = System.currentTimeMillis();

				HashMap<String,String> txnDataMap=new HashMap<String, String>();

				String txnData=null;

				if(rs.next()){

					try{
						Serial_Number = rs.getString("Serial_Number");
						machineNumber = rs.getString("Machine_Number");
						Date txnDate = rs.getDate("timeKey");

						dateUtililty2 = dateUtililty.getCurrentDateUtility(txnDate);

						week = dateUtililty2.week;

						//week = rs.getInt("week_no");

						month = rs.getInt("month");

						quarter = rs.getInt("quarter");

						year = rs.getInt("year");



						ERB1=Double.parseDouble(decformat.format(rs.getDouble("ERB1")));

						ERB2 = Double.parseDouble(decformat.format(rs.getDouble("ERB2")));

						ERB3 = Double.parseDouble(decformat.format(rs.getDouble("ERB3")));

						ERB4 = Double.parseDouble(decformat.format(rs.getDouble("ERB4")));

						ERB5 = Double.parseDouble(decformat.format(rs.getDouble("ERB5")));

						ERB6 = Double.parseDouble(decformat.format(rs.getDouble("ERB6")));

						ERB7 = Double.parseDouble(decformat.format(rs.getDouble("ERB7")));

						asset_type = rs.getString("asset_type");

						type_name = rs.getString("type_name");

						group_id = rs.getString("group_code");

						group_name = rs.getString("group_name");

					

						startEHours = rs.getDouble("StartEHours");

					

						endEHours = rs.getDouble("EndEHours");
						if(startEHours != 0){
							

							period_hours = endEHours - startEHours;
						}
						else{
							

							period_hours = endEHours;
						}
						

						AVG_Uitlization = (period_hours/24)*100;

					

						LPB = Double.parseDouble(decformat.format(rs.getDouble("LPB")));
						MPB = Double.parseDouble(decformat.format(rs.getDouble("MPB")));
						HPB = Double.parseDouble(decformat.format(rs.getDouble("HPB")));

						idle_band = LPB;


					

						idle_time = Double.parseDouble(decformat.format(rs.getDouble("Idle_Time")));

						working_time = Double.parseDouble(decformat.format(rs.getDouble("WorkingTime")));

						//DF20170821 @Roopa adding ERB8 to ERB 11 for working time calculations for the machines which has data

						if(rs.getObject("agg_param_data")!=null)
							txnData=rs.getObject("agg_param_data").toString();

						if(txnData!=null)
							txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, String>>() {}.getType());
						
						if(rs.getObject("lastagg_param_data")!=null)
							prevDayTxnData=rs.getObject("lastagg_param_data").toString();

						if(prevDayTxnData!=null)
							preTtxnDataMap = new Gson().fromJson(prevDayTxnData, new TypeToken<HashMap<String, String>>() {}.getType());

						if(txnDataMap!=null && txnDataMap.size()>0){

							if(txnDataMap.get("ERB9")!=null && txnDataMap.get("ERB10")!=null && txnDataMap.get("ERB11")!=null){
								working_time=working_time+Double.parseDouble(txnDataMap.get("ERB9"))+Double.parseDouble(txnDataMap.get("ERB10"))+Double.parseDouble(txnDataMap.get("ERB11"));	
								working_time=Double.parseDouble(decformat.format(working_time));
							}
							customer_code = txnDataMap.get("customerCode");

							customer_name = txnDataMap.get("CustomerName");

							dealer_code = txnDataMap.get("dealerCode");

							dealer_name = txnDataMap.get("DealerName");


							zonal_code = txnDataMap.get("ZonalCode");

							zonal_name = txnDataMap.get("ZonalName");


							primary_owner_code = txnDataMap.get("PrimaryOwner");

							customer_number =txnDataMap.get("Customer_Number");

							dealer_number = txnDataMap.get("Dealer_Number");

							regionCode=txnDataMap.get("regionCode");
							regionName=txnDataMap.get("regionName");
							msgID=txnDataMap.get("MSG_ID");
						}

						engine_on = Double.parseDouble(decformat.format(idle_time + working_time));
						engine_off = Double.parseDouble(decformat.format(total_period_hours - engine_on));

						if(period_hours<0 || period_hours>=24)
							period_hours = engine_on;
						
						if(engine_on != 0)
						{
							

							LPB_Perc = Double.parseDouble(decformat.format((LPB/engine_on)*100));
							MPB_Perc = Double.parseDouble(decformat.format((MPB/engine_on)*100));
							HPB_Perc = Double.parseDouble(decformat.format((HPB/engine_on)*100));

							idle_Perc = LPB_Perc;
						}
						if(db2Connection.isClosed())
							db2Connection = new ConnectMySQL().getProdDb2Connection();

						if(updatest==null || updatest.isClosed())
							updatest = db2Connection.createStatement();

						updateRS = updatest.executeQuery("select * from factInsight_dayAgg where AssetID = '"+Serial_Number+"' and TxnDate = '"+txnDate+"'");

						if(updateRS.next()){
							iLogger.info("Migration for update for Serial_Number "+Serial_Number+"' and TxnDate = '"+txnDate);
							pst1.setDouble(1, startEHours);
							//stmtValues += startEHours+",";
							pst1.setDouble(2, endEHours);
							//stmtValues += endEHours+",";
							pst1.setDouble(3, period_hours);
							//stmtValues += period_hours+",";
							pst1.setDouble(4, ERB1);
							//stmtValues += ERB1+",";
							pst1.setDouble(5, ERB2);
							//stmtValues += ERB2+",";
							pst1.setDouble(6, ERB3);
							//stmtValues += ERB3+",";
							pst1.setDouble(7, ERB4);
							//stmtValues += ERB4+",";
							pst1.setDouble(8, ERB5);
							//stmtValues += ERB5+",";
							pst1.setDouble(9, ERB6);
							//stmtValues += ERB6+",";
							pst1.setDouble(10, ERB7);

							pst1.setDouble(11, LPB);
							//stmtValues += LPB+",";
							pst1.setDouble(12, MPB);
							//stmtValues += MPB+",";
							pst1.setDouble(13, HPB);
							//stmtValues += HPB+",";
							pst1.setDouble(14, idle_band);
							//	stmtValues += idle_band+",";
							pst1.setDouble(15, idle_time);
							//stmtValues += idle_time+",";
							pst1.setDouble(16, working_time);
							//stmtValues += working_time+",";
							pst1.setDouble(17, engine_on);
							//stmtValues += engine_on+",";
							pst1.setDouble(18, engine_off);


							//DF20170510 - Rajani Nagaraju - Computing nonRpmHours - Adjustment value for the difference in period HMR and running band values
							nonRpmHours = period_hours - (ERB1+ERB2+ERB3+ERB4+ERB5+ERB6+ERB7);
							pst1.setDouble(19, nonRpmHours);
							
							
							//Df20180112 @Roopa CMS and WLS integration
							
							pst1.setString(20, preTtxnDataMap.get("CMS_FV"));
							pst1.setString(21, txnDataMap.get("CMS_FV"));
							
							pst1.setString(22, preTtxnDataMap.get("CMS_AV"));
							pst1.setString(23, txnDataMap.get("CMS_AV"));
							
							pst1.setString(24, preTtxnDataMap.get("CMS_CV"));
							pst1.setString(25, txnDataMap.get("CMS_CV"));
							
							pst1.setString(26, preTtxnDataMap.get("CMS_DJ_SA")); //prev day value
							pst1.setString(27, txnDataMap.get("CMS_DJ_SA")); //server side accumulated value
							
							pst1.setString(28, preTtxnDataMap.get("CMS_HFVH"));
							pst1.setString(29, txnDataMap.get("CMS_HFVH"));
							
							pst1.setString(30, preTtxnDataMap.get("CMS_LFVH"));
							pst1.setString(31, txnDataMap.get("CMS_LFVH"));
							
							pst1.setString(32, txnDataMap.get("CMS_DI"));
							
							pst1.setString(33, preTtxnDataMap.get("CMS_VSOO_SA")); //prev day value
							pst1.setString(34, txnDataMap.get("CMS_VSOO_SA")); //server side accumulated value
							
							
							pst1.setString(35, txnDataMap.get("CMS_RPN"));
							
							pst1.setString(36, preTtxnDataMap.get("CMS_MMS_SA")); //prev day value
							pst1.setString(37, txnDataMap.get("CMS_MMS_SA")); //server side accumulated value
							
							pst1.setString(38, preTtxnDataMap.get("CMS_SPH"));
							pst1.setString(39, txnDataMap.get("CMS_SPH"));
							

							
							//DF20180227 @Roopa GENSET param integration
							
							pst1.setString(40, txnDataMap.get("LOAD_BLOCK1"));
							pst1.setString(41, txnDataMap.get("LOAD_BLOCK2"));
							pst1.setString(42, txnDataMap.get("LOAD_BLOCK3"));
							pst1.setString(43, txnDataMap.get("LOAD_BLOCK4"));
							pst1.setString(44, txnDataMap.get("LOAD_BLOCK5"));
							pst1.setString(45, txnDataMap.get("LOAD_BLOCK6"));
							pst1.setString(46, txnDataMap.get("UTIL_BC_OFF"));
							pst1.setString(47, txnDataMap.get("UTIL_BC_ON"));
							pst1.setString(48, preTtxnDataMap.get("KVA_HOURS"));
							pst1.setString(49, txnDataMap.get("KVA_HOURS"));
							pst1.setString(50, preTtxnDataMap.get("KW_HOURS"));
							pst1.setString(51, txnDataMap.get("KW_HOURS"));
							pst1.setString(52, preTtxnDataMap.get("Fuel_Used_at_the_Reporting_Time"));
							pst1.setString(53, txnDataMap.get("Fuel_Used_at_the_Reporting_Time"));
							pst1.setString(54, preTtxnDataMap.get("Fuel_used_at_Very_Low_Load"));
							pst1.setString(55, txnDataMap.get("Fuel_used_at_Very_Low_Load"));
							pst1.setString(56, preTtxnDataMap.get("Fuel_used_at_Low_Load"));
							pst1.setString(57, txnDataMap.get("Fuel_used_at_Low_Load"));
							pst1.setString(58, preTtxnDataMap.get("Fuel_used_at_Medium_Load"));
							pst1.setString(59, txnDataMap.get("Fuel_used_at_Medium_Load"));
							pst1.setString(60, preTtxnDataMap.get("Fuel_used_at_High_Load"));
							pst1.setString(61, txnDataMap.get("Fuel_used_at_High_Load"));
							pst1.setString(62, preTtxnDataMap.get("Fuel_used_at_Very_High_Load"));
							pst1.setString(63, txnDataMap.get("Fuel_used_at_Very_High_Load"));
							pst1.setString(64, preTtxnDataMap.get("Fuel_level_at_the_end_of_the_reporting_period"));
							pst1.setString(65, txnDataMap.get("Fuel_level_at_the_end_of_the_reporting_period"));
							pst1.setString(66, txnDataMap.get("GEN_BLK_VOLTAGE_PHASEA"));
							pst1.setString(67, txnDataMap.get("GEN_VOLTAGE_PHASEB"));
							pst1.setString(68, txnDataMap.get("GEN_VOLTAGE_PHASEC"));
							pst1.setString(69, txnDataMap.get("GEN_CURRENT_PHASEA"));
							pst1.setString(70, txnDataMap.get("GEN_CURRENT_PHASEB"));
							pst1.setString(71, txnDataMap.get("GEN_CURRENT_PHASEC"));
							pst1.setString(72, txnDataMap.get("GEN_AC_VOLTAGE"));
							pst1.setString(73, txnDataMap.get("GEN_NEUTRAL_AC_RMS_VOLTAGE"));
							pst1.setString(74, txnDataMap.get("GEN_PHASEA_LINE_VOLTAGE"));
							pst1.setString(75, txnDataMap.get("GEN_PHASEB_LINE_VOLTAGE"));
							pst1.setString(76, txnDataMap.get("GEN_PHASEC_LINE_VOLTAGE"));
							pst1.setString(77, txnDataMap.get("GEN_PHASEC_LINE_NEUTRAL_VOLTAGE"));
							pst1.setString(78, txnDataMap.get("GEN_FREQUENCY"));
							pst1.setString(79, txnDataMap.get("overall_Power_Factor"));
							pst1.setString(80, txnDataMap.get("load_on_phaseA"));
							pst1.setString(81, txnDataMap.get("load_on_phaseB"));
							pst1.setString(82, txnDataMap.get("load_on_phaseC"));
							
							pst1.setString(83, txnDataMap.get("GEN_PHASEB_LINE_NEUTRAL_VOLTAGE"));
							pst1.setString(84, txnDataMap.get("Engine_Coolant_temp"));
							pst1.setString(85, txnDataMap.get("Gen_Oil_Pressure"));
							
							//DF20180314 @Roopa Adding missed columns for the CAN model
							pst1.setString(86, txnDataMap.get("LEPB"));
							pst1.setString(87, txnDataMap.get("IEPB"));
							pst1.setString(88, txnDataMap.get("MEPB"));
							pst1.setString(89, txnDataMap.get("HEPB"));
							
							pst1.setString(90, Serial_Number);
							pst1.setDate(91, new java.sql.Date(txnDate.getTime()));
							pst1.addBatch();

							if(updateRS!=null){
								try{
									updateRS.close();	
								}
								catch(Exception e){

								}
							}
							//	continue;
						}
						else{

							//Df20170605 @Roopa Not inserting to state/city in case of updations

							/*if(rs.getString("location")!=null)
						{
							String location = rs.getString("location");
							String[] lat_long = location.split(",");
							latitude = Double.parseDouble(lat_long[0]);
							longitude = Double.parseDouble(lat_long[1]);
						}
						address = rs.getString("address");
						city = rs.getString("city");
						state = rs.getString("state");
						if(address == null || state == null){
							iLogger.info("getDayAGGData : state is null so abnormal termination ");
							break;
						}
						if(state !=null && city != null){

							if(state!=null){
								if(!statesMap.isEmpty() && statesMap.containsKey(state)){

									stateId = statesMap.get(state);
								}
								else{
									try{

										if(db2Statement==null || db2Statement.isClosed())
											db2Statement = db2Connection.createStatement();

										db2Statement.executeUpdate("insert into State(StateName,Country) values('"+state+"','"+country+"')");

										rs1 = db2Statement.executeQuery("select * from State where StateName like '"+state+"'");
										if(rs1.next()){
											stateId = rs1.getInt("StateID");
											statesMap.put(state,stateId);
										}
									}catch(SQLException e){
										try {

											if(db2Statement==null || db2Statement.isClosed())
												db2Statement = db2Connection.createStatement();

											rs1 = db2Statement.executeQuery("select * from State where StateName like '"+state+"'");
											if(rs1.next()){
												stateId = rs1.getInt("StateID");
												statesMap.put(state,stateId);
											}

										} catch (SQLException se) {
											se.printStackTrace();
										} 
									}
									finally{
										if(rs1!=null){
											try{
												rs1.close();	
											}
											catch(Exception e){

											}
										}
									}
								}
							}
							if(city!=null){
								if(!citiesMap.isEmpty() && citiesMap.containsKey(city)){
									cityId = citiesMap.get(city);
								}
								else {
									try{

										if(db2Statement==null || db2Statement.isClosed())
											db2Statement = db2Connection.createStatement();

										db2Statement.executeUpdate("insert into City(CityName,StateID) values('"+city+"',"+stateId+")");


										rs2 = db2Statement.executeQuery("select c.StateID,c.CityID from City c where c.CityName like '"+city+"'");
										if(rs2.next())
										{
											stateId = rs2.getInt("StateID");
											cityId = rs2.getInt("CityID");
											citiesMap.put(city, cityId);
										}
									}catch(SQLException e){
										try {

											if(db2Statement==null || db2Statement.isClosed())
												db2Statement = db2Connection.createStatement();

											rs2 = db2Statement.executeQuery("select c.StateID,c.CityID from City c where c.CityName like '"+city+"'");
											if(rs2.next()){

												stateId = rs2.getInt("StateID");
												cityId = rs2.getInt("CityID");
												citiesMap.put(city, cityId);
											}

										} catch (SQLException se) {
											se.printStackTrace();
										} 
									}

									finally{
										if(rs2!=null){
											try{
												rs2.close();	
											}
											catch(Exception e){

											}
										}
									}
								}
							}
						}*/




							idle_time_perc = Double.parseDouble(decformat.format((idle_time/total_period_hours)*100));
							working_time_perc = Double.parseDouble(decformat.format((working_time_perc/total_period_hours)*100));

							engine_on_perc = Double.parseDouble(decformat.format((engine_on/total_period_hours)*100));
							engine_off_perc = Double.parseDouble(decformat.format(100 - engine_on_perc));


							pst.setString(1, Serial_Number);


							pst.setDate(2, new java.sql.Date(TxnDate.getTime()));
							//stmtValues += "'"+new java.sql.Date(sdf.parse(timeKey).getTime())+"',";

							pst.setInt(3, week);
							//stmtValues += week+",";
							pst.setInt(4, month);
							//stmtValues += month+",";
							pst.setInt(5, quarter);
							//stmtValues += quarter+",";
							pst.setInt(6, year);
							//stmtValues += year+",";
							pst.setString(7, asset_type);
							//stmtValues += asset_type+",";
							pst.setString(8, type_name);
							//stmtValues += "'"+type_name+"',";
							pst.setString(9, group_id);
							//stmtValues += group_id+",";
							pst.setString(10,group_name);
							

							pst.setString(11, zonal_code);
							pst.setString(12, zonal_name);
							pst.setString(13, dealer_code);
							pst.setString(14, dealer_name);
							pst.setString(15, customer_code);
							pst.setString(16, customer_name);
							pst.setString(17, dealer_number);
							pst.setString(18, customer_number);
							pst.setString(19, primary_owner_code);

							pst.setDouble(20, latitude);
							//stmtValues += latitude+",";
							pst.setDouble(21, longitude);
							//stmtValues += longitude+",";
							pst.setString(22,address);
							//stmtValues += "'"+address+"',";
							pst.setInt(23,stateId);
							//stmtValues += stateId+",";
							pst.setString(24,state);
							pst.setInt(25,cityId);
							//stmtValues += cityId+",";
							pst.setString(26,city);
							//stmtValues += "'"+state+"',";
							pst.setString(27,country);
							//stmtValues += "'"+country+"',";
							pst.setDouble(28, startEHours);
							//stmtValues += startEHours+",";
							pst.setDouble(29, endEHours);
							//stmtValues += endEHours+",";
							pst.setDouble(30, period_hours);
							//stmtValues += period_hours+",";
							pst.setDouble(31, AVG_Uitlization);
							//stmtValues += AVG_Uitlization+",";
							pst.setDouble(32, ERB1);
							//stmtValues += ERB1+",";
							pst.setDouble(33, ERB2);
							//stmtValues += ERB2+",";
							pst.setDouble(34, ERB3);
							//stmtValues += ERB3+",";
							pst.setDouble(35, ERB4);
							//stmtValues += ERB4+",";
							pst.setDouble(36, ERB5);
							//stmtValues += ERB5+",";
							pst.setDouble(37, ERB6);
							//stmtValues += ERB6+",";
							pst.setDouble(38, ERB7);
							//stmtValues += ERB7+",";

							pst.setDouble(39, LPB);
							//stmtValues += LPB+",";
							pst.setDouble(40, MPB);
							//stmtValues += MPB+",";
							pst.setDouble(41, HPB);
							//stmtValues += HPB+",";
							pst.setDouble(42, idle_band);
							//	stmtValues += idle_band+",";
							pst.setDouble(43, idle_time);
							//stmtValues += idle_time+",";
							pst.setDouble(44, working_time);
							//stmtValues += working_time+",";
							pst.setDouble(45, engine_on);
							//stmtValues += engine_on+",";
							pst.setDouble(46, engine_off);
							//stmtValues += engine_off+",";
							pst.setDouble(47,engine_on_perc);
							//stmtValues += engine_on_perc+",";
							pst.setDouble(48,engine_off_perc);
							//stmtValues += engine_off_perc+",";
							pst.setDouble(49, idle_time_perc);
							//stmtValues += idle_time_perc+",";
							pst.setDouble(50,working_time_perc);
							//stmtValues += working_time_perc+",";
							pst.setDouble(51,LPB_Perc);
							//stmtValues += LPB_Perc+",";
							pst.setDouble(52,MPB_Perc);
							//stmtValues += MPB_Perc+",";
							pst.setDouble(53,HPB_Perc);
							//stmtValues += HPB_Perc+",";
							pst.setDouble(54,idle_Perc);
							//stmtValues += idle_Perc+",";
							//stmtValues += idle_Perc;
							pst.setInt(55, partition_ID);
							//stmtValues += ","+partition_ID;
							pst.setString(56, machineNumber);
							pst.setTimestamp(57, rs.getTimestamp("Last_Engine_Run"));

							pst.setTimestamp(58, rs.getTimestamp("Last_Reported"));

							pst.setInt(59, rs.getInt("Engine_Status"));

							//DF20170510 - Rajani Nagaraju - Computing nonRpmHours - Adjustment value for the difference in period HMR and running band values
							nonRpmHours = period_hours - (ERB1+ERB2+ERB3+ERB4+ERB5+ERB6+ERB7);
							pst.setDouble(60,nonRpmHours);

							//DF20170823 @Roopa inclusion of region

							pst.setString(61, regionCode);
							pst.setString(62, regionName);

							//Df20171113 @Roopa including msgID for BHL differentiation for reports

							pst.setString(63, msgID);
							
							//Df20180112 @Roopa CMS and WLS integration
							
							pst.setString(64, preTtxnDataMap.get("CMS_FV"));
							pst.setString(65, txnDataMap.get("CMS_FV"));
							
							pst.setString(66, preTtxnDataMap.get("CMS_AV"));
							pst.setString(67, txnDataMap.get("CMS_AV"));
							
							pst.setString(68, preTtxnDataMap.get("CMS_CV"));
							pst.setString(69, txnDataMap.get("CMS_CV"));
							
							pst.setString(70, preTtxnDataMap.get("CMS_DJ_SA")); //prev day value
							pst.setString(71, txnDataMap.get("CMS_DJ_SA")); //server side accumulated value
							
							pst.setString(72, preTtxnDataMap.get("CMS_HFVH"));
							pst.setString(73, txnDataMap.get("CMS_HFVH"));
							
							pst.setString(74, preTtxnDataMap.get("CMS_LFVH"));
							pst.setString(75, txnDataMap.get("CMS_LFVH"));
							
							pst.setString(76, txnDataMap.get("CMS_DI"));
							
							pst.setString(77, preTtxnDataMap.get("CMS_VSOO_SA")); //prev day value
							pst.setString(78, txnDataMap.get("CMS_VSOO_SA")); //server side accumulated value
							
							
							pst.setString(79, txnDataMap.get("CMS_RPN"));
							
							pst.setString(80, preTtxnDataMap.get("CMS_MMS_SA")); //prev day value
							pst.setString(81, txnDataMap.get("CMS_MMS_SA")); //server side accumulated value
							
							pst.setString(82, preTtxnDataMap.get("CMS_SPH"));
							pst.setString(83, txnDataMap.get("CMS_SPH"));
							
							//DF20180227 @Roopa GENSET param integration
							
							pst.setString(84, txnDataMap.get("LOAD_BLOCK1"));
							pst.setString(85, txnDataMap.get("LOAD_BLOCK2"));
							pst.setString(86, txnDataMap.get("LOAD_BLOCK3"));
							pst.setString(87, txnDataMap.get("LOAD_BLOCK4"));
							pst.setString(88, txnDataMap.get("LOAD_BLOCK5"));
							pst.setString(89, txnDataMap.get("LOAD_BLOCK6"));
							pst.setString(90, txnDataMap.get("UTIL_BC_OFF"));
							pst.setString(91, txnDataMap.get("UTIL_BC_ON"));
							pst.setString(92, preTtxnDataMap.get("KVA_HOURS"));
							pst.setString(93, txnDataMap.get("KVA_HOURS"));
							pst.setString(94, preTtxnDataMap.get("KW_HOURS"));
							pst.setString(95, txnDataMap.get("KW_HOURS"));
							pst.setString(96, preTtxnDataMap.get("Fuel_Used_at_the_Reporting_Time"));
							pst.setString(97, txnDataMap.get("Fuel_Used_at_the_Reporting_Time"));
							pst.setString(98, preTtxnDataMap.get("Fuel_used_at_Very_Low_Load"));
							pst.setString(99, txnDataMap.get("Fuel_used_at_Very_Low_Load"));
							pst.setString(100, preTtxnDataMap.get("Fuel_used_at_Low_Load"));
							pst.setString(101, txnDataMap.get("Fuel_used_at_Low_Load"));
							pst.setString(102, preTtxnDataMap.get("Fuel_used_at_Medium_Load"));
							pst.setString(103, txnDataMap.get("Fuel_used_at_Medium_Load"));
							pst.setString(104, preTtxnDataMap.get("Fuel_used_at_High_Load"));
							pst.setString(105, txnDataMap.get("Fuel_used_at_High_Load"));
							pst.setString(106, preTtxnDataMap.get("Fuel_used_at_Very_High_Load"));
							pst.setString(107, txnDataMap.get("Fuel_used_at_Very_High_Load"));
							pst.setString(108, preTtxnDataMap.get("Fuel_level_at_the_end_of_the_reporting_period"));
							pst.setString(109, txnDataMap.get("Fuel_level_at_the_end_of_the_reporting_period"));
							pst.setString(110, txnDataMap.get("GEN_BLK_VOLTAGE_PHASEA"));
							pst.setString(111, txnDataMap.get("GEN_VOLTAGE_PHASEB"));
							pst.setString(112, txnDataMap.get("GEN_VOLTAGE_PHASEC"));
							pst.setString(113, txnDataMap.get("GEN_CURRENT_PHASEA"));
							pst.setString(114, txnDataMap.get("GEN_CURRENT_PHASEB"));
							pst.setString(115, txnDataMap.get("GEN_CURRENT_PHASEC"));
							pst.setString(116, txnDataMap.get("GEN_AC_VOLTAGE"));
							pst.setString(117, txnDataMap.get("GEN_NEUTRAL_AC_RMS_VOLTAGE"));
							pst.setString(118, txnDataMap.get("GEN_PHASEA_LINE_VOLTAGE"));
							pst.setString(119, txnDataMap.get("GEN_PHASEB_LINE_VOLTAGE"));
							pst.setString(120, txnDataMap.get("GEN_PHASEC_LINE_VOLTAGE"));
							pst.setString(121, txnDataMap.get("GEN_PHASEC_LINE_NEUTRAL_VOLTAGE"));
							pst.setString(122, txnDataMap.get("GEN_FREQUENCY"));
							pst.setString(123, txnDataMap.get("overall_Power_Factor"));
							pst.setString(124, txnDataMap.get("load_on_phaseA"));
							pst.setString(125, txnDataMap.get("load_on_phaseB"));
							pst.setString(126, txnDataMap.get("load_on_phaseC"));
							
							pst.setString(127, txnDataMap.get("GEN_PHASEB_LINE_NEUTRAL_VOLTAGE"));
							pst.setString(128, txnDataMap.get("Engine_Coolant_temp"));
							pst.setString(129, txnDataMap.get("Gen_Oil_Pressure"));
							
							//DF20180314 @Roopa Adding missed columns for the CAN model
							pst.setString(130, txnDataMap.get("LEPB"));
							pst.setString(131, txnDataMap.get("IEPB"));
							pst.setString(132, txnDataMap.get("MEPB"));
							pst.setString(133, txnDataMap.get("HEPB"));

							//faultStatements.add(Serial_Number);
							pst.addBatch();
						}
					}catch(SQLException innerSQL){
						fLogger.fatal("Error occured in migration:"+innerSQL.getMessage());
						innerSQL.printStackTrace();
					}
					if(row%1000 == 0)
					{
						//System.out.println(timeKey+"roww:"+row);
						long iteratorEnd = System.currentTimeMillis();
						iLogger.info(timeKey+" 1000 iterations executed in: "+(iteratorEnd-iteratorStart));
						iteratorStart = System.currentTimeMillis();
						try{
							iLogger.info(timeKey+"before executingbatch updations for 1000 records");
							long preparedStmtStart = System.currentTimeMillis();
							pst.executeBatch();
							pst1.executeBatch();

							pst.clearBatch();
							pst1.clearBatch();
							long preparedStmtEnd = System.currentTimeMillis();
							iLogger.info(timeKey+" executing batch updations for 1000 records in "+(preparedStmtEnd-preparedStmtStart));



						}catch(BatchUpdateException bue){
							iLogger.info(" inner BatchUpdateexception"+bue.getMessage());
							fLogger.fatal("Error occured in migration:"+bue.getMessage());
							bue.printStackTrace();
							int[] updatedCount = bue.getUpdateCounts();


						}
						catch(Exception e){
							fLogger.fatal("Error occured in migration:"+e.getMessage());
							e.printStackTrace();


						}


						if(db2Connection == null || db2Connection.isClosed())
							db2Connection = new ConnectMySQL().getProdDb2Connection();
						if(pst==null || pst.isClosed())
							pst = db2Connection.prepareStatement(insertStatement);
						if(pst1==null || pst1.isClosed())
							pst1 = db2Connection.prepareStatement(updateStatement);
					}

					row++;

					if(rs!=null){
						try{
							rs.close();	
						}
						catch(Exception e){
							fLogger.fatal("Error occured in migration:"+e.getMessage());
						}
					}

				}
			}
			//System.out.println("row:"+row);
			iLogger.info(timeKey+"after executing while loop for query");

			if(db2Connection == null || db2Connection.isClosed())
				db2Connection = new ConnectMySQL().getProdDb2Connection();
			if(pst==null || pst.isClosed())
				pst = db2Connection.prepareStatement(insertStatement);
			if(pst1==null || pst1.isClosed())
				pst1 = db2Connection.prepareStatement(updateStatement);

			int[] updateVins = pst.executeBatch();
			pst1.executeBatch();

			pst.clearBatch();
			pst1.clearBatch();
			iLogger.info(timeKey+"after executing batch insertions");
			result += ": "+row;


		}
		catch(BatchUpdateException bue){
			iLogger.info("outer BatchUpdateexception"+bue.getMessage());
			fLogger.fatal("Error occured in migration:"+bue.getMessage());
			bue.printStackTrace();
			int[] updatedCount = bue.getUpdateCounts();


		}
		catch (SQLException e) {

			iLogger.info("SQL Exception ------"+e.getMessage());
			fLogger.fatal("Error occured in migration:"+e.getMessage());
			e.printStackTrace();
		} 
		catch(Exception e)
		{
			iLogger.info("Normal Exception ------"+e.getMessage());
			fLogger.fatal("Error occured in migration:"+e.getMessage());

			e.printStackTrace();
		}
		finally{
			try { if (rs != null) rs.close(); } catch (Exception e) {};
			try { if (rs1 != null) rs1.close(); } catch (Exception e) {};
			try { if (rs2 != null) rs2.close(); } catch (Exception e) {};

			try { if (citiesRS != null) citiesRS.close(); } catch (Exception e) {};

			try { if (queryByCreatedTSRS != null) queryByCreatedTSRS.close(); } catch (Exception e) {};

			if(updateRS!=null){
				try{
					updateRS.close();	
				}
				catch(Exception e){

				}
			}

			if(cityst!=null){
				try {
					cityst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


			if(updatest!=null){
				try{
					updatest.close();	
				}
				catch(Exception e){

				}
			}

			if(queryByCreatedTSstatement!=null){
				try{
					queryByCreatedTSstatement.close();	
				}
				catch(Exception e){

				}
			}

			if(statement!=null){
				try{
					statement.close();	
				}
				catch(Exception e){

				}
			}

			if(db2Statement!=null){
				try{
					db2Statement.close();	
				}
				catch(Exception e){

				}
			}

			if(localStatement!=null){
				try {
					localStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pst1!=null){
				try {
					pst1.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(db2Statement!=null){
				try {
					db2Statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(db2Connection!=null)
			{

				try {
					//db2Connection.commit();
					db2Connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


		}
		long threadEnd = System.currentTimeMillis();
		int threadExecutionTime = (int) (((threadEnd-threadStart) / (1000*60)) % 60);
		iLogger.info("Thread"+timeKey+" is finished... with "+result+" machines in "+threadExecutionTime+" mins");



		return result;
	}


}
