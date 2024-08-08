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
import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;

public class SootMonitorServiceImpl {
    Logger iLogger = InfoLoggerClass.logger;
    Logger fLogger = FatalLoggerClass.logger;

    @SuppressWarnings("resource")
    //public List<HashMap<String, String>> getSootMonitorReport(String vin, String date) {//CR471.o
    public List<HashMap<String, String>> getSootMonitorReport(String vin, String date, String machineGroupIdList) {//CR471.n

	List<HashMap<String, String>> mapList = new ArrayList<>();
	ConnectMySQL connectionObj = new ConnectMySQL();
	//CR471.sn
	if(machineGroupIdList!=null && !machineGroupIdList.equalsIgnoreCase("null")) {
	    //Validate if vin present in machineGroupIdList
	    ArrayList<String> groupIdList = new ArrayList<>(); 
	    String query = "select b.group_id from asset a, custom_asset_group_member b where a.serial_number=b.serial_number and b.group_id in ("+machineGroupIdList+")";
	    iLogger.info("query : " +query);
	    try(Connection con = connectionObj.getConnection();
		    Statement st = con.createStatement();
		    ResultSet rs = st.executeQuery(query)){
		while(rs.next()) {
		    groupIdList.add(rs.getString("group_id"));
		}
	    }catch (Exception e) {
		fLogger.fatal("Exception occurred",e);
		return mapList;
	    }
	    if (groupIdList.isEmpty()) {
		iLogger.info("Machine not tagged to machine groups : " + machineGroupIdList);
		return mapList; 
	    }
	}
	//CR471.en
	String TAssetMonQuery = null;
	String startTAssetMonQuery = null;
	String endTAssetMonQuery = null;
	Connection con = null;
	Statement statement = null;
	ResultSet resultSet = null;
	

	try {

	    DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd");

	    Date TxnTS = null;
	    try {
		TxnTS = dateStr.parse(date);
	    } catch (ParseException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	    }

	    String endTS = date + " 18:30:00";

	    String startTAssetMonTable = null;
	    String endTAssetMonTable = null;

	    String dynamicTable = new DateUtil().getDynamicTable("SootMonitor", TxnTS);

	    if (dynamicTable != null) {
		endTAssetMonTable = dynamicTable;

	    }

	    Calendar cal = Calendar.getInstance();
	    cal.setTime(TxnTS);
	    cal.add(Calendar.DAY_OF_YEAR, -1);

	    String startTS = dateStr.format(cal.getTime());
	    // data will be take from previous day 6:30 to today 6:30
	    startTS = startTS + " 18:30:00";

	    Timestamp starttxnTimestamp = new Timestamp(cal.getTimeInMillis());

	    String startDynamicTable = new DateUtil().getDynamicTable("SootMonitor", starttxnTimestamp);

	    if (startDynamicTable != null) {
		startTAssetMonTable = startDynamicTable;
	    } else {
		startTAssetMonTable = endTAssetMonTable;
	    }

	    if (startTAssetMonTable.equals(endTAssetMonTable)) {

		TAssetMonQuery = "select serial_number, JSON_UNQUOTE(json_extract(TxnData,'$.CMH')) as HMR, CONVERT_TZ(Transaction_timestamp,'+00:00','+05:30') as Transaction_timestamp,"
			+" JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.LOG_SootMon')) as LOG_SootMon from "+ startTAssetMonTable
			+" where serial_number ='"+vin+"' and Transaction_Timestamp >= '" + startTS +"' and Transaction_Timestamp <='"+ endTS+"'"
			+" and JSON_EXTRACT(TxnData,'$.MSG_ID') = '068'";

	    } else {

		startTAssetMonQuery = "select serial_number, JSON_UNQUOTE(json_extract(TxnData,'$.CMH')) as HMR, CONVERT_TZ(Transaction_timestamp,'+00:00','+05:30') as Transaction_timestamp,"
			+" JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.LOG_SootMon')) as LOG_SootMon from "+ startTAssetMonTable
			+" where serial_number ='"+vin+"' and Transaction_Timestamp >= '" + startTS +"' and Transaction_Timestamp <='"+ endTS+"'"
			+" and JSON_EXTRACT(TxnData,'$.MSG_ID') = '068'";

		endTAssetMonQuery = "select serial_number, JSON_UNQUOTE(json_extract(TxnData,'$.CMH')) as HMR, CONVERT_TZ(Transaction_timestamp,'+00:00','+05:30') as Transaction_timestamp,"
			+" JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.LOG_SootMon')) as LOG_SootMon from "+ endTAssetMonTable
			+" where serial_number ='"+vin+"' and Transaction_Timestamp >= '" + startTS +"' and Transaction_Timestamp <='"+ endTS+"'"
			+" and JSON_EXTRACT(TxnData,'$.MSG_ID') = '068'";

	    }

	    iLogger.info("getSootMonitorReport TAssetMonQuery : "+TAssetMonQuery);
	    iLogger.info("getSootMonitorReport startTAssetMonQuery : "+startTAssetMonQuery);
	    iLogger.info("getSootMonitorReport endTAssetMonQuery : "+endTAssetMonQuery);

	  
	    con = connectionObj.getDatalakeConnection_3309();
	    statement = con.createStatement();
	    HashMap<String, String> tableMap = null;
	    String txnTs = null;
	    String hmr = null;
	    String sootMonitor = null;
	    if(TAssetMonQuery !=null) {
		resultSet = statement.executeQuery(TAssetMonQuery);
		while(resultSet.next())
		{

		    tableMap = new HashMap<String,String>();
		    tableMap.put("vin",resultSet.getString("serial_number"));
		    txnTs = resultSet.getString("Transaction_timestamp").substring(0, 19);

		    if(txnTs !=null) {
			tableMap.put("txnTs",txnTs);
		    }else {
			tableMap.put("txnTs","NA");
		    }

		    hmr = resultSet.getString("HMR");
		    if(hmr !=null) {
			tableMap.put("hmr",hmr);
		    }else {
			tableMap.put("hmr","NA");
		    }

		    sootMonitor = resultSet.getString("LOG_SootMon");
		    if(sootMonitor !=null) {
			tableMap.put("sootMonitor",sootMonitor);
		    }else {
			tableMap.put("sootMonitor","NA");
		    }


		    mapList.add(tableMap);
		}
	    }else {
		resultSet = statement.executeQuery(startTAssetMonQuery);
		while(resultSet.next())
		{

		    tableMap = new HashMap<String,String>();
		    tableMap.put("vin",resultSet.getString("serial_number"));
		    txnTs = resultSet.getString("Transaction_timestamp");

		    if(txnTs !=null) {
			tableMap.put("txnTs",txnTs);
		    }else {
			tableMap.put("txnTs","NA");
		    }

		    hmr = resultSet.getString("HMR");
		    if(hmr !=null) {
			tableMap.put("hmr",hmr);
		    }else {
			tableMap.put("hmr","NA");
		    }

		    sootMonitor = resultSet.getString("LOG_SootMon");
		    if(sootMonitor !=null) {
			tableMap.put("sootMonitor",sootMonitor);
		    }else {
			tableMap.put("sootMonitor","NA");
		    }


		    mapList.add(tableMap);
		}

		resultSet = statement.executeQuery(endTAssetMonQuery);
		while(resultSet.next())
		{

		    tableMap = new HashMap<String,String>();
		    tableMap.put("vin",resultSet.getString("serial_number"));
		    txnTs = resultSet.getString("Transaction_timestamp");

		    if(txnTs !=null) {
			tableMap.put("txnTs",txnTs);
		    }else {
			tableMap.put("txnTs","NA");
		    }

		    hmr = resultSet.getString("HMR");
		    if(hmr !=null) {
			tableMap.put("hmr",hmr);
		    }else {
			tableMap.put("hmr","NA");
		    }

		    sootMonitor = resultSet.getString("LOG_SootMon");
		    if(sootMonitor !=null) {
			tableMap.put("sootMonitor",sootMonitor);
		    }else {
			tableMap.put("sootMonitor","NA");
		    }


		    mapList.add(tableMap);
		}

	    }

	} catch (Exception ex) {

	    ex.printStackTrace();
	    fLogger.fatal("Exception occurred while fetching SootMonitor details in SootMonitorServiceImpl: "
		    + ex.getMessage());

	} finally {
	    if (resultSet != null)
		try {
		    resultSet.close();
		} catch (SQLException e1) {
		    e1.printStackTrace();
		}

	    if (statement != null)
		try {
		    statement.close();
		} catch (SQLException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}

	    if (con != null) {
		try {
		    con.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	}

	return mapList;
    }

}
