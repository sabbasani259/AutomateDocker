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

public class ThreadForClosedAlertDetailMigration implements Callable<String>{
	
	String time_key;
	public ThreadForClosedAlertDetailMigration(String time_key){
		this.time_key = time_key;
		//Thread.currentThread().setName(this.time_key);
	}
	
	public ThreadForClosedAlertDetailMigration(){
		
	}
	@Override
	public String call() throws Exception {
		// TODO Auto-generated method stub
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Closed Alert Detail Migration "+Thread.currentThread().getName()+" is running...");
		String noOfRecords = getInsightAlertDayAgg(time_key);
		return noOfRecords;
	}
	
	public String getInsightAlertDayAgg(String timeKey) {
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
		//PreparedStatement prodDBPST= null;
		PreparedStatement pst1= null;
		PreparedStatement pst2= null;
		Statement db2Statement = null;
		HashMap<String,Integer> statesMap = new HashMap<String, Integer>();
		HashMap<String,Integer> citiesMap = new HashMap<String, Integer>();
		String ac_type = null;
		String fwVersion = null;
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
		Calendar cal = Calendar.getInstance();
		String createdTimeStamp = sdf1.format(cal.getTime());
		try {
			//Date time_Key = sdf.parse(timeKey);
			iLogger.info("Closed Alert Detail Migration "+timeKey+" is executing day agg... with time_key "+timeKey);
			//ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
			iLogger.info("Closed Alert Detail Migration  getting proddb2 connection");
				db2Connection = new ConnectMySQL().getProdDb2Connection();
				db2Connection.setAutoCommit(false);
				iLogger.info("Closed Alert Detail Migration connected to proddb2 connection");
				
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
						"aa.*,state_city.Machine_Hours,state_city.Location as location,state_city.Address,state_city.State,state_city.City,bb.Machine_Number,bb.asset_type_code,bb.type_name,bb.group_code,bb.group_name "+
					"FROM ( "+
						"SELECT "+ 
							"ae.Serial_Number,Event_Generated_Time as timeKey, "+
							"Week(Event_Generated_Time)+1 as week_no, "+
							"MONTH(Event_Generated_Time) as month, "+
							"QUARTER(Event_Generated_Time) as quarter, "+
							"YEAR(Event_Generated_Time) as year,ac.account_id,ac.Account_Code as customerCode,ac.account_name as CustomerName,pa.Account_Code as parentCode,pa.account_name as DealerName,za.Account_Code as ZonalCode,za.account_name as ZonalName,ac.Account_Code as  PrimaryOwner, "+
							"ac.Mobile as Customer_Number,pa.Mobile as Dealer_Number,"+
							"ae.Event_ID as alertCode, "+
							"be.Event_Name as alert, "+
							"ae.Event_Type_ID as alertTypeCode,et.Event_Type_Name as alertTypeName,ae.Event_Severity as Severity," +
							"ae.Event_Generated_Time as AlertGenerationTime,ae.Event_Closed_Time as AlertClosureTime" +
							",time_to_sec(timediff(ae.Event_Closed_Time,ae.Event_Generated_Time))/3600 as ResolutionTimeInHrs," +
							"ae.Event_Generated_Time as TxnDate,ao.Ownership_Start_Date  "+ 
						"FROM "+ 
							"asset_owners ao, "+
							"account ac, "+
							"asset_event ae, "+ 
							"event_type et, "+
							"business_event be, "+
							"account pa left outer join account za on za.account_id = pa.Parent_ID "+ 
						"WHERE "+ 
							"ae.created_timestamp like '"+timeKey+"%' and "+ 
							"ae.Serial_Number is not null and "+ 
							"ao.Serial_Number = ae.Serial_Number and "+ 
							"ao.Ownership_Start_Date= ( "+
								"SELECT "+ 
									"max(ao1.Ownership_Start_Date) "+ 
								"FROM "+ 
									"asset_owners ao1 "+ 
								"WHERE "+
									"ao1.Serial_Number=ae.Serial_Number and "+ 
									"ao1.Ownership_Start_Date<=ae.Event_Generated_Time) and "+ 
							"ac.account_id = ao.Account_ID and " +
							"ae.active_status = 0 and "+ 
							"pa.Account_ID = ac.Parent_ID and "+ 
							"ae.Event_ID = be.Event_ID and "+ 
							"ae.Event_Type_ID = et.Event_Type_ID "+ 
						"GROUP BY ae.Serial_Number,ae.Event_Generated_Time,ae.Event_ID "+ 
						"ORDER BY ae.Serial_Number,ae.Event_Type_ID "+
						") as aa "+ 
						"LEFT OUTER JOIN ("+
					"SELECT "+
							"dayagg.Serial_Number, "+
							"dayagg.Machine_Hours, "+
							"dayagg.Location, "+
							"dayagg.Address, "+
							"dayagg.State,dayagg.City, "+
							"a_t.account_id  "+
						//DF20170720 : SU334449	remote_monitoring_fact_data_dayagg --> remote_monitoring_fact_data_dayagg_json_new
						"FROM "+
							"remote_monitoring_fact_data_dayagg_json_new dayagg, "+
							"tenancy_dimension td, "+
							" account_tenancy a_t "+
						"WHERE "+
							"dayagg.Time_Key = '"+timeKey+"' and  "+
							"dayagg.tenancy_id = td.Tenancy_Dimension_ID and  "+
							"td.tenancy_id = a_t.tenancy_id  "+
						") as state_city "+
					"ON "+
						"state_city.Serial_Number = aa.Serial_Number and  "+
						"state_city.account_id = aa.account_id  "+
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
						"aa.Serial_Number = bb.Serial_number";
				//System.out.println(migrationQuery);
				//System.out.println("Query successfully executed");
				//System.out.println(migrationQuery);
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
				iLogger.info("Closed Alert Detail Migration total records for thread "+timeKey+" are:"+rowCount+" exceuted in "+(queryEndTime - queryStartTime));

				//System.out.println("total records for thread "+timeKey+" are:"+rowCount+" exceuted in "+(queryEndTime - queryStartTime));
				String insertStatement = "  insert into alertInsight_detail(AssetID,TxnDate,TxnWeek,TxnMonth,TxnQuarter,TxnYear,"+
						"ModelCode,ModelName,ProfileCode,ProfileName,ZonalCode,ZonalName,DealerCode,DealerName,CustCode,CustomerName,DealerContact,CustContact,PrimaryOwner," +
						"Latitude,Longitude,Address,StateId,State,CityId,City,Country," +
						"FWVersion,AlertTypeCode,AlertType,AlertCode,Alert,AlertSeverity,AlertGenerationTime,AlertClosureTime," +
						"ResolutionTimeInHrs,PartitionID,MachineNum,AlertGenCMH,LastUpdatedTime) "+
						"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				long endQueryTime = System.currentTimeMillis();
				
				String updateStatement =  "update alertInsight_detail set AlertClosureTime = ?,ResolutionTimeInHrs=? " +
						"where AssetID = ? and TxnDate = ? and AlertGenerationTime = ? and AlertCode = ? and AlertTypeCode = ?";
				
				//String insertQuery = insertStatement + values;
				iLogger.info("Alert Detail Migration "+timeKey+" thread execution time for query"+((int)(((endQueryTime-queryStartTime)/1000)%60))+"");
				//System.out.println(timeKey+" thread execution time for query"+((int)(((endQueryTime-queryStartTime)/1000)%60))+"");
				if(db2Connection.isClosed())
					db2Connection = new ConnectMySQL().getProdDb2Connection();
				pst = db2Connection.prepareStatement(insertStatement);
				
				pst2 = db2Connection.prepareStatement(updateStatement);
				db2Statement = db2Connection.createStatement();
				/*String latLongQuery = "SELECT amh.FW_Version_Number,amd.Parameter_ID,amd.Parameter_Value " +
						"FROM asset_monitoring_header amh," +
						"asset_monitoring_detail_data amd " +
						"WHERE amh.Serial_Number=? and " +
						"amh.Transaction_Timestamp=(" +
							"SELECT max(ae.Event_Generated_Time) " +
							"FROM asset_event ae " +
							"WHERE ae.Serial_Number = amh.Serial_Number and " +
							"ae.Event_Generated_Time>? and " +
							"ae.Event_Generated_Time<=? and " +
							"Event_ID = ? and Event_Type_ID = ?) and amd.Transaction_Number = amh.Transaction_Number and amd.Parameter_ID in (1,2)";*/
				
				iLogger.info("Alert Detail Migration "+timeKey+"before executing while loop for query");
				//prodDBPST = prodConnection.prepareStatement(latLongQuery);
				//System.out.println(timeKey+"before executing while loop for query");
				long iteratorStart = System.currentTimeMillis();
				//String minTimeKey = timeKey+" 00:00:00";
				//String maxTimeKey = timeKey+" 23:59:59";
				ResultSet db2RS = null;
				while(rs.next()){
					try{
						
						
						
						db2RS = db2Statement.executeQuery("select AssetID from alertInsight_detail " +
								"where AssetID = '"+rs.getString("Serial_Number")+"' and TxnDate = Date('"+rs.getTimestamp("AlertGenerationTime")+"') and " +
										"AlertGenerationTime = '"+rs.getTimestamp("AlertGenerationTime")+"' and AlertCode = "+rs.getInt("alertCode")+" and AlertTypeCode = "+rs.getInt("alertTypeCode"));
						if(db2RS.next()){
							
							iLogger.info("Alert Detail Migration "+timeKey+" updtae mode");
							pst2.setTimestamp(1, rs.getTimestamp("AlertClosureTime"));
							pst2.setDouble(2, new BigDecimal(rs.getDouble("ResolutionTimeInHrs")).setScale(4, BigDecimal.ROUND_CEILING).doubleValue());
							pst2.setString(3, rs.getString("Serial_Number"));
							pst2.setDate(4, new java.sql.Date(sdf.parse(timeKey).getTime()));
							
							pst2.setTimestamp(5, rs.getTimestamp("AlertGenerationTime"));
							pst2.setInt(6,rs.getInt("alertCode"));
							pst2.setInt(7, rs.getInt("alertTypeCode"));	
							pst2.addBatch();
							continue;
						}
						
					pst.setString(1, rs.getString("Serial_Number"));
					
					//prodDBPST.setString(1, rs.getString("Serial_Number"));//binding dynamic variables to the lat long query serialNumber
					//prodDBPST.setString(2, minTimeKey);//binding dynamic variables to the lat long query min Event_Generated_Time
					//prodDBPST.setString(3, maxTimeKey);//binding dynamic variables to the lat long query max Event_Generated_Time
					
					//binding the varibles for the lat long query 
					//	stmtValues += "'"+Serial_Number+"',";
						timeKey = sdf.format(rs.getTimestamp("AlertGenerationTime"));
						//System.out.println("TxnDate "+timeKey);
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
						if(rs.getString("location")!=null)
						{
							String location = rs.getString("location");
							String[] lat_long = location.split(",");
							latitude = Double.parseDouble(lat_long[0]);
							longitude = Double.parseDouble(lat_long[1]);
						}
						
						address = rs.getString("Address");
						city = rs.getString("City");
						state = rs.getString("State");
						
						if(state !=null && city != null){
							
							if(state!=null){
								if(!statesMap.isEmpty() && statesMap.containsKey(state)){
									
									stateId = statesMap.get(state);
								//System.out.println("Map stateId:"+state);
								}
								else{
									try{
										
											db2Statement.executeUpdate("insert into State(StateName,Country) values('"+state+"','"+country+"')");
											if(db2Connection.isClosed()){
												db2Connection = new ConnectMySQL().getProdDb2Connection();
												db2Connection.commit();
												}
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
											if(db2Connection.isClosed()){
												db2Connection = new ConnectMySQL().getProdDb2Connection();
												db2Connection.commit();
												}
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
						/*prodDBPST.setInt(5, rs.getInt("alertTypeCode"));//binding dynamic variables to the lat long query Event Type ID
						long latlongQueryStart = System.currentTimeMillis();
						rs2 = prodDBPST.executeQuery();
						long latlongQueryEnd = System.currentTimeMillis();//executing the lat long query to get the latitude and longitude for a machine for a particular event and period
						System.out.println(timeKey +" record "+row+"executed in "+(latlongQueryEnd - latlongQueryStart));*/
						
						/*while(rs2.next()){
							if(rs2.getInt("Parameter_ID") ==1)
								latitude = new BigDecimal(Double.valueOf(rs2.getString("Parameter_Value"))).setScale(4, BigDecimal.ROUND_CEILING).doubleValue();
							else
								longitude = new BigDecimal(Double.valueOf(rs2.getString("Parameter_Value"))).setScale(4, BigDecimal.ROUND_CEILING).doubleValue();
							fwVersion = rs2.getString("FW_Version_Number");
						}*/
						
						
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
						//stmtValues += "'"+city+"',";
						
						//stmtValues += "'"+state+"',";
						pst.setString(27,country);
						//stmtValues += "'"+country+"',";
						
						pst.setString(28, fwVersion);
						pst.setInt(29, rs.getInt("alertTypeCode"));
						
						pst.setString(30, rs.getString("alertTypeName"));
						pst.setInt(31,rs.getInt("alertCode"));
						
						pst.setString(32,rs.getString("alert"));
						
						
						pst.setString(33, rs.getString("Severity"));
						//System.out.println(rs.getTimestamp("AlertGenerationTime"));
						pst.setTimestamp(34, rs.getTimestamp("AlertGenerationTime"));
						pst.setTimestamp(35, rs.getTimestamp("AlertClosureTime"));
						pst.setDouble(36, new BigDecimal(rs.getDouble("ResolutionTimeInHrs")).setScale(4, BigDecimal.ROUND_CEILING).doubleValue());
						
						
						pst.setInt(37, partition_ID);
						//stmtValues += ","+partition_ID;
						pst.setString(38, rs.getString("Machine_Number"));
						pst.setDouble(39, new BigDecimal(rs.getDouble("Machine_Hours")).setScale(4, BigDecimal.ROUND_CEILING).doubleValue());
						//stmtValues += ",'"+machineNumber+"')";
						pst.setString(40,createdTimeStamp);
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
							iLogger.info("Alert Detail Migration "+timeKey+" 1000 iterations executed in: "+(iteratorEnd-iteratorStart));
							iteratorStart = System.currentTimeMillis();
							try{
							iLogger.info("Alert Detail Migration "+timeKey+"before executingbatch insertions for 1000 records");
							long preparedStmtStart = System.currentTimeMillis();
							pst.executeBatch();
							pst2.executeBatch();
							if(db2Connection.isClosed()){
								db2Connection = new ConnectMySQL().getProdDb2Connection();
								}
							db2Connection.commit();
							pst.clearBatch();
							pst2.clearBatch();
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
				pst2.executeBatch();
				if(db2Connection.isClosed()){
					db2Connection = new ConnectMySQL().getProdDb2Connection();
					}
				db2Connection.commit();

				pst.clearBatch();
				pst2.clearBatch();
				iLogger.info("Alert Detail Migration "+timeKey+"after executing batch insertions");
				result += ": "+row;
				}
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
					if(pst2!=null){
						try {
							pst2.close();
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
							
									db2Connection.commit();
								db2Connection.close();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					}
				}
		return result;
	}
}
