package remote.wise.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import remote.wise.dal.DynamicAMS_Doc_DAL;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AMSDoc_DAO;
import remote.wise.pojo.AssetMonitoringParametersDAO;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TitanMachineParameterBO {

	public HashMap<String,Double> getAMPValuesInTxnRange(Map asset2SegID,String txnTimestamp, String period){


		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		iLogger.info("AMH_DAL getAMPValuesInTxnRange: for START");
		
		HashMap<String,Double> response= new HashMap<String,Double>();

		String ampSelectQuery = "";
		String ampFromQuery = "";
		
		String PersistTo_InMemory = null;

		long startTime = 0;
		long endTime = 0;

		Properties prop = null;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;


		String startTAssetMonTable=null;
		String endTAssetMonTable=null;


			try{

				Calendar calendar1 = Calendar.getInstance();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//dateStr.setLenient(false);
				String currDate =txnTimestamp;
				String strCurrDate = currDate;

				Date date3 = dateFormat.parse(strCurrDate);
				Timestamp currTimestamp=new Timestamp(date3.getTime());
				//				Date date4 = dateFormat1.parse(currDate);
				calendar1.setTime(date3);
				// get previous day
				calendar1.add(Calendar.DAY_OF_YEAR, -1);

				String prevDate = dateFormat.format(calendar1.getTime());
				// data will be take from previous day 6:30 to today 6:20
				prevDate = prevDate + " 18:30:00";
				strCurrDate = strCurrDate + " 18:30:00";



				//System.out.println(prevDate+" currnetDate "+strCurrDate);
				Calendar cal = Calendar.getInstance();
				// Date st = dateStr.parse(strCurrDate);
				//System.out.println(st);
				cal.setTime(dateStr.parse(strCurrDate));

				Timestamp starttxnTimestamp = new Timestamp(cal.getTimeInMillis());

				String dynamicTable=new DateUtil().getDynamicTable("Fleet Utilization Service", starttxnTimestamp);

				if(dynamicTable!=null){
					startTAssetMonTable=dynamicTable;

				}

				cal.setTime(dateStr.parse(prevDate));

				Timestamp endtxnTimestamp = new Timestamp(cal.getTimeInMillis());


				String endDynamicTable=new DateUtil().getDynamicTable("Fleet Utilization Service", endtxnTimestamp);

				if(endDynamicTable!=null){
					endTAssetMonTable=endDynamicTable;

				}


				startTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getAMPValuesInTxnRange Query starts ");
				connection = connFactory.getDatalakeConnection_3309();
				
				Iterator it = asset2SegID.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry entry = (Map.Entry) it.next();
					String Serial_Number = (String) entry.getKey();
					int seg_ID = (Integer) entry.getValue();

					if(!startTAssetMonTable.equals(endTAssetMonTable)){
						double pwr_mode=0, ec_mode=0;
						ampSelectQuery = " select t.Transaction_Timestamp, t.Serial_Number, t.TxnData->'$.LOAD_PWR_MOD_HRS' as PWR_MOD, t.TxnData->'$.LOAD_EC_MOD_HRS' as EC_MOD";
						ampFromQuery = " from "+endTAssetMonTable+" t"
								+ " where t.Segment_ID_TxnDate = "+seg_ID+" and t.Serial_Number = '" + Serial_Number + "'"
								+ " and t.Transaction_Timestamp >= '" + endtxnTimestamp + "' and t.TxnData->'$.MSG_ID'='030' order by t.Transaction_Timestamp limit 1";

						String mainQuery = ampSelectQuery+ampFromQuery;
						iLogger.info("TitanMachineParameterBO: main query1: "+mainQuery);
						statement = connection.createStatement();
						rs = statement.executeQuery(mainQuery);
						if(rs.isBeforeFirst()){
							while(rs.next()){
								Timestamp timeStamp = rs.getTimestamp("Transaction_Timestamp");
								String serialNumber = rs.getString("Serial_Number");
								String PWR_MOD = rs.getString("PWR_MOD").replaceAll("[^0-9]", "");
								String EC_MOD = rs.getString("EC_MOD").replaceAll("[^0-9]", "");
								pwr_mode = Integer.valueOf(PWR_MOD!=null?PWR_MOD:"0");
								ec_mode = Integer.valueOf(EC_MOD!=null?EC_MOD:"0");
								
								iLogger.info("Titan machines max timestamp: "+timeStamp+"serialNumber: "+serialNumber+" PWR_MODE: "+pwr_mode+" EC_MODE: "+ec_mode);
							}
						}else{
							ampSelectQuery = " select t.Transaction_Timestamp, t.Serial_Number, t.TxnData->'$.LOAD_PWR_MOD_HRS' as PWR_MOD, t.TxnData->'$.LOAD_EC_MOD_HRS' as EC_MOD";
							ampFromQuery = " from "+startTAssetMonTable+" t "
									+ " where t.Segment_ID_TxnDate = "+seg_ID+" and t.Serial_Number = '" + Serial_Number + "'"
									+ " and t.Transaction_Timestamp <= '" + starttxnTimestamp + "' and t.TxnData->'$.MSG_ID'='030' order by t.Transaction_Timestamp limit 1";
							mainQuery = ampSelectQuery+ampFromQuery;
							iLogger.info("TitanMachineParameterBO: main query2: "+mainQuery);
							statement = connection.createStatement();
							rs = statement.executeQuery(mainQuery);
							while(rs.next()){
								Timestamp timeStamp = rs.getTimestamp("Transaction_Timestamp");
								String serialNumber = rs.getString("Serial_Number");
								String PWR_MOD = rs.getString("PWR_MOD").replaceAll("[^0-9]", "");
								String EC_MOD = rs.getString("EC_MOD").replaceAll("[^0-9]", "");
								pwr_mode = Integer.valueOf(PWR_MOD!=null?PWR_MOD:"0");
								ec_mode = Integer.valueOf(EC_MOD!=null?EC_MOD:"0");
								
								iLogger.info("Titan machines max timestamp: "+timeStamp+"serialNumber: "+serialNumber+" PWR_MODE: "+pwr_mode+" EC_MODE: "+ec_mode);
							}

						}


						ampSelectQuery = " select t.Transaction_Timestamp, t.Serial_Number, t.TxnData->'$.LOAD_PWR_MOD_HRS' as PWR_MOD, t.TxnData->'$.LOAD_EC_MOD_HRS' as EC_MOD";
						ampFromQuery = " from "+startTAssetMonTable+" t "
								+ " where t.Segment_ID_TxnDate = "+seg_ID+" and t.Serial_Number = '" + Serial_Number + "'"
								+ " and t.Transaction_Timestamp <= '" + starttxnTimestamp + "' and t.TxnData->'$.MSG_ID'='030' order by t.Transaction_Timestamp desc limit 1";

						mainQuery = ampSelectQuery+ampFromQuery;
						iLogger.info("TitanMachineParameterBO: main query3: "+mainQuery);
						
						statement = connection.createStatement();
						rs = statement.executeQuery(mainQuery);
						if(rs.isBeforeFirst()){
							while(rs.next()){
								Timestamp timeStamp = rs.getTimestamp("Transaction_Timestamp");
								String serialNumber = rs.getString("Serial_Number");
								String PWR_MOD = rs.getString("PWR_MOD").replaceAll("[^0-9]", "");
								String EC_MOD = rs.getString("EC_MOD").replaceAll("[^0-9]", "");
								int temp_pwr_mode = Integer.valueOf(PWR_MOD!=null?PWR_MOD:"0");
								int temp_ec_mode = Integer.valueOf(EC_MOD!=null?EC_MOD:"0");
								
								iLogger.info("Titan machines min timestamp: "+timeStamp+"serialNumber: "+serialNumber+" PWR_MODE: "+temp_pwr_mode+" EC_MODE: "+temp_ec_mode);
								pwr_mode= (temp_pwr_mode-pwr_mode)/3600;
								ec_mode= (temp_ec_mode-ec_mode)/3600;
								
								iLogger.info("Titan machines engine utilization for serialNumber: "+serialNumber+" PWR_MODE: "+pwr_mode+" EC_MODE: "+ec_mode);
							}
						}else{
							ampSelectQuery = " select t.Transaction_Timestamp, t.Serial_Number, t.TxnData->'$.LOAD_PWR_MOD_HRS' as PWR_MOD, t.TxnData->'$.LOAD_EC_MOD_HRS' as EC_MOD";
							ampFromQuery = " from "+endTAssetMonTable+" t"
									+ " where t.Segment_ID_TxnDate = "+seg_ID+" and t.Serial_Number = '" + Serial_Number + "'"
									+ " and t.Transaction_Timestamp >= '" + endtxnTimestamp + "' and t.TxnData->'$.MSG_ID'='030' order by t.Transaction_Timestamp desc limit 1";

							 mainQuery = ampSelectQuery+ampFromQuery;
							 iLogger.info("TitanMachineParameterBO: main query4: "+mainQuery);
							statement = connection.createStatement();
							rs = statement.executeQuery(mainQuery);
							while(rs.next()){
								Timestamp timeStamp = rs.getTimestamp("Transaction_Timestamp");
								String serialNumber = rs.getString("Serial_Number");
								String PWR_MOD = rs.getString("PWR_MOD").replaceAll("[^0-9]", "");
								String EC_MOD = rs.getString("EC_MOD").replaceAll("[^0-9]", "");
								int temp_pwr_mode = Integer.valueOf(PWR_MOD!=null?PWR_MOD:"0");
								int temp_ec_mode = Integer.valueOf(EC_MOD!=null?EC_MOD:"0");
								
								iLogger.info("Titan machines min timestamp: "+timeStamp+"serialNumber: "+serialNumber+" PWR_MODE: "+temp_pwr_mode+" EC_MODE: "+temp_ec_mode);
								pwr_mode= (temp_pwr_mode-pwr_mode)/3600;
								ec_mode= (temp_ec_mode-ec_mode)/3600;
								
								iLogger.info("Titan machines engine utilization for serialNumber: "+serialNumber+" PWR_MODE: "+pwr_mode+" EC_MODE: "+ec_mode);
							}
							
						}
						response.put("PWR_MODE",pwr_mode);
						response.put("EC_MODE",ec_mode);
					}
					else{
						double pwr_mode=0, ec_mode=0;
						ampSelectQuery = " select t.Transaction_Timestamp, t.Serial_Number, t.TxnData->'$.LOAD_PWR_MOD_HRS' as PWR_MOD, t.TxnData->'$.LOAD_EC_MOD_HRS' as EC_MOD";
						ampFromQuery = " from "+startTAssetMonTable+" t "
								+ " where t.Segment_ID_TxnDate = "+seg_ID+" and t.Serial_Number = '" + Serial_Number + "'"
								+ " and t.Transaction_Timestamp between '" + endtxnTimestamp 
								+ "' and '"+starttxnTimestamp+"' and t.TxnData->'$.MSG_ID'='030' order by t.Transaction_Timestamp desc limit 1";


						String mainQuery = ampSelectQuery+ampFromQuery;
						iLogger.info("TitanMachineParameterBO: main query5: "+mainQuery);
						statement = connection.createStatement();
						rs = statement.executeQuery(mainQuery);

						while(rs.next()){
							Timestamp timeStamp = rs.getTimestamp("Transaction_Timestamp");
							String serialNumber = rs.getString("Serial_Number");
							String PWR_MOD = rs.getString("PWR_MOD").replaceAll("[^0-9]", "");
							String EC_MOD = rs.getString("EC_MOD").replaceAll("[^0-9]", "");
							pwr_mode = Integer.valueOf(PWR_MOD!=null?PWR_MOD:"0");
							ec_mode = Integer.valueOf(EC_MOD!=null?EC_MOD:"0");
							
							iLogger.info("Titan machines max timestamp: "+timeStamp+"serialNumber: "+serialNumber+" PWR_MODE: "+pwr_mode+" EC_MODE: "+ec_mode);
						}
						
						ampSelectQuery = " select t.Transaction_Timestamp, t.Serial_Number, t.TxnData->'$.LOAD_PWR_MOD_HRS' as PWR_MOD, t.TxnData->'$.LOAD_EC_MOD_HRS' as EC_MOD";
						ampFromQuery = " from "+startTAssetMonTable+" t "
								+ " where t.Segment_ID_TxnDate = "+seg_ID+" and t.Serial_Number = '" + Serial_Number + "'"
								+ " and t.Transaction_Timestamp between '" + endtxnTimestamp 
								+ "' and '"+starttxnTimestamp+"' and t.TxnData->'$.MSG_ID'='030' order by t.Transaction_Timestamp limit 1";


						mainQuery = ampSelectQuery+ampFromQuery;
						iLogger.info("TitanMachineParameterBO: main query6: "+mainQuery);
						statement = connection.createStatement();
						rs = statement.executeQuery(mainQuery);

						while(rs.next()){
							Timestamp timeStamp = rs.getTimestamp("Transaction_Timestamp");
							String serialNumber = rs.getString("Serial_Number");
							String PWR_MOD = rs.getString("PWR_MOD").replaceAll("[^0-9]", "");
							String EC_MOD = rs.getString("EC_MOD").replaceAll("[^0-9]", "");
							int temp_pwr_mode = Integer.valueOf(PWR_MOD!=null?PWR_MOD:"0");
							int temp_ec_mode = Integer.valueOf(EC_MOD!=null?EC_MOD:"0");
							
							iLogger.info("Titan machines min timestamp: "+timeStamp+"serialNumber: "+serialNumber+" PWR_MODE: "+temp_pwr_mode+" EC_MODE: "+temp_ec_mode);
							pwr_mode= (pwr_mode-temp_pwr_mode)/3600;
							ec_mode= (ec_mode-temp_ec_mode)/3600;
							
							iLogger.info("Titan machines engine utilization for serialNumber: "+serialNumber+" PWR_MODE: "+pwr_mode+" EC_MODE: "+ec_mode);
							
						}
						
						response.put("PWR_MODE",pwr_mode);
						response.put("EC_MODE",ec_mode);
						
					}


				}//end of while
			}//end of try 
			catch(Exception ex){
				response.put("PWR_MODE",0.0);
				response.put("EC_MODE",0.0);
				ex.printStackTrace();				
				fLogger.fatal("AMH_DAL getAMPValuesInTxnRange: for  on "+txnTimestamp+" exception "+ex.getMessage());
				return response;
			}
			finally{
				try {
					if(statement != null && !statement.isClosed()){
						statement.close();

					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getAMPValuesInTxnRange exception "+e.getMessage());
					e.printStackTrace();
				}
				try {
					if(connection!= null && !connection.isClosed()){
						connection.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getAMPValuesInTxnRange exception "+e.getMessage());
					e.printStackTrace();
				}
				endTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getAMPValuesInTxnRange: for  on "+txnTimestamp+" Query ends in "+(endTime-startTime));	
			}
		return response;
	}
}
