package remote.wise.util;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class ThreadForAlertMigration implements Callable<String>{
	String time_key;
	public ThreadForAlertMigration(String time_key){
		this.time_key = time_key;
		Thread.currentThread().setName(this.time_key);
	}
	@Override
	public String call() throws Exception {
		// TODO Auto-generated method stub
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Alert Summary Migration "+Thread.currentThread().getName()+" is running...");
		String noOfRecords = getInsightAlertDayAgg(time_key);
		return noOfRecords;
	}
	public String getInsightAlertDayAgg(String timeKey){
		
		Logger iLogger = InfoLoggerClass.logger;
		String result = timeKey;
		Statement statement = null;
		Statement localStatement = null;
		ResultSet rs = null;
		Connection db2Connection = null;
		PreparedStatement pst= null;
		PreparedStatement pst1= null;
		Statement db2Statement = null;
		int row = 1;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
		Calendar cal = Calendar.getInstance();
		String createdTimeStamp = sdf.format(cal.getTime());
		List<String> faultStatements = new LinkedList<String>();
		String alertSummaryQuery = "select *,count(*) as AlertCount from alertInsight_detail where TxnDate = '"+timeKey+"' " +
				"group by AssetID,AlertCode";
		long queryStartTime = System.currentTimeMillis();
		db2Connection = new ConnectMySQL().getProdDb2Connection();
		
		try {
			db2Connection.setAutoCommit(false);
			db2Statement = db2Connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			
			rs= db2Statement.executeQuery(alertSummaryQuery);
			
			long queryEndTime = System.currentTimeMillis();
			if(rs != null){
				rs.last();
				int rowCount = rs.getRow();
				rs.beforeFirst();
			//System.out.println("total records for thread "+timeKey+" are:"+rowCount);
				iLogger.info("Alert Detail Migration  total records for thread "+timeKey+" are:"+rowCount+" exceuted in "+(queryEndTime - queryStartTime));
				System.out.println("Alert Detail Migration  total records for thread "+timeKey+" are:"+rowCount+" exceuted in "+(queryEndTime - queryStartTime));
			}
			String insertStatement = "  insert into alertInsight_dayAgg(AssetID,TxnDate,TxnWeek,TxnMonth,TxnQuarter,TxnYear,"+
					"ModelCode,ModelName,ProfileCode,ProfileName,ZonalCode,ZonalName,DealerCode,DealerName,CustCode,CustomerName,DealerContact,CustContact,PrimaryOwner," +
					"Latitude,Longitude,Address,StateId,State,CityId,City,Country," +
					"FWVersion,AlertTypeCode,AlertType,AlertCode,Alert,AlertSeverity,AlertCount,PartitionID,MachineNum,LastUpdatedTime) "+
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			if(db2Connection.isClosed())
				db2Connection = new ConnectMySQL().getProdDb2Connection();
			pst = db2Connection.prepareStatement(insertStatement);
			
			while(rs.next()){
				pst.setString(1, rs.getString("AssetID"));
				pst.setDate(2, rs.getDate("TxnDate"));
				pst.setInt(3, rs.getInt("TxnWeek"));
				pst.setInt(4, rs.getInt("TxnMonth"));
				pst.setInt(5, rs.getInt("TxnQuarter"));
				pst.setInt(6, rs.getInt("TxnYear"));
				pst.setString(7, rs.getString("ModelCode"));
				//stmtValues += asset_type+",";
				pst.setString(8, rs.getString("ModelName"));
				//stmtValues += "'"+type_name+"',";
				pst.setString(9, rs.getString("ProfileCode"));
				//stmtValues += group_id+",";
				pst.setString(10,rs.getString("ProfileName"));
				//stmtValues += "'"+group_name+"',";
				
					pst.setString(11, rs.getString("ZonalCode"));
					//stmtValues += dealer_code+",";
					pst.setString(12, rs.getString("ZonalName"));
					//stmtValues += "'"+dealer_name+"',";
					pst.setString(13, rs.getString("DealerCode"));
					//stmtValues += customer_code+",";
					pst.setString(14, rs.getString("DealerName"));
				//	stmtValues += "'"+customer_name+"',";
					pst.setString(15, rs.getString("CustCode"));
				//	stmtValues += customer_code+",";
					pst.setString(16, rs.getString("CustomerName"));
				//	stmtValues += "'"+customer_name+"',";
					pst.setString(17, rs.getString("DealerContact"));
					//stmtValues += "'"+customer_number+"',";
					pst.setString(18, rs.getString("CustContact"));
				
				//stmtValues += "'"+customer_number+"',";
				
				pst.setString(19, rs.getString("PrimaryOwner"));
				//stmtValues += primary_owner_code+",";
				
				
				pst.setString(20, rs.getString("Latitude"));
				//stmtValues += latitude+",";
				pst.setString(21, rs.getString("Longitude"));
				//stmtValues += longitude+",";
				pst.setString(22,rs.getString("Address"));
				//stmtValues += "'"+address+"',";
				pst.setInt(23,rs.getInt("StateId"));
				//stmtValues += cityId+",";
				pst.setString(24,rs.getString("State"));
				//stmtValues += "'"+city+"',";
				pst.setInt(25,rs.getInt("CityId"));
				//stmtValues += stateId+",";
				pst.setString(26,rs.getString("City"));
				//stmtValues += "'"+state+"',";
				pst.setString(27,rs.getString("Country"));
				//stmtValues += "'"+country+"',";
				
				pst.setString(28, rs.getString("FWVersion"));
				pst.setInt(29, rs.getInt("AlertTypeCode"));
				
				pst.setString(30, rs.getString("AlertType"));
				pst.setInt(31,rs.getInt("AlertCode"));
				
				pst.setString(32,rs.getString("Alert"));
				
				
				pst.setString(33, rs.getString("AlertSeverity"));
				
				pst.setInt(34, rs.getInt("AlertCount"));
				
				
				pst.setInt(35, rs.getInt("PartitionID"));
				//stmtValues += ","+partition_ID;
				pst.setString(36, rs.getString("MachineNum"));
				pst.setString(37,createdTimeStamp);
				faultStatements.add( rs.getString("AssetID"));
				pst.addBatch();
				if(row%1000 == 0)
				{
					//System.out.println(timeKey+"roww:"+row);
					long iteratorEnd = System.currentTimeMillis();
					//iLogger.info("Alert Detail Migration "+timeKey+" 1000 iterations executed in: "+(iteratorEnd-iteratorStart));
					
					try{
					iLogger.info("Alert Detail Migration "+timeKey+"before executingbatch insertions for 1000 records");
					long preparedStmtStart = System.currentTimeMillis();
					pst.executeBatch();
					if(db2Connection.isClosed()){
						db2Connection = new ConnectMySQL().getProdDb2Connection();
						}
					db2Connection.commit();

					pst.clearBatch();
					long preparedStmtEnd = System.currentTimeMillis();
					iLogger.info("Alert Detail Migration "+timeKey+" executing batch insertions for 1000 records in "+(preparedStmtEnd-preparedStmtStart));
					}catch(BatchUpdateException bue){
						iLogger.info("Alert Detail Migration  inner BatchUpdateexception");
						bue.printStackTrace();
						int[] updatedCount = bue.getUpdateCounts();
						String faultInsertStmt = "insert into alert_fault_statements(serial_number,time_key,fault_details) values(?,?,?)";
						
						//PreparedStatement pst1;
						try {
							pst1 = db2Connection.prepareStatement(faultInsertStmt);
						
							if(updatedCount.length>0){
						for(int i=0;i<updatedCount.length;i++)
						{
							if(updatedCount[i]<0){
								int j=i;
								//iLogger.info("Exception statement :"+faultStatements.get(j));
								//iLogger.info("Exception next statement :"+faultStatements.get(j+1));
								pst1.setString(1, faultStatements.get(j));
								pst1.setString(2, timeKey);
								pst1.setString(3, bue.getMessage());
								pst1.addBatch();
								
							}
						}
						pst1.executeBatch();
						if(db2Connection.isClosed()){
							db2Connection = new ConnectMySQL().getProdDb2Connection();
							}
						db2Connection.commit();
							pst1.clearBatch();
							}
							else{
			String faultInsertStmt1 = "insert into alert_fault_statements(serial_number,time_key,fault_details) values('Query Exception','"+timeKey+"','"+bue.getMessage()+"')";
								
								try {
									//ConnectMySQL connMySql = new ConnectMySQL();
									//devConnection = connMySql.getConnection();
									localStatement = db2Connection.createStatement();
									localStatement.executeUpdate(faultInsertStmt);
									if(db2Connection.isClosed()){
										db2Connection = new ConnectMySQL().getProdDb2Connection();
										}
									db2Connection.commit();

								} catch (SQLException se) {
									// TODO Auto-generated catch block
									se.printStackTrace();
								} 
								bue.printStackTrace();
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						
					}
				}
					
				row++;
		}
		iLogger.info("Alert Detail Migration "+timeKey+"after executing while loop for query");
		int[] updateVins = pst.executeBatch();
		if(db2Connection.isClosed()){
			db2Connection = new ConnectMySQL().getProdDb2Connection();
			}
		db2Connection.commit();
		pst.clearBatch();
		iLogger.info("Alert Detail Migration "+timeKey+"after executing batch insertions");
		result += ": "+row;
}catch(BatchUpdateException bue){
			iLogger.info("Alert Detail Migration outer BatchUpdateexception");
			bue.printStackTrace();
			int[] updatedCount = bue.getUpdateCounts();
			String faultInsertStmt = "insert into alert_fault_statements(serial_number,time_key,fault_details) values(?,?,?)";
			
			//PreparedStatement pst1;
			try {
				pst1 = db2Connection.prepareStatement(faultInsertStmt);
			
				if(updatedCount.length>0){
			for(int i=0;i<updatedCount.length;i++)
			{
				if(updatedCount[i]<0){
					int j=i;
					//iLogger.info("Exception statement :"+faultStatements.get(j));
					//iLogger.info("Exception next statement :"+faultStatements.get(j+1));
					pst1.setString(1, faultStatements.get(j));
					pst1.setString(2, timeKey);
					pst1.setString(3, bue.getMessage());
					pst1.addBatch();
					
				}
			}
			pst1.executeBatch();
			if(db2Connection.isClosed()){
				db2Connection = new ConnectMySQL().getProdDb2Connection();
				}
			db2Connection.commit();
			pst1.clearBatch();
				}
				else{
String faultInsertStmt1 = "insert into alert_fault_statements(serial_number,time_key,fault_details) values('Query Exception','"+timeKey+"','"+bue.getMessage()+"')";
					
					try {
						//ConnectMySQL connMySql = new ConnectMySQL();
						//devConnection = connMySql.getConnection();
						localStatement = db2Connection.createStatement();
						localStatement.executeUpdate(faultInsertStmt);
						if(db2Connection.isClosed()){
							db2Connection = new ConnectMySQL().getProdDb2Connection();
							}
						db2Connection.commit();

					} catch (SQLException se) {
						// TODO Auto-generated catch block
						se.printStackTrace();
					} 
					bue.printStackTrace();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		catch (SQLException e) {
			
			// TODO Auto-generated catch block
			
			//cursor_position = row+1;
			iLogger.info("Alert Detail Migration SQL Exception ------");
			iLogger.info(e);
			//e.printStackTrace();
		} 
		catch(Exception e)
		{
			iLogger.info("Alert Detail Migration Normal Exception ------");
			iLogger.info(e);
String faultInsertStmt = "insert into alert_fault_statements(serial_number,time_key,fault_details) values('normal exception','"+timeKey+"','"+e.getMessage()+"')";
			
			
			try {
				//ConnectMySQL connMySql = new ConnectMySQL();
				//devConnection = connMySql.getConnection();
				localStatement = db2Connection.createStatement();
				localStatement.executeUpdate(faultInsertStmt);
				if(db2Connection.isClosed()){
					db2Connection = new ConnectMySQL().getProdDb2Connection();
					}
				db2Connection.commit();

			} catch (SQLException se) {
				// TODO Auto-generated catch block
				se.printStackTrace();
			} 
			
			e.printStackTrace();
		}
		finally{
			 try { if (rs != null) rs.close(); } catch (Exception e) {};
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
		}

		
		
		
		return result;
		
	}
	/*public String getInsightAlertDayAgg(String timeKey) {
		// TODO Auto-generated method stub
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String result = timeKey;
		Connection prodConnection = null;
		Statement statement = null;
		Statement localStatement = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		//iLogger.info(Thread.currentThread().getName()+" is executing day agg...");
		long threadStart = System.currentTimeMillis();
		final String country = "INDIA";
		int cityId = 0;
		int stateId = 0;
		String address = null;
		String city = null;
		String state = null;
		int cursor_position = 0;
		Connection db2Connection = null;
		int row=1;
		final int partition_ID =1;
		double latitude = 0;
		double longitude = 0;
		String machineNumber = null;
		List<String> faultStatements = new LinkedList<String>();
		PreparedStatement pst= null;
		PreparedStatement prodDBPST= null;
		PreparedStatement pst1= null;
		Statement db2Statement = null;
		HashMap<String,Integer> statesMap = new HashMap<String, Integer>();
		HashMap<String,Integer> citiesMap = new HashMap<String, Integer>();
		String ac_type = null;
		String fwVersion = null;
		try {
			//Date time_Key = sdf.parse(timeKey);
			iLogger.info(timeKey+"Alert Summary Migration  is executing day agg... with time_key "+timeKey);
			//ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
			iLogger.info("Alert Summary Migration getting proddb2 connection");
				db2Connection = new ConnectMySQL().getProdDb2Connection();
				iLogger.info("Alert Summary Migration connected to proddb2 connection");
				
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
				//Connection connection = new ConnectMySQL().getProdDb2Connection();
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
				
				long queryStartTime = System.currentTimeMillis();
				
				String migrationQuery = "SELECT "+
	"aa.*,bb.Machine_Number,bb.asset_type_code,bb.type_name,bb.group_code,bb.group_name "+
"FROM ( "+
	"SELECT "+ 
		"ae.Serial_Number,'"+timeKey+"' as timeKey, "+
		"Week('"+timeKey+"') as week_no, "+
		"MONTH('"+timeKey+"') as month, "+
		"QUARTER('"+timeKey+"') as quarter, "+
		"YEAR('"+timeKey+"') as year,ac.Account_Code as customerCode,ac.account_name as CustomerName,pa.Account_Code as parentCode,pa.account_name as DealerName,za.Account_Code as ZonalCode,za.account_name as ZonalName,ac.Account_Code as  PrimaryOwner, "+
		"count(ae.Event_ID) as alertCount, " +
		"ac.Mobile as Customer_Number,pa.Mobile as Dealer_Number,"+
		"ae.Event_ID as alertCode, "+
		"be.Event_Name as alert, "+
		"ae.Event_Type_ID as alertTypeCode,et.Event_Type_Name as alertTypeName,ae.Event_Severity as Severity,ae.Event_Generated_Time as TxnDate,ao.Ownership_Start_Date,ae.Address,ae.State,ae.City "+ 
	"FROM "+ 
		"asset_owners ao, "+
		"account ac, "+
		"asset_event ae, "+ 
		"event_type et, "+
		"business_event be, "+
		"account pa left outer join account za on za.account_id = pa.Parent_ID "+ 
	"WHERE "+ 
		"ae.Event_Generated_Time like '"+timeKey+"%' and "+ 
		"ae.Serial_Number is not null and "+ 
		"ao.Serial_Number = ae.Serial_Number and "+ 
		"ao.Ownership_Start_Date= ( "+
			"SELECT "+ 
				"max(ao1.Ownership_Start_Date) "+ 
			"FROM "+ 
				"asset_owners ao1 "+ 
			"WHERE "+
				"ao1.Serial_Number=ae.Serial_Number and "+ 
				"ao1.Ownership_Start_Date<='"+timeKey+"') and "+ 
		"ac.account_id = ao.Account_ID and "+ 
		"pa.Account_ID = ac.Parent_ID and "+ 
		"ae.Event_ID = be.Event_ID and "+ 
		"ae.Event_Type_ID = et.Event_Type_ID "+ 
	"GROUP BY ae.Serial_Number,ae.Event_Type_ID,ae.Event_ID "+ 
	"ORDER BY ae.Serial_Number,ae.Event_Type_ID "+
	") as aa "+ 
"LEFT OUTER JOIN ( "+
	"SELECT "+ 
		"a.Serial_Number,a.Machine_Number, "+
		"a_t.Asset_Type_Code as asset_type_code, "+
		"a_t.Asset_Type_Name as type_name, "+
		"agp.asset_grp_code as group_code, "+
		"ag.Asseet_Group_Name as group_name "+ 
	"FROM "+ 
		"asset a,products p, "+
		"asset_type a_t,asset_group ag,asset_group_profile agp "+
	"WHERE "+ 
	"p.Product_ID = a.Product_ID and "+ 
	"ag.Asset_Group_ID = p.Asset_Group_ID and "+ 
	"agp.asset_grp_id = p.Asset_Group_ID and "+ 
	"a_t.Asset_Type_ID = p.Asset_Type_ID "+
	") as bb "+ 
"ON "+ 
	"aa.Serial_Number = bb.Serial_number ";
				//System.out.println("Query successfully executed");
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
				iLogger.info("Alert Summary Migration total records for thread "+timeKey+" are:"+rowCount+" exceuted in "+(queryEndTime - queryStartTime));

				System.out.println("total records for thread "+timeKey+" are:"+rowCount+" exceuted in "+(queryEndTime - queryStartTime));
				String insertStatement = "  insert into alertInsight_dayAgg(AssetID,TxnDate,TxnWeek,TxnMonth,TxnQuarter,TxnYear,"+
						"ModelCode,ModelName,ProfileCode,ProfileName,ZonalCode,ZonalName,DealerCode,DealerName,CustCode,CustomerName,DealerContact,CustContact,PrimaryOwner," +
						"Latitude,Longitude,Address,StateId,State,CityId,City,Country," +
						"FWVersion,AlertTypeCode,AlertType,AlertCode,Alert,AlertSeverity,AlertCount,PartitionID,MachineNum) "+
						"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				long endQueryTime = System.currentTimeMillis();
				
				
				
				//String insertQuery = insertStatement + values;
				iLogger.info(timeKey+"Alert Summary Migration  thread execution time for query"+((int)(((endQueryTime-queryStartTime)/1000)%60))+"");
				//System.out.println(timeKey+" thread execution time for query"+((int)(((endQueryTime-queryStartTime)/1000)%60))+"");
				if(db2Connection.isClosed())
					db2Connection = new ConnectMySQL().getProdDb2Connection();
				pst = db2Connection.prepareStatement(insertStatement);
				db2Statement = db2Connection.createStatement();
				String latLongQuery = "SELECT amh.FW_Version_Number,amd.Parameter_ID,amd.Parameter_Value " +
						"FROM asset_monitoring_header amh," +
						"asset_monitoring_detail_data amd " +
						"WHERE amh.Serial_Number=? and " +
						"amh.Transaction_Timestamp=(" +
							"SELECT max(ae.Event_Generated_Time) " +
							"FROM asset_event ae " +
							"WHERE ae.Serial_Number = amh.Serial_Number and " +
							"ae.Event_Generated_Time>? and " +
							"ae.Event_Generated_Time<=? and " +
							"Event_ID = ? and Event_Type_ID = ?) and amd.Transaction_Number = amh.Transaction_Number and amd.Parameter_ID in (1,2)";
				
				iLogger.info(timeKey+" Alert Summary Migration before executing while loop for query");
				//prodDBPST = prodConnection.prepareStatement(latLongQuery);
				//System.out.println(timeKey+"before executing while loop for query");
				long iteratorStart = System.currentTimeMillis();
				String minTimeKey = timeKey+" 00:00:00";
				String maxTimeKey = timeKey+" 23:59:59";
				while(rs.next()){
					try{
					pst.setString(1, rs.getString("Serial_Number"));
					
					//prodDBPST.setString(1, rs.getString("Serial_Number"));//binding dynamic variables to the lat long query serialNumber
					//prodDBPST.setString(2, minTimeKey);//binding dynamic variables to the lat long query min Event_Generated_Time
					//prodDBPST.setString(3, maxTimeKey);//binding dynamic variables to the lat long query max Event_Generated_Time
					
					//binding the varibles for the lat long query 
					//	stmtValues += "'"+Serial_Number+"',";
						pst.setDate(2, new java.sql.Date(sdf.parse(timeKey).getTime()));
						//stmtValues += "'"+new java.sql.Date(sdf.parse(timeKey).getTime())+"',";
						
						pst.setInt(3, rs.getInt("week_no"));
						//stmtValues += week+",";
						pst.setInt(4, rs.getInt("month"));
						//stmtValues += month+",";
						pst.setInt(5, rs.getInt("quarter"));
						//stmtValues += quarter+",";
						pst.setInt(6, rs.getInt("year"));
						//stmtValues += year+",";
						

						//ac_type = rs.getString("ac_type");
						
						pst.setString(7, rs.getString("asset_type_code"));
						//stmtValues += asset_type+",";
						pst.setString(8, rs.getString("type_name"));
						//stmtValues += "'"+type_name+"',";
						pst.setString(9, rs.getString("group_code"));
						//stmtValues += group_id+",";
						pst.setString(10,rs.getString("group_name"));
						//stmtValues += "'"+group_name+"',";
						if(rs.getString("ZonalCode")!=null){
							pst.setString(11, rs.getString("ZonalCode"));
							//stmtValues += zonal_code+",";
							pst.setString(12, rs.getString("ZonalName"));
							//stmtValues += "'"+zonal_name+"',";
							pst.setString(13, rs.getString("parentCode"));
							//stmtValues += dealer_code+",";
							pst.setString(14, rs.getString("DealerName"));
							//stmtValues += "'"+dealer_name+"',";
							pst.setString(15, rs.getString("customerCode"));
							//stmtValues += customer_code+",";
							pst.setString(16, rs.getString("CustomerName"));
							//stmtValues += "'"+customer_name+"',";
							pst.setString(17, rs.getString("Dealer_Number"));
							//stmtValues += "'"+dealer_number+"',";
							pst.setString(18, rs.getString("Customer_Number"));
						}
						else if(rs.getString("CustomerName").equals("JCB_India")){
							pst.setString(11, "N/A");
						//	stmtValues += "0";
							pst.setString(12, "N/A");
							//stmtValues += "'N/A',";
							pst.setString(13, rs.getString("parentCode"));
						//	stmtValues += dealer_code+",";
							pst.setString(14, rs.getString("DealerName"));
						//	stmtValues += "'"+dealer_name+"',";
							pst.setString(15, rs.getString("customerCode"));
							//stmtValues += customer_code+",";
							pst.setString(16, rs.getString("CustomerName"));
							//stmtValues += "'"+customer_name+"',";
							pst.setString(17, "N/A");
							//stmtValues += "'"+dealer_number+"',";
							pst.setString(18, "N/A");
						}
						else{
							pst.setString(11, rs.getString("parentCode"));
							//stmtValues += dealer_code+",";
							pst.setString(12, rs.getString("DealerName"));
							//stmtValues += "'"+dealer_name+"',";
							pst.setString(13, rs.getString("customerCode"));
							//stmtValues += customer_code+",";
							pst.setString(14, rs.getString("CustomerName"));
						//	stmtValues += "'"+customer_name+"',";
							pst.setString(15, rs.getString("customerCode"));
						//	stmtValues += customer_code+",";
							pst.setString(16, rs.getString("CustomerName"));
						//	stmtValues += "'"+customer_name+"',";
							pst.setString(17, rs.getString("Customer_Number"));
							//stmtValues += "'"+customer_number+"',";
							pst.setString(18, rs.getString("Customer_Number"));
						}
						
						

						//stmtValues += "'"+customer_number+"',";
						
						pst.setString(19, rs.getString("PrimaryOwner"));
						//stmtValues += primary_owner_code+",";
						
						
						address = rs.getString("address");
						city = rs.getString("city");
						state = rs.getString("state");
						
						if(state !=null && city != null){
							
							if(state!=null){
								if(!statesMap.isEmpty() && statesMap.containsKey(state)){
									
									stateId = statesMap.get(state);
								//System.out.println("Map stateId:"+state);
								}
								else{
									try{
										
											db2Statement.executeUpdate("insert into State(StateName,Country) values('"+state+"','"+country+"')");
											//String stateQuery = "select * from State where StateName like '"+state+"'";
											rs1 = db2Statement.executeQuery("select * from State where StateName like '"+state+"'");
											if(rs1.next()){
												stateId = rs1.getInt("StateID");
												//System.out.println("DB stateId:"+state);
												statesMap.put(state,stateId);
											}
									}catch(SQLException e){
										//	String faultInsertStmt = "insert into fault_statements(serial_number,time_key,fault_details) values('Query Exception','"+timeKey+"','"+innerSQL.getMessage()+"')";
										try {
											//ConnectMySQL connMySql = new ConnectMySQL();
											//devConnection = connMySql.getConnection();
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
									//	String faultInsertStmt = "insert into fault_statements(serial_number,time_key,fault_details) values('Query Exception','"+timeKey+"','"+innerSQL.getMessage()+"')";
										
										
										try {
											//ConnectMySQL connMySql = new ConnectMySQL();
											//devConnection = connMySql.getConnection();
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
						//prodDBPST.setInt(4, rs.getInt("alertCode"));//binding dynamic variables to the lat long query eventID
						prodDBPST.setInt(5, rs.getInt("alertTypeCode"));//binding dynamic variables to the lat long query Event Type ID
						long latlongQueryStart = System.currentTimeMillis();
						rs2 = prodDBPST.executeQuery();
						long latlongQueryEnd = System.currentTimeMillis();//executing the lat long query to get the latitude and longitude for a machine for a particular event and period
						System.out.println(timeKey +" record "+row+"executed in "+(latlongQueryEnd - latlongQueryStart));
						
						while(rs2.next()){
							if(rs2.getInt("Parameter_ID") ==1)
								latitude = new BigDecimal(Double.valueOf(rs2.getString("Parameter_Value"))).setScale(4, BigDecimal.ROUND_CEILING).doubleValue();
							else
								longitude = new BigDecimal(Double.valueOf(rs2.getString("Parameter_Value"))).setScale(4, BigDecimal.ROUND_CEILING).doubleValue();
							fwVersion = rs2.getString("FW_Version_Number");
						}
						
						
						pst.setDouble(20, latitude);
						//stmtValues += latitude+",";
						pst.setDouble(21, longitude);
						//stmtValues += longitude+",";
						pst.setString(22,address);
						//stmtValues += "'"+address+"',";
						pst.setInt(23,cityId);
						//stmtValues += cityId+",";
						pst.setString(24,city);
						//stmtValues += "'"+city+"',";
						pst.setInt(25,stateId);
						//stmtValues += stateId+",";
						pst.setString(26,state);
						//stmtValues += "'"+state+"',";
						pst.setString(27,country);
						//stmtValues += "'"+country+"',";
						
						pst.setString(28, fwVersion);
						pst.setInt(29, rs.getInt("alertTypeCode"));
						
						pst.setString(30, rs.getString("alertTypeName"));
						pst.setInt(31,rs.getInt("alertCode"));
						
						pst.setString(32,rs.getString("alert"));
						
						
						pst.setString(33, rs.getString("Severity"));
						
						pst.setInt(34, rs.getInt("alertCount"));
						
						
						pst.setInt(35, partition_ID);
						//stmtValues += ","+partition_ID;
						pst.setString(36, rs.getString("Machine_Number"));
						//stmtValues += ",'"+machineNumber+"')";
						faultStatements.add( rs.getString("Serial_Number"));
						//System.out.println(Serial_Number+" : "+row);
						//iLogger.info(stmtValues);
						pst.addBatch();
						}catch(SQLException innerSQL){
							String faultInsertStmt = "insert into alert_fault_statements(serial_number,time_key,fault_details) values('Query Exception','"+timeKey+"','"+innerSQL.getMessage()+"')";
							
							fLogger.fatal("fault statemet:"+innerSQL.getMessage());
							try {
								//ConnectMySQL connMySql = new ConnectMySQL();
								//devConnection = connMySql.getConnection();
								localStatement = db2Connection.createStatement();
								localStatement.executeUpdate(faultInsertStmt);
							
							} catch (SQLException se) {
								// TODO Auto-generated catch block
								se.printStackTrace();
							} 
							innerSQL.printStackTrace();
						}
						if(row%1000 == 0)
						{
							//System.out.println(timeKey+"roww:"+row);
							long iteratorEnd = System.currentTimeMillis();
							iLogger.info(timeKey+" Alert Summary Migration  1000 iterations executed in: "+(iteratorEnd-iteratorStart));
							iteratorStart = System.currentTimeMillis();
							try{
							iLogger.info(timeKey+" Alert Summary Migration before executingbatch insertions for 1000 records");
							long preparedStmtStart = System.currentTimeMillis();
							pst.executeBatch();
							pst.clearBatch();
							long preparedStmtEnd = System.currentTimeMillis();
							iLogger.info(timeKey+" Alert Summary Migration executing batch insertions for 1000 records in "+(preparedStmtEnd-preparedStmtStart));
							}catch(BatchUpdateException bue){
								iLogger.info(" Alert Summary Migration inner BatchUpdateexception");
								bue.printStackTrace();
								int[] updatedCount = bue.getUpdateCounts();
								String faultInsertStmt = "insert into alert_fault_statements(serial_number,time_key,fault_details) values(?,?,?)";
								
								//PreparedStatement pst1;
								try {
									pst1 = db2Connection.prepareStatement(faultInsertStmt);
								
									if(updatedCount.length>0){
								for(int i=0;i<updatedCount.length;i++)
								{
									if(updatedCount[i]<0){
										int j=i;
										//iLogger.info("Exception statement :"+faultStatements.get(j));
										//iLogger.info("Exception next statement :"+faultStatements.get(j+1));
										pst1.setString(1, faultStatements.get(j));
										pst1.setString(2, timeKey);
										pst1.setString(3, bue.getMessage());
										pst1.addBatch();
										
									}
								}
								pst1.executeBatch();
									}
									else{
					String faultInsertStmt1 = "insert into alert_fault_statements(serial_number,time_key,fault_details) values('Query Exception','"+timeKey+"','"+bue.getMessage()+"')";
										
										try {
											//ConnectMySQL connMySql = new ConnectMySQL();
											//devConnection = connMySql.getConnection();
											localStatement = db2Connection.createStatement();
											localStatement.executeUpdate(faultInsertStmt);
										
										} catch (SQLException se) {
											// TODO Auto-generated catch block
											se.printStackTrace();
										} 
										bue.printStackTrace();
									}
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
								
							}
						}
							
						row++;
				}
				iLogger.info(timeKey+" Alert Summary Migration after executing while loop for query");
				int[] updateVins = pst.executeBatch();
				iLogger.info(timeKey+" Alert Summary Migration after executing batch insertions");
				result += ": "+row;
				}
		}catch(BatchUpdateException bue){
					iLogger.info(" Alert Summary Migration outer BatchUpdateexception");
					bue.printStackTrace();
					int[] updatedCount = bue.getUpdateCounts();
					String faultInsertStmt = "insert into alert_fault_statements(serial_number,time_key,fault_details) values(?,?,?)";
					
					//PreparedStatement pst1;
					try {
						pst1 = db2Connection.prepareStatement(faultInsertStmt);
					
						if(updatedCount.length>0){
					for(int i=0;i<updatedCount.length;i++)
					{
						if(updatedCount[i]<0){
							int j=i;
							//iLogger.info("Exception statement :"+faultStatements.get(j));
							//iLogger.info("Exception next statement :"+faultStatements.get(j+1));
							pst1.setString(1, faultStatements.get(j));
							pst1.setString(2, timeKey);
							pst1.setString(3, bue.getMessage());
							pst1.addBatch();
							
						}
					}
					pst1.executeBatch();
						}
						else{
		String faultInsertStmt1 = "insert into alert_fault_statements(serial_number,time_key,fault_details) values('Query Exception','"+timeKey+"','"+bue.getMessage()+"')";
							
							try {
								//ConnectMySQL connMySql = new ConnectMySQL();
								//devConnection = connMySql.getConnection();
								localStatement = db2Connection.createStatement();
								localStatement.executeUpdate(faultInsertStmt);
							
							} catch (SQLException se) {
								// TODO Auto-generated catch block
								se.printStackTrace();
							} 
							bue.printStackTrace();
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
				}
				catch (SQLException e) {
					
					// TODO Auto-generated catch block
					
					//cursor_position = row+1;
					iLogger.info(" Alert Summary Migration SQL Exception ------");
					iLogger.info(e);
					//e.printStackTrace();
				} 
				catch(Exception e)
				{
					iLogger.info(" Alert Summary Migration Normal Exception ------");
					iLogger.info(e);
		String faultInsertStmt = "insert into alert_fault_statements(serial_number,time_key,fault_details) values('normal exception','"+timeKey+"','"+e.getMessage()+"')";
					
					
					try {
						//ConnectMySQL connMySql = new ConnectMySQL();
						//devConnection = connMySql.getConnection();
						localStatement = db2Connection.createStatement();
						localStatement.executeUpdate(faultInsertStmt);
					
					} catch (SQLException se) {
						// TODO Auto-generated catch block
						se.printStackTrace();
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
						if(db2Connection!=null)
						{
							try {
								db2Connection.close();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					}
				}
		return result;
	}*/
}
