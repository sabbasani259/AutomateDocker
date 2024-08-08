package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.IstGmtTimeConversion;
import remote.wise.util.ListToStringConversion;

/**
 * Implementation class to get Genset trends charts data
 * @author KO369761
 * DF20181214
 */

public class GensetTrendsDataImpl {


	public HashMap<String, HashMap<Integer, HashMap<String, Object>>> getGensetTrendsData(String loginId, String serialNumber, String period, String startDate, String endDate) throws CustomFault{

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		HashMap<String, HashMap<Integer,HashMap<String,Object>>> response =  new HashMap<String, HashMap<Integer,HashMap<String,Object>>>();
		HashMap<Integer,HashMap<String,Object>> hourWiseData = null;

		if(loginId==null){			
			bLogger.error("Please pass a LoginId");
			throw new CustomFault("Please pass a LoginId");
		}

		if(serialNumber==null){
			bLogger.error("Please pass a SerialNumber");
			throw new CustomFault("Please pass a SerialNumber");
		}

		if(period==null){
			bLogger.error("Please pass a period");
			throw new CustomFault("Please pass a period");
		}

		iLogger.info("Into the implementation class::"+loginId);


			try{

				List<String> dateRangeList = new LinkedList<String>();
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
				List<String> txnTimeList = null;
				List<HashMap<String, Object>> gensetData = null;
				IstGmtTimeConversion conversionTimeObj = new IstGmtTimeConversion();
				Timestamp gmtTimestamp = null;
				int istHour=0;

				if(period.equalsIgnoreCase("Week")){
					//Fetching last 7 days from today
					for(int i = 1;i<=7;i++){
						cal.add(Calendar.DATE, -1);
						dateRangeList.add(sdfDate.format(cal.getTime()));
					}
				}
				//Yesterday filter
				else if(period.equalsIgnoreCase("yesterday")){
					cal.add(Calendar.DATE, -1);
					dateRangeList.add(sdfDate.format(cal.getTime()));
				}
				
				//daterange filter
				else if(period.equalsIgnoreCase("date")){
					Date stDate = sdfDate.parse(startDate);
					cal.setTime(stDate);
					String cDate = startDate;
					
					dateRangeList.add(startDate);
					while(!cDate.equalsIgnoreCase(endDate)){
						cal.add(Calendar.DATE, 1);
						cDate = sdfDate.format(cal.getTime());
						dateRangeList.add(cDate);
					}
				}

				//Fetching asset details
				AssetEntity asset = new AssetDetailsBO().getAssetEntity(serialNumber);
				GensetTrendsDataImpl currObj = new GensetTrendsDataImpl();
				
				HashMap<String,Object> emptyObj = new HashMap<String, Object>();
				emptyObj.put("L1",null);
				emptyObj.put("L2",null);
				emptyObj.put("L3",null);
				emptyObj.put("ECTemp",null);
				emptyObj.put("KVAR",null);
				emptyObj.put("KW",null);
				emptyObj.put("transTS",null);

				for (String currDate : dateRangeList) {
					txnTimeList = currObj.getMaxTxnTSForDate(serialNumber, asset.getSegmentId(), currDate);
					
					if(txnTimeList != null){
						gensetData = currObj.getTxnDataforTxnTS(serialNumber, asset.getSegmentId(), txnTimeList);
					}
					
					if(gensetData != null && !gensetData.isEmpty()){
						hourWiseData = new HashMap<Integer, HashMap<String,Object>>();
						for(int i = 0;i<gensetData.size();i++){
							
							gmtTimestamp = conversionTimeObj.convertGmtToIst(String.valueOf(gensetData.get(i).get("transTS")));
							if (gmtTimestamp != null) {
								if (gmtTimestamp.toString().length() >= 13) {
									istHour = Integer.parseInt(gmtTimestamp
											.toString().substring(11, 13));
								}
							}
							hourWiseData.put(istHour, gensetData.get(i));
						}
						
						for(int i = 0;i<24;i++){
							if(!hourWiseData.containsKey(i))
								hourWiseData.put(i, emptyObj);
						}
					}
					else
						hourWiseData = null;
					
					response.put(currDate, hourWiseData);
				}

			}catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal(loginId+"::Exception Caught::"+e.getMessage());
			}
		return response;
	}



	public List<String> getMaxTxnTSForDate(String serialNumber, int segmentId,String strCurrDate){

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String startDatetAssetMonTable=null;
		String endDatetAssetMonTable=null;
		List<String> txnTSList = new LinkedList<String>();

		try{
			iLogger.info("GetMaxTxnTSForDate: for START");

			Calendar cal = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date currDateObj = dateFormat.parse(strCurrDate);
			cal.setTime(currDateObj);
			cal.add(Calendar.DAY_OF_YEAR, -1);
			String prevDate = dateFormat.format(cal.getTime());
			prevDate = prevDate + " 18:30:00";
			strCurrDate = strCurrDate + " 18:20:00";
			cal.setTime(dateStr.parse(strCurrDate));

			Timestamp endtxnTimestamp = new Timestamp(cal.getTimeInMillis());
			cal.setTime(dateStr.parse(prevDate));
			Timestamp starttxnTimestamp = new Timestamp(cal.getTimeInMillis());

			//Fetching TAssetMontables for respective timestamps
			startDatetAssetMonTable=new DateUtil().getDynamicTable("GensetTrendsData", starttxnTimestamp);
			endDatetAssetMonTable=new DateUtil().getDynamicTable("GensetTrendsData", endtxnTimestamp);


			//Creating sql connection
			ConnectMySQL connFactory = new ConnectMySQL();
			connection = connFactory.getDatalakeConnection_3309();

			if(!startDatetAssetMonTable.equals(endDatetAssetMonTable)){

				String mainQuery = "SELECT MAX(Transaction_timestamp) as Transaction_Timestamp "+
						"FROM "+endDatetAssetMonTable +" where "+
						"Segment_ID_TxnDate = "+segmentId+" and Serial_Number = '" + serialNumber + "'"+
						" AND Transaction_timestamp <= '" + endtxnTimestamp +
						"' AND Message_ID -> '$.LOG'='1' "+
						" GROUP BY HOUR(convert_tz(Transaction_timestamp,'+00:00','+05:30'))"+ 
						" ORDER BY Transaction_timestamp";

				statement = connection.createStatement();
				rs = statement.executeQuery(mainQuery);

				while(rs.next()){
					if(rs.getTimestamp("Transaction_Timestamp") != null){
						System.out.println(rs.getTimestamp("Transaction_Timestamp"));
						txnTSList.add(rs.getTimestamp("Transaction_Timestamp").toString().substring(0,19));
					}
				}

				mainQuery = "SELECT MAX(Transaction_timestamp) as Transaction_Timestamp "+
						"FROM "+startDatetAssetMonTable +" where "+
						"Segment_ID_TxnDate = "+segmentId+" and Serial_Number = '" + serialNumber + "'"+
						" AND Transaction_timestamp >= '" + starttxnTimestamp +
						"' AND Message_ID -> '$.LOG'='1' "+
						" GROUP BY HOUR(convert_tz(Transaction_timestamp,'+00:00','+05:30'))"+ 
						" ORDER BY Transaction_timestamp";

				statement = connection.createStatement();
				rs = statement.executeQuery(mainQuery);

				while(rs.next()){
					if(rs.getTimestamp("Transaction_Timestamp") != null){
						System.out.println(rs.getTimestamp("Transaction_Timestamp"));
						txnTSList.add(rs.getTimestamp("Transaction_Timestamp").toString().substring(0,19));
					}
				}
			}
			else{

				String mainQuery = "SELECT MAX(Transaction_timestamp) as Transaction_Timestamp "+
						"FROM "+startDatetAssetMonTable +" where "+
						"Segment_ID_TxnDate = "+segmentId+" and Serial_Number = '" + serialNumber + "'"+
						" AND Transaction_timestamp between '"+starttxnTimestamp+"' and '" + endtxnTimestamp +
						"' AND Message_ID -> '$.LOG'='1' "+
						" GROUP BY HOUR(convert_tz(Transaction_timestamp,'+00:00','+05:30'))"+ 
						"ORDER BY Transaction_timestamp";

				statement = connection.createStatement();
				rs = statement.executeQuery(mainQuery);

				while(rs.next()){
					if(rs.getTimestamp("Transaction_Timestamp") != null){
						System.out.println(rs.getTimestamp("Transaction_Timestamp"));
						txnTSList.add(rs.getTimestamp("Transaction_Timestamp").toString().substring(0,19));
					}
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			fLogger.fatal("GensetTrendsData::Exception Caught::"+ex.getMessage());
		}
		finally{
			try{
				if(rs != null)
					rs.close();

			}catch (SQLException e) {
				fLogger.fatal("GetMaxTxnTSForDate exception "+e.getMessage());
				e.printStackTrace();
			}
			try {
				if(statement != null && !statement.isClosed()){
					statement.close();
				}

			} catch (SQLException e) {
				fLogger.fatal("GetMaxTxnTSForDate exception "+e.getMessage());
				e.printStackTrace();
			}
			try {
				if(connection!= null && !connection.isClosed()){
					connection.close();
				}
			} catch (SQLException e) {
				fLogger.fatal("GetMaxTxnTSForDate exception "+e.getMessage());
				e.printStackTrace();
			}
		}

		return txnTSList;
	}

	public List<HashMap<String, Object>> getTxnDataforTxnTS(String serialNumber, int segmentId, List<String> TxnTSList){

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String tAssetMonTable=null;
		String amhSelectQuery = "";
		String amhFromQuery = "";
		String amhWhereQuery = "";
		String amhGroupNyQuery = "";
		String amhOrderByQuery = "";
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		DateFormat dateStr = null;
		Properties prop = null;
		HashMap<String, List<String>> tAssetMonTableData = new HashMap<String, List<String>>(); 
		List<HashMap<String,Object>> gensetData=new LinkedList<HashMap<String, Object>>();

		try {
			dateStr = new SimpleDateFormat("yyyy-MM-dd");
			prop = CommonUtil.getDepEnvProperties();
			tAssetMonTable = prop.getProperty("default_TAssetMonData_Table");

		}catch(IllegalArgumentException iag){
			fLogger.fatal("getTxnDataforTxnTS Exception in formatting the Date "+iag.getMessage());
		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("getTxnDataforTxnTS generic Exception:"+e.getMessage());
		}

		DateUtil dateUtilObj = new DateUtil();
		String tAssetTable = null;
		List<String> txnTimeList = null;
		for(int i = 0;i<TxnTSList.size();i++){
			String txn = (String)TxnTSList.get(i);
			try {
				Date dt = dateStr.parse(txn);
				tAssetTable = dateUtilObj.getDynamicTable("GensetTrendsData", dt);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if(tAssetMonTableData.containsKey(tAssetTable))
				txnTimeList = tAssetMonTableData.get(tAssetTable);
			else
				txnTimeList = new LinkedList<String>();
			txnTimeList.add(TxnTSList.get(i));
			tAssetMonTableData.put(tAssetTable, txnTimeList);
		}
		
		try{
			iLogger.info("getTxnDataforTxnTS Query starts ");

			connection = connFactory.getDatalakeConnection_3309();
			statement = connection.createStatement();

			for(String table:tAssetMonTableData.keySet()){
				tAssetMonTable = table;
				txnTimeList = tAssetMonTableData.get(tAssetMonTable);
				String TxnTSListString = new ListToStringConversion().getStringList(txnTimeList).toString();

				/*amhSelectQuery = "select Transaction_Timestamp," +
						"JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEA') as L1," +
						"JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEB') as L2," +
						"JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEC') as L3," +
						"JSON_EXTRACT(TxnData,'$.FINISH_KVA_HOURS') as FKVA," +
						"JSON_EXTRACT(TxnData,'$.START_KVA_HOURS') as SKVA," +
						"JSON_EXTRACT(TxnData,'$.FINISH_KW_HOURS') as FKW," +
						"JSON_EXTRACT(TxnData,'$.START_KW_HOURS') as SKW," +
						"JSON_EXTRACT(TxnData,'$.Engine_Coolant_temp') as ECTemp";*/
				
				//DF20181218- KO369761 - Genset data fetching parameters modified.
				amhSelectQuery = "select Transaction_Timestamp," +
						"JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEA') as L1," +
						"JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEB') as L2," +
						"JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEC') as L3," +
						"JSON_EXTRACT(TxnData,'$.KVA_HOURS') as KVA," +
						"JSON_EXTRACT(TxnData,'$.KW_HOURS') as KW," +
						"JSON_EXTRACT(TxnData,'$.Engine_Coolant_temp') as ECTemp";
				
				amhFromQuery = " from "+tAssetMonTable;
				amhWhereQuery = " where Segment_ID_TxnDate = "+segmentId +" and Serial_Number = '"+serialNumber+"' and " +
						" Transaction_Timestamp in  ("+TxnTSListString+") ";
				amhOrderByQuery = " order by Transaction_Timestamp";

				String mainQuery = amhSelectQuery+amhFromQuery+amhWhereQuery+amhGroupNyQuery+amhOrderByQuery;
				iLogger.info("getTxnDataforTxnTS mainQuery "+mainQuery);

				rs = statement.executeQuery(mainQuery);
				HashMap<String,Object> paramsData = null;
				Double FKVA,SKVA,FKW,SKW;
				while(rs.next()){
					FKVA = null;
					SKVA = null;
					FKW = null;
					SKW = null;
					paramsData = new HashMap<String,Object>();
					paramsData.put("transTS",rs.getTimestamp("Transaction_Timestamp").toString().substring(0,19));
					
					if(rs.getString("L1") != null)
						paramsData.put("L1",Double.parseDouble(rs.getString("L1").replaceAll("\"", "")));
					else
						paramsData.put("L1",null);

					if(rs.getString("L2") != null)
						paramsData.put("L2",Double.parseDouble(rs.getString("L2").replaceAll("\"", "")));
					else
						paramsData.put("L2",null);
					
					if(rs.getString("L3") != null)
						paramsData.put("L3",Double.parseDouble(rs.getString("L3").replaceAll("\"", "")));
					else
						paramsData.put("L3",null);
					
					if(rs.getString("ECTemp") != null)
						paramsData.put("ECTemp",Double.parseDouble(rs.getString("ECTemp").replaceAll("\"", "")));
					else
						paramsData.put("ECTemp",null);
					
					/*if(rs.getString("FKVA") != null)
						FKVA = Double.parseDouble(rs.getString("FKVA").replaceAll("\"", ""));
					
					if(rs.getString("SKVA") != null)
						SKVA = Double.parseDouble(rs.getString("SKVA").replaceAll("\"", ""));
					
					if(rs.getString("FKW") != null)
						FKW = Double.parseDouble(rs.getString("FKW").replaceAll("\"", ""));
					
					if(rs.getString("SKW") != null)
						SKW = Double.parseDouble(rs.getString("SKW").replaceAll("\"", ""));
					
					if(FKVA != null && SKVA != null)
						paramsData.put("KVAR",(FKVA - SKVA));
					else
						paramsData.put("KVAR",null);
					
					if(FKW != null && SKW != null)
						paramsData.put("KW",(FKW - SKW));
					else
						paramsData.put("KW",null);*/
					
					//DF20181218- KO369761 - Genset data fetching parameters modified.
					if(rs.getString("KVA") != null)
						paramsData.put("KVAR",Double.parseDouble(rs.getString("KVA").replaceAll("\"", "")));
					else
						paramsData.put("KVAR",null);
					
					if(rs.getString("KW") != null)
						paramsData.put("KW",Double.parseDouble(rs.getString("KW").replaceAll("\"", "")));
					else
						paramsData.put("KW",null);

					gensetData.add(paramsData);

				}
			}


		}catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception Caught ::"+e.getMessage());
		}

		finally{
			try{
				if(rs != null)
					rs.close();

			}catch (SQLException e) {
				fLogger.fatal("getTxnDataforTxnTS exception "+e.getMessage());
				e.printStackTrace();
			}
			try {
				if(statement != null && !statement.isClosed()){
					statement.close();

				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				fLogger.fatal("getTxnDataforTxnTS exception "+e.getMessage());
				e.printStackTrace();
			}
			try {
				if(connection!= null && !connection.isClosed()){
					connection.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				fLogger.fatal("getTxnDataforTxnTS exception "+e.getMessage());
				e.printStackTrace();
			}
		}

		return gensetData;
	}
}
