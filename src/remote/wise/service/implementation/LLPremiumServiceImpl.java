package remote.wise.service.implementation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import remote.wise.EAintegration.Qhandler.PremiumMachinesKafkaDataProducer;
import remote.wise.EAintegration.Qhandler.PremiumMachinesKafkaDataProducerForRabbitMq;
import remote.wise.handler.SendEmailWithKafka;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.AssetUtil;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;
import scala.collection.mutable.LinkedList;
/*
 * ME100011369 : Dhiraj Kumar : 20240414 : Livelink Premium subcription report
 */
public class LLPremiumServiceImpl {
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;

	public List<HashMap<String,String>> getMachineListUnderTenancyId(String tenancyId,String vin,String platform,String model,String dealerName,String customerName,String premiumFlag , String premiumStartDate, String premiumEndDate, String installationDate , String installationEndDate , int startLimit , int endLimit , String limitFlag){		
		
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet=null;
		List<HashMap<String, String>> mapList = new ArrayList<>();
		String query = "";
		
		try{
			
			if(premiumFlag.equalsIgnoreCase("1")){
				query = "select aos1.serial_number,at.Asset_Type_Code,ag.Asseet_Group_Name as profile,aos1.Asset_Type_ID,a.Renewal_Date,a.Install_Date,a.PremFlag,llps1.LLPremStartDate,llps1.LLPremEndDate,croe.Dealer_Name,croe.CustomerName,croe.Customer_Mobile,at.asset_type_name as model,croe.Zone from (select serial_number,Asset_Type_ID,Asset_Group_ID from asset_owner_snapshot where account_id in (select account_id from account_tenancy where tenancy_id in ("+tenancyId+"))) as aos1" 
						+" inner join (select * from asset where premFlag = '"+premiumFlag+"') a ON a.serial_number = aos1.serial_number" 
						+" inner join (select llps.serial_number as serial_number, llps.updatedOn, max(llps.LLPremStartDate) as LLPremStartDate,max(llps.LLPremEndDate) as LLPremEndDate from LLPremiumSubs AS llps, (select serial_number, max(updatedOn) as updatedOn from LLPremiumSubs AS llps group by serial_number) as temp where llps.serial_number=temp.serial_number and llps.updatedOn = temp.updatedOn group by llps.serial_number) as llps1 ON llps1.serial_number = a.serial_number"
						//+" inner join (select serial_number,max(SubsEndDate) as Renewal_Date  from asset_renewal_data group by serial_number) ard ON aos1.serial_number = ard.serial_number"
						+" inner join (select * from asset_type where Asset_Type_Code in (select Asset_Type_Code from LLPremiumModels)) at ON aos1.Asset_Type_ID = at.Asset_Type_ID"
						+" inner join asset_group ag ON aos1.Asset_Group_ID = ag.Asset_Group_ID"
						+" left outer join  com_rep_oem_enhanced croe ON  aos1.serial_number=croe.serial_number";
			}else{
//				query = "select aos1.serial_number,at.Asset_Type_Code,ag.Asseet_Group_Name as profile,aos1.Asset_Type_ID,ard.Renewal_Date,a.Install_Date,a.PremFlag,croe.Dealer_Name,croe.CustomerName,croe.Customer_Mobile,at.asset_type_name as model,croe.Zone from (select serial_number,Asset_Type_ID,Asset_Group_ID from asset_owner_snapshot where account_id in (select account_id from account_tenancy where tenancy_id in ("+tenancyId+"))) as aos1" 
//						+" inner join (select * from asset where premFlag = '"+premiumFlag+"') a ON a.serial_number = aos1.serial_number" 
//						+" inner join (select serial_number,max(SubsEndDate) as Renewal_Date  from asset_renewal_data group by serial_number) ard ON aos1.serial_number = ard.serial_number"
//						+" inner join (select * from asset_type where Asset_Type_Code in (select Asset_Type_Code from LLPremiumModels)) at ON aos1.Asset_Type_ID = at.Asset_Type_ID"
//						+" inner join asset_group ag ON aos1.Asset_Group_ID = ag.Asset_Group_ID"
//						+" left outer join  com_rep_oem_enhanced croe ON  aos1.serial_number=croe.serial_number";
				query = "select aos1.serial_number,at.Asset_Type_Code,ag.Asseet_Group_Name as profile,aos1.Asset_Type_ID,a.Install_Date,a.PremFlag,croe.Dealer_Name,croe.CustomerName,croe.Customer_Mobile,at.asset_type_name as model,croe.Zone,a.Renewal_Date , aos2.Ownership_Start_Date as GateOutDate from (select serial_number,Asset_Type_ID,Asset_Group_ID  from asset_owner_snapshot where account_id in (select account_id from account_tenancy where tenancy_id in ("+tenancyId+"))) as aos1 "
						+ "inner join (select * from asset a where a.premFlag = '"+premiumFlag+"') a ON a.serial_number = aos1.serial_number "
						+ " left outer  JOIN (select serial_number ,Ownership_Start_Date from asset_owner_snapshot aos where aos.account_type = 'Dealer') aos2 ON aos1.serial_number = aos2.serial_number "
						+ "inner join (select * from asset_type where Asset_Type_Code in (select Asset_Type_Code from LLPremiumModels)) at ON aos1.Asset_Type_ID = at.Asset_Type_ID "
						+ "inner join asset_group ag ON aos1.Asset_Group_ID = ag.Asset_Group_ID "
						+ "left outer join  com_rep_oem_enhanced croe ON  aos1.serial_number=croe.serial_number ";

			}
		
			if((!vin.equalsIgnoreCase("null") && vin != null) || (!platform.equalsIgnoreCase("null") && platform != null) || (!model.equalsIgnoreCase("null") && model != null)
					|| (!dealerName.equalsIgnoreCase("null") && dealerName != null) || (!customerName.equalsIgnoreCase("null") && customerName != null)
					||(!premiumStartDate.equalsIgnoreCase("null") && premiumStartDate != null && premiumStartDate != "NA")
					||(!premiumEndDate.equalsIgnoreCase("null") && premiumEndDate != null && premiumEndDate != "NA")
					||(!installationDate.equalsIgnoreCase("null") && installationDate != null && installationDate != "NA")){
				
				query = "select top.* from ("+query+") as top";
			}
				
			Boolean filterApplied = false;			//To check whether to put 'and' keyword first or not for top query
			if(!vin.equalsIgnoreCase("null") && vin != null && vin.length() == 7){
				vin =	AssetUtil.getVinNoUsingMachineNo(vin);
			}
			if(!vin.equalsIgnoreCase("null") && vin != null ){
				if(filterApplied){
					query +=" and top.serial_number = '"+vin+"'";
				}else{
					query +=" where top.serial_number = '"+vin+"'";
					filterApplied = true;
				}
			}
				
			
			if(!platform.equalsIgnoreCase("null") && platform != null)
			{
				if(filterApplied){
					query += " and top.profile = '"+platform+"'";
				}else{
					query += " where top.profile = '"+platform+"'";
					filterApplied = true;
				}
			}
				
			
			if(!model.equalsIgnoreCase("null") && model != null){
				if(filterApplied){
					query += " and top.model = '"+model+"'";
				}else{
					query += " where top.model = '"+model+"'";
					filterApplied = true;
				}
			}
				
			
			if(!dealerName.equalsIgnoreCase("null") && dealerName != null )
			{
				if(dealerName.equalsIgnoreCase("NA")){
					if(filterApplied){
						query += " and top.Dealer_Name is NULL";
					}else{
						query += " where top.Dealer_Name is NULL";
						filterApplied = true;
					}
					
				}else{
					dealerName = dealerName.split("-")[0];
					if(filterApplied){
						query += " and top.Dealer_Name = '"+dealerName+"'";
					}else{
						query += " where top.Dealer_Name = '"+dealerName+"'";
						filterApplied = true;
					}
					
				}
					
					
			}
			
			if(!customerName.equalsIgnoreCase("null") && customerName != null)
			{
				if(customerName.equalsIgnoreCase("NA")){
					if(filterApplied){
						query += " and top.CustomerName is NULL";
					}else{
						query += " where top.CustomerName is NULL";
						filterApplied = true;
					}
				}else{
					if(filterApplied){
						query += " and top.CustomerName = '"+customerName+"'";
					}else{
						query += " where top.CustomerName = '"+customerName+"'";
						filterApplied = true;
					}
				}
					
			}
				
			
//			if(!renewalDate.equalsIgnoreCase("null") && renewalDate != null && renewalDate != "NA"){
//				if(filterApplied){
//					query += " and top.Renewal_Date = '"+renewalDate+"'";
//				}else{
//					query += " where top.Renewal_Date = '"+renewalDate+"'";
//					filterApplied = true;
//				}
//			}
			if( premiumFlag.equalsIgnoreCase("1") && !premiumStartDate.equalsIgnoreCase("null") && premiumStartDate != null && premiumStartDate != "NA"
					&& !premiumEndDate.equalsIgnoreCase("null") && premiumEndDate != null && premiumEndDate != "NA"){
				if(filterApplied){
					query += " and top.LLPremStartDate >= '"+premiumStartDate+"' and top.LLPremStartDate <= '"+premiumEndDate+"'";
				}else{
					query += " where top.LLPremStartDate >= '"+premiumStartDate+"' and top.LLPremStartDate <= '"+premiumEndDate+"'";
					filterApplied = true;
				}
			}	
			if(premiumFlag.equalsIgnoreCase("0") && !installationDate.equalsIgnoreCase("null") && installationDate != null && installationDate != "NA"
					&& !installationEndDate.equalsIgnoreCase("null") && installationEndDate != null && installationEndDate != "NA"){
				if(filterApplied){
					query += " and top.Install_Date >= '"+installationDate+"' and top.Install_Date <= '"+installationEndDate+"'";
				}else{
					query += " where top.Install_Date >= '"+installationDate+"' and top.Install_Date <= '"+installationEndDate+"'";
					filterApplied = true;
				}
			}
			
//			if(premiumFlag.equalsIgnoreCase("0") && limitFlag.equalsIgnoreCase("true") ){
//				query +=  " limit " +startLimit + " ," + endLimit;
//			}
				
				startLimit = (startLimit-1) * endLimit;
				query +=  " limit " +startLimit + " ," + endLimit;
				
			iLogger.info("getMachineListUnderTenancyId query ..: "+query);
			
			
			ConnectMySQL connectionObj = new ConnectMySQL();
			con = connectionObj.getConnection();
			statement = con.createStatement();
			
			
			resultSet = statement.executeQuery(query);
			HashMap<String, String> tableMap = null;
			while(resultSet.next())
			{
				if(resultSet.getString("Dealer_Name") != null){
				tableMap = new HashMap<String,String>();
				tableMap.put("vin",resultSet.getString("serial_number"));
				tableMap.put("subscription",resultSet.getString("PremFlag"));
				
				if(premiumFlag.equalsIgnoreCase("1")){
				
					if(resultSet.getString("LLPremStartDate") != null)
						tableMap.put("premiumStartDate",resultSet.getString("LLPremStartDate").substring(0, 10));
					else
						tableMap.put("premiumStartDate","NA");
					
					if(resultSet.getString("LLPremEndDate") != null)
						tableMap.put("premiumEndDate",resultSet.getString("LLPremEndDate").substring(0, 10));
					else
						tableMap.put("premiumEndDate","NA");
				}
				
				
				if(resultSet.getString("Dealer_Name") != null)
						tableMap.put("dealerName",resultSet.getString("Dealer_Name"));
				else
					tableMap.put("dealerName","NA");
				
				if(resultSet.getString("CustomerName") != null)
						tableMap.put("customerName",resultSet.getString("CustomerName"));
				else
					tableMap.put("customerName","NA");
				
				if(resultSet.getString("Profile") != null)
					tableMap.put("platform",resultSet.getString("Profile"));
				else
					tableMap.put("platform","NA");
				
				if(resultSet.getString("model") != null)
					tableMap.put("model",resultSet.getString("model"));
				else
					tableMap.put("model","NA");
				
				if(resultSet.getString("zone") != null)
					tableMap.put("zone",resultSet.getString("zone"));
				else
					tableMap.put("zone","NA");
				
				if(resultSet.getString("Customer_Mobile") != null)
					tableMap.put("customerMobileNumber",resultSet.getString("Customer_Mobile"));
				else
					tableMap.put("customerMobileNumber","NA");
				
				if (resultSet.getString("Install_Date") != null) {
						tableMap.put("installDate", resultSet.getString("Install_Date").substring(0, 10));
						if (premiumFlag.equalsIgnoreCase("0")) {
							tableMap.put("premiumPunchingDate","NA" );

						}

					}
				else{
					tableMap.put("installDate","NA");
					if (premiumFlag.equalsIgnoreCase("0")) {

						if (resultSet.getString("GateOutDate") != null)
							tableMap.put("premiumPunchingDate", resultSet.getString("GateOutDate").substring(0, 10));
						else
							tableMap.put("premiumPunchingDate", "NA");

					}
					
				}
					
				
				if(resultSet.getString("Renewal_Date") != null)
					tableMap.put("renewalDate",resultSet.getString("Renewal_Date").substring(0, 10));
				else
					tableMap.put("renewalDate","NA");

				
//				if (premiumFlag.equalsIgnoreCase("0")) {
//
//					if (resultSet.getString("GateOutDate") != null)
//						tableMap.put("premiumPunchingDate", resultSet.getString("GateOutDate").substring(0, 10));
//					else
//						tableMap.put("premiumPunchingDate", "NA");
//
//				}
				
				mapList.add(tableMap);
			}
			
			iLogger.info("result map: "+mapList);
			}
		}catch(Exception ex)
		{
			
			ex.printStackTrace();
			fLogger.fatal("Exception occurred while getMachineListUnderTenancyId in LLPremiumServiceImpl: "+ex.getMessage());
			
		}finally{
			if(resultSet!=null)
				try {
					resultSet.close();
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
	
	@SuppressWarnings("null")
	public String setSubscription(HashMap<String, String> jsonData){
		
		String userId = (String) jsonData.get("userId");
		String fleet = (String) jsonData.get("fleet");
		List<HashMap<String, String>> machines = null;
		ResultSet result = null;
		int resultSet;
		String updateQuery = "";
		String insertQuery = "";
		
		try{
				machines = new Gson().fromJson(
						 (String) jsonData.get("machineData"),
						 new TypeToken<List<HashMap<String, String>>>() {
						 }.getType());
			
				

			Timestamp createdTimestamp = new Timestamp(new Date().getTime());
			SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat date_format2 = new SimpleDateFormat("yyyy-MM-dd");
			String createdTimeInString = date_format.format(createdTimestamp.getTime());
			Date startDate = null;
			Date renewalDate = null;
			Date installDate = null;
			Date currentDate = date_format2.parse(createdTimeInString.substring(0,10));
			
			ConnectMySQL connectionObj = new ConnectMySQL();
	        try (Connection connection = connectionObj.getConnection();
	                Statement statement = connection.createStatement()) {
	        	
	        	for(int i=0;i<machines.size();i++){
	        		String premiumFlag = machines.get(i).get("premiumFlag");
	        		if(machines.get(i).get("premiumStartDate") != null && machines.get(i).get("premiumStartDate").equalsIgnoreCase("NA"))
	        		machines.get(i).put("premiumStartDate", null);
	        		if(machines.get(i).get("premiumStartDate") != null && machines.get(i).get("premiumEndDate").equalsIgnoreCase("NA"))
	        		machines.get(i).put("premiumEndDate", null);
	        		
	        		//add a flag for fleet so that renewal date can be checked for a vin and update the premFlaf to 1 test below fleet logic
	        		if(premiumFlag.equalsIgnoreCase("1")){
	        			
	        			if(fleet.equalsIgnoreCase("1")){
	        				//String selectQuery = "select serial_number,max(SubsEndDate) as Renewal_Date  from asset_renewal_data where serial_number= '"+machines.get(i).get("vin")+"' group by serial_number";
	        				String selectQuery = "select serial_number, Renewal_Date, install_date  from asset where serial_number= '"+machines.get(i).get("vin")+"'";
	        				result = statement.executeQuery(selectQuery);
	        				iLogger.info("selectQuery query for fleet premium update: "+selectQuery);
	        				startDate = date_format2.parse(machines.get(i).get("premiumStartDate").substring(0,10));
	        				result.first();
	        				if(result.getString("Renewal_Date") !=null && result.getString("install_date")!=null) {
	        					String d = result.getString("Renewal_Date").substring(0,10);
	        					renewalDate = date_format2.parse(d);

	        					//	        				while(result.next()){
	        					//	        					String d = result.getString("Renewal_Date").substring(0,10);
	        					//	        					renewalDate = date_format2.parse(d);
	        					//	        					
	        					//	        				}

	        					long timeDiff = Math.abs(startDate.getTime()-renewalDate.getTime());
	        					long noOfdaysBetween = (TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS));

	        					if(noOfdaysBetween < 365){
	        						return "renewMachine";
	        					}

	        					//insertQuery = "insert into LLPremiumSubs (serial_number,NoOfyears,UpdatedOn,UpdatedBy,LLPremStartDate,LLPremEndDate) values ('"+machines.get(i).get("vin")+"','"+machines.get(i).get("noOfYrs")+"','"+createdTimeInString+"','"+userId+"','"+machines.get(i).get("premiumStartDate")+"','"+machines.get(i).get("premiumEndDate")+"')";
	        					insertQuery = "insert into LLPremiumSubs (serial_number,NoOfyears,UpdatedOn,UpdatedBy,LLPremStartDate,LLPremEndDate,LLPremPunchingDate) values ('"+machines.get(i).get("vin")+"','"+machines.get(i).get("noOfYrs")+"','"+createdTimeInString+"','"+userId+"','"+machines.get(i).get("premiumStartDate")+"','"+machines.get(i).get("premiumEndDate")+"','"+machines.get(i).get("premiumPunchingDate")+"')";
	        					iLogger.info("insert query for premFlag = 1: "+insertQuery);
	        					resultSet = statement.executeUpdate(insertQuery);

	        					iLogger.info("updateQuery query for premFlag = 1: "+updateQuery);
	        					if(resultSet ==1){
	        						updateQuery = "update asset set PremFlag= '"+premiumFlag+"' where serial_number = ('"+machines.get(i).get("vin")+"')";	
	        						statement.executeUpdate(updateQuery);
	        					}
	        				} else {
	        					insertQuery = "insert into LLPremiumSubs (serial_number,NoOfyears,UpdatedOn,UpdatedBy,PunchingDate) values ('"+machines.get(i).get("vin")+"','"+machines.get(i).get("noOfYrs")+"','"+createdTimeInString+"','"+userId+"','"+machines.get(i).get("premiumPunchingDate")+"')";
	        					iLogger.info("insert query for premFlag = 1: "+insertQuery);
	        					resultSet = statement.executeUpdate(insertQuery);

	        					iLogger.info("updateQuery query for premFlag = 1: "+updateQuery);
	        					if(resultSet ==1){
	        						updateQuery = "update asset set PremFlag= '"+premiumFlag+"' where serial_number = ('"+machines.get(i).get("vin")+"')";	
	        						statement.executeUpdate(updateQuery);
	        					}
	        				}
	        				
	        			}else{
	        				if (machines.get(i).get("premiumStartDate")!=null && !(machines.get(i).get("premiumStartDate")).equalsIgnoreCase("NA")) {
	        					startDate = date_format2.parse(machines.get(i).get("premiumStartDate").substring(0,10));

	        					if(currentDate.equals(startDate)) {
	        						//insertQuery = "insert into LLPremiumSubs (serial_number,NoOfyears,UpdatedOn,UpdatedBy,LLPremStartDate,LLPremEndDate) values ('"+machines.get(i).get("vin")+"','"+machines.get(i).get("noOfYrs")+"','"+createdTimeInString+"','"+userId+"','"+machines.get(i).get("premiumStartDate")+"','"+machines.get(i).get("premiumEndDate")+"')";
	        						insertQuery = "insert into LLPremiumSubs (serial_number,NoOfyears,UpdatedOn,UpdatedBy,LLPremStartDate,LLPremEndDate,LLPremPunchingDate) values ('"+machines.get(i).get("vin")+"','"+machines.get(i).get("noOfYrs")+"','"+createdTimeInString+"','"+userId+"','"+machines.get(i).get("premiumStartDate")+"','"+machines.get(i).get("premiumEndDate")+"','"+machines.get(i).get("premiumPunchingDate")+"')";
	        						iLogger.info("insert query for premFlag = 1: "+insertQuery);
	        						resultSet = statement.executeUpdate(insertQuery);
	        						iLogger.info("insert query resultSet for premFlag = 1: "+resultSet);
	        						if(resultSet == 1){
	        							updateQuery = "update asset set PremFlag= '"+premiumFlag+"' where serial_number = ('"+machines.get(i).get("vin")+"')";	
	        							statement.executeUpdate(updateQuery);
	        							iLogger.info("updateQuery query for premFlag = 1: "+updateQuery);

	        						}
	        					}else {
	        						//insertQuery = "insert into LLPremiumSubs (serial_number,NoOfyears,UpdatedOn,UpdatedBy,LLPremStartDate,LLPremEndDate) values ('"+machines.get(i).get("vin")+"','"+machines.get(i).get("noOfYrs")+"','"+createdTimeInString+"','"+userId+"','"+machines.get(i).get("premiumStartDate")+"','"+machines.get(i).get("premiumEndDate")+"')";
	        						insertQuery = "insert into LLPremiumSubs (serial_number,NoOfyears,UpdatedOn,UpdatedBy,LLPremStartDate,LLPremEndDate,LLPremPunchingDate) values ('"+machines.get(i).get("vin")+"','"+machines.get(i).get("noOfYrs")+"','"+createdTimeInString+"','"+userId+"','"+machines.get(i).get("premiumStartDate")+"','"+machines.get(i).get("premiumEndDate")+"','"+machines.get(i).get("premiumPunchingDate")+"')";
	        						iLogger.info("insert query for premFlag = 1 with future start date: "+insertQuery);
	        						resultSet = statement.executeUpdate(insertQuery);
	        						machines.remove(i);
	        					}
	        				}else {
	        					insertQuery = "insert into LLPremiumSubs (serial_number,NoOfyears,UpdatedOn,UpdatedBy,LLPremPunchingDate) values ('"+machines.get(i).get("vin")+"','"+machines.get(i).get("noOfYrs")+"','"+createdTimeInString+"','"+userId+"','"+machines.get(i).get("premiumPunchingDate")+"')";
	        					iLogger.info("insert query for premFlag = 1: "+insertQuery);
	        					resultSet = statement.executeUpdate(insertQuery);
	        					iLogger.info("insert query resultSet for premFlag = 1: "+resultSet);
	        					if(resultSet == 1){
	        						updateQuery = "update asset set PremFlag= '"+premiumFlag+"' where serial_number = ('"+machines.get(i).get("vin")+"')";	
	        						statement.executeUpdate(updateQuery);
	        						iLogger.info("updateQuery query for premFlag = 1: "+updateQuery);
	        					}
	        				}
	        			}
	        		}else if(premiumFlag.equalsIgnoreCase("0")){
	        			insertQuery = "insert into LLPremiumSubs (serial_number,NoOfyears,UpdatedOn,UpdatedBy,LLPremStartDate,LLPremEndDate,LLPremPunchingDate) values ('"+machines.get(i).get("vin")+"','0','"+createdTimeInString+"','"+userId+"','"+machines.get(i).get("premiumStartDate")+"','"+createdTimeInString.substring(0, 10)+"' ,'"+createdTimeInString+"')";
		        		iLogger.info("insert query for premFlag = 0: "+insertQuery);
		        		resultSet = statement.executeUpdate(insertQuery);
		        		iLogger.info("insert query resultSet for premFlag = 0: "+resultSet);
		        		if(resultSet == 1){
		        			
		        			updateQuery = "update asset set PremFlag= '"+premiumFlag+"' where serial_number = ('"+machines.get(i).get("vin")+"')";	
			        		statement.executeUpdate(updateQuery);
			        		iLogger.info("updateQuery query for premFlag = 0: "+updateQuery);
		        		}
	        		}
	        		
	        	}
	        }catch(Exception e){
	        	e.printStackTrace();
	        	fLogger.fatal("Exception occurred while performing db operation in LLPremiumServiceImpl setSubscription: "+e.getMessage());
	        	return "failure";
	        }
			
		}catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception occurred while setSubscription in LLPremiumServiceImpl setSubscription: "+e.getMessage());
			return "failure";
		}
		
		if(machines.size() > 0){
			//Push to rabbit MQ .sn
			new PremiumMachinesKafkaDataProducerForRabbitMq(machines);
			//Push to rabbit MQ .en
			
			//MDA service call here to update redis.s
			setMongoPremiumStatus(machines);
			//MDA service call here to update redis.n
		}
		return "success";
		
	}
	
	public String sendEmailToDealer(String vin){
		String selectQuery = "select Email_ID from account where account_id in (select account_id from asset_owner_snapshot where Account_Type = 'Dealer' and serial_number = '"+vin+"') order by Email_ID Desc";
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet=null;
		List<HashMap<String, String>> mapList = new ArrayList<>();
		String emailId = "";
		
		try{
			
			ConnectMySQL connectionObj = new ConnectMySQL();
			con = connectionObj.getConnection();
			statement = con.createStatement();
			
			
			resultSet = statement.executeQuery(selectQuery);
			resultSet.first();
			emailId = resultSet.getString("Email_ID");
			//while(resultSet.next())
//			{
//				emailId = resultSet.getString("Email_ID");
//			}
			iLogger.info("dealer email id for LLPremium : "+emailId);
			
		}catch(Exception ex)
		{
			
			ex.printStackTrace();
			fLogger.fatal("Exception occurred while sendEmailToDealer in LLPremiumServiceImpl: "+ex.getMessage()+" vin: "+vin);
			
		}finally{
			if(resultSet!=null)
				try {
					resultSet.close();
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

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		String emailSubject = "Interested in LL Premium subscription";
		String emailBody = "The customer against the VIN "+vin+" has shown interest in purchasing the premium subscription. Please do reach out to the user for more details.";
		Timestamp createdTimestamp = new Timestamp(new Date().getTime());
		String transactionTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdTimestamp.getTime());
		SendEmailWithKafka sendEmail = new SendEmailWithKafka();
		iLogger.info("sendEmailToDealer: emailID= "+emailId+" vin: "+vin);
		String result = sendEmail.sendMailToDealer(emailId, emailSubject, emailBody, vin, transactionTime);
		
		iLogger.info("sendEmailToDealer result: "+result);
		
		if(result.equalsIgnoreCase("FAILURE")){
			iLogger.info("sendEmailToDealer failed for vin: "+vin);
			return "failure";
		}
		iLogger.info("sendEmailToDealer success for vin: "+vin);
		return  "success";
	}
	

	
	public String removeSubscription(String date){
		
		String selectQuery = "select a.serial_number,a.premFlag,llps1.LLPremStartDate,llps1.LLPremEndDate from (select serial_number,premFlag from asset where premFlag = '1') as a"
							+" inner join (select llps.serial_number as serial_number, llps.updatedOn, max(llps.LLPremStartDate) as LLPremStartDate,max(llps.LLPremEndDate) as LLPremEndDate from LLPremiumSubs AS llps, (select serial_number, max(updatedOn) as updatedOn from LLPremiumSubs AS llps group by serial_number) as temp where llps.serial_number=temp.serial_number and llps.updatedOn = temp.updatedOn group by llps.serial_number) as llps1 ON a.serial_number = llps1.serial_number";
		
		List<HashMap<String, String>> premiumMapList = new ArrayList<>();
		List<HashMap<String, String>> premiumMDAMapList = new ArrayList<>();
		ResultSet resultSet=null;

		
		try{
		Timestamp createdTimestamp = new Timestamp(new Date().getTime());
		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat date_format2 = new SimpleDateFormat("yyyy-MM-dd");
		String createdTimeInString = date_format.format(createdTimestamp.getTime());
		//Date currentDate = date_format.parse(createdTimeInString);
		Date currentDate = date_format2.parse(date);
		
		Date endDate = null;
		String updateQuery = "";
		String insertQuery = "";
		
		ConnectMySQL connectionObj = new ConnectMySQL();
        try (Connection connection = connectionObj.getConnection();
                Statement statement = connection.createStatement()) {
        	
        	resultSet = statement.executeQuery(selectQuery);
    		HashMap<String, String> tableMap = null;
    		
    		while(resultSet.next())
			{
    			
    			endDate = date_format2.parse(resultSet.getString("LLPremEndDate").substring(0, 10));
    			
    			if(currentDate.after(endDate)){
    				tableMap = new HashMap<String,String>();
    				tableMap.put("vin", resultSet.getString("serial_number"));
    				tableMap.put("premiumFlag", "0");
    				tableMap.put("premiumStartDate", resultSet.getString("LLPremStartDate"));
    				tableMap.put("premiumEndDate", createdTimeInString.substring(0, 10));
    				premiumMapList.add(tableMap);
    			}
	
			}
    		
    		if(premiumMapList.size() > 0){
    			for(int i=0;i<premiumMapList.size();i++){
	        		String premiumFlag = premiumMapList.get(i).get("premiumFlag");
	        		
	        			updateQuery = "update asset set PremFlag= '"+premiumFlag+"' where serial_number = ('"+premiumMapList.get(i).get("vin")+"')";	
	        			statement.addBatch(updateQuery);
		        		iLogger.info("removeSubscription updateQuery query for premFlag = 0: "+updateQuery);
//		        		insertQuery = "insert into LLPremiumSubs (serial_number,NoOfyears,UpdatedOn,UpdatedBy,LLPremStartDate,LLPremEndDate) values ('"+premiumMapList.get(i).get("vin")+"','0','"+createdTimeInString+"','batchUser','"+premiumMapList.get(i).get("premiumStartDate")+"','"+premiumMapList.get(i).get("premiumEndDate")+"')";
//			        	statement.addBatch(insertQuery);
		        		//iLogger.info("insert query for premFlag = 0: "+insertQuery);
		        		premiumMDAMapList.add(premiumMapList.get(i));
		        		if((i+1)%10 == 0){
		        			//This block is to push 10 records at a time to MDA and rabbit MQ
		        			statement.executeBatch();
		        			
		        			//Push to rabbit MQ .sn
		        			
		        			 new PremiumMachinesKafkaDataProducerForRabbitMq(premiumMDAMapList);
		        			 iLogger.info("PremiumMachinesKafkaDataProducerForRabbitMq inside if condition to push 10 records : ");
		        			 
		        			//call MDA reports here
		        			 
		        			setMongoPremiumStatus(premiumMDAMapList);
		        			iLogger.info("removeSubscription setMongoPremiumStatus inside if condition to push 10 records : ");
		        			iLogger.info("removeSubscription premiumMDAMapList inside if condition before clear : "+premiumMDAMapList);
		        			premiumMDAMapList.clear();
		        			iLogger.info("removeSubscription premiumMDAMapList inside if condition after clear : "+premiumMDAMapList);
		        		}
	        		
	        	}
    			
    			if(premiumMDAMapList.size() > 0) {
	    			 statement.executeBatch();
	    			
	    			//Push to rabbit MQ .sn
	    			new PremiumMachinesKafkaDataProducerForRabbitMq(premiumMDAMapList);
	    			iLogger.info("PremiumMachinesKafkaDataProducerForRabbitMq outside loop: ");
	    			 
	    			//call MDA reports here
	    			setMongoPremiumStatus(premiumMDAMapList);
	    			iLogger.info("setMongoPremiumStatus outside loop condition: ");
	    			iLogger.info("premiumMDAMapList outside loop condition before clear : "+premiumMDAMapList);
	    			premiumMDAMapList.clear();
	    			iLogger.info("premiumMDAMapList outside loop condition after clear : "+premiumMDAMapList);
    			}
    		}
        	
        	
        }catch(Exception e){
        	e.printStackTrace();
        	fLogger.fatal("Exception occurred while performing db operation in LLPremiumServiceImpl removeSubscription: "+e.getMessage());
        	return "failure";
        }
		
	}catch (Exception e) {
		e.printStackTrace();
		fLogger.fatal("Exception occurred while removeSubscription in LLPremiumServiceImpl setSubscription: "+e.getMessage());
		return "failure";
	}
	
		
		return "success";
	}
	
	
	
public String batchUpdateSubscription(String date){
		
		String selectQuery = "select a.serial_number,llps1.LLPremStartDate,llps1.LLPremEndDate from (select serial_number from asset where premFlag = '0') as a"
							+" inner join (select llps.serial_number as serial_number, llps.updatedOn, max(llps.LLPremStartDate) as LLPremStartDate,max(llps.LLPremEndDate) as LLPremEndDate from LLPremiumSubs AS llps, (select serial_number, max(updatedOn) as updatedOn from LLPremiumSubs AS llps group by serial_number) as temp where llps.serial_number=temp.serial_number and llps.updatedOn = temp.updatedOn group by llps.serial_number) as llps1 ON a.serial_number = llps1.serial_number";
		
		
		iLogger.info("select query query for batchUpdateSubscription: "+selectQuery);
		List<HashMap<String, String>> premiumMapList = new ArrayList<>();
		List<HashMap<String, String>> premiumMDAMapList = new ArrayList<>();
		ResultSet resultSet=null;

		
		try{
		Timestamp createdTimestamp = new Timestamp(new Date().getTime());
		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat date_format2 = new SimpleDateFormat("yyyy-MM-dd");
		String createdTimeInString = date_format.format(createdTimestamp.getTime());
		Date currentDate = date_format2.parse(date);
		
		Date startDate = null;
		Date endDate = null;
		String updateQuery = "";
		String insertQuery = "";
		
		ConnectMySQL connectionObj = new ConnectMySQL();
        try (Connection connection = connectionObj.getConnection();
                Statement statement = connection.createStatement()) {
        	
        	resultSet = statement.executeQuery(selectQuery);
    		HashMap<String, String> tableMap = null;
    		
    		while(resultSet.next())
			{
    			
    			startDate = date_format2.parse(resultSet.getString("LLPremStartDate").substring(0,10));
    			
    			endDate = date_format2.parse(resultSet.getString("LLPremendDate").substring(0,10));
    			long timeDiff = Math.abs(startDate.getTime()-endDate.getTime());
				long noOfdaysBetween = (TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS));
    			if(currentDate.equals(startDate) && (noOfdaysBetween == 365) ){			//start end date should differ by 365 to avoid picking closed subscription within same day.
    				tableMap = new HashMap<String,String>();
    				tableMap.put("vin", resultSet.getString("serial_number"));
    				tableMap.put("premiumFlag", "1");
    				tableMap.put("premiumStartDate", resultSet.getString("LLPremStartDate"));
    				tableMap.put("premiumEndDate", resultSet.getString("LLPremEndDate"));
    				premiumMapList.add(tableMap);
    			}
				
				
			}
    		
    		if(premiumMapList.size() > 0){
    			for(int i=0;i<premiumMapList.size();i++){
	        		String premiumFlag = premiumMapList.get(i).get("premiumFlag");
	        		
	        			updateQuery = "update asset set PremFlag= '"+premiumFlag+"' where serial_number = ('"+premiumMapList.get(i).get("vin")+"')";	
	        			statement.addBatch(updateQuery);
		        		iLogger.info("batchUpdateSubscription updateQuery query for premFlag = 1: "+updateQuery);
		        		
		        		premiumMDAMapList.add(premiumMapList.get(i));
		        		if((i+1)%10 == 0){
		        			//This block is to push 10 records at a time to MDA and rabbit MQ
		        			statement.executeBatch();
		        			
		        			//Push to rabbit MQ .sn
		        			
		        			 new PremiumMachinesKafkaDataProducerForRabbitMq(premiumMDAMapList);
		        			 iLogger.info("PremiumMachinesKafkaDataProducerForRabbitMq inside if condition to push 10 records : ");
		        			 
		        			//call MDA reports here
		        			 
		        			setMongoPremiumStatus(premiumMDAMapList);
		        			iLogger.info("batchUpdateSubscription setMongoPremiumStatus inside if condition to push 10 records : ");
		        			iLogger.info("batchUpdateSubscription premiumMDAMapList inside if condition before clear : "+premiumMDAMapList);
		        			premiumMDAMapList.clear();
		        			iLogger.info("batchUpdateSubscription premiumMDAMapList inside if condition after clear : "+premiumMDAMapList);
		        		}
	        		
	        	}
    			
    			if(premiumMDAMapList.size() > 0){
	    			 statement.executeBatch();
	    			
	    			//Push to rabbit MQ .sn
	    			new PremiumMachinesKafkaDataProducerForRabbitMq(premiumMDAMapList);
	    			iLogger.info("batchUpdateSubscription PremiumMachinesKafkaDataProducerForRabbitMq outside loop: ");
	    			 
	    			//call MDA reports here
	    			setMongoPremiumStatus(premiumMDAMapList);
	    			iLogger.info("batchUpdateSubscription setMongoPremiumStatus outside loop condition: ");
	    			iLogger.info("batchUpdateSubscription premiumMDAMapList outside loop condition before clear : "+premiumMDAMapList);
	    			premiumMDAMapList.clear();
	    			iLogger.info("batchUpdateSubscription premiumMDAMapList outside loop condition after clear : "+premiumMDAMapList);
    			}
    		}
        	
        	
        }catch(Exception e){
        	e.printStackTrace();
        	fLogger.fatal("Exception occurred while performing db operation in LLPremiumServiceImpl removeSubscription: "+e.getMessage());
        	return "failure";
        }
		
	}catch (Exception e) {
		e.printStackTrace();
		fLogger.fatal("Exception occurred while removeSubscription in LLPremiumServiceImpl setSubscription: "+e.getMessage());
		return "failure";
	}
	
		
		return "success";
	}
	
	
	public String getPremiumMachinesCount(String tenancyId){
		String selectQuery = "select count(*) as machineCount from (select serial_number,Asset_Type_ID,Asset_Group_ID from asset_owner_snapshot where account_id in (select account_id from account_tenancy where tenancy_id in ("+tenancyId+"))) as aos1" 
							+" inner join (select * from asset where premFlag = 1) as a ON a.serial_number = aos1.serial_number";
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet=null;
		String result = "";
		HashMap<String, String> resultMap = null;
		try{
			
			ConnectMySQL connectionObj = new ConnectMySQL();
			con = connectionObj.getConnection();
			statement = con.createStatement();
			
			
			resultSet = statement.executeQuery(selectQuery);
			iLogger.info("getPremiumMachinesCount select Query : "+selectQuery);
			while(resultSet.next())
			{
				result = resultSet.getString("machineCount");
			}
			iLogger.info("premum machines count : "+result);
			
		}catch(Exception ex)
		{
			
			ex.printStackTrace();
			fLogger.fatal("Exception occurred while getPremiumMachinesCount in LLPremiumServiceImpl: "+ex.getMessage());
			
		}finally{
			if(resultSet!=null)
				try {
					resultSet.close();
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

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	
	public String getPremiumStatus(String tenancyId,String vin){
		String selectQuery = "select a.premFlag as status,aos1.serial_number from (select serial_number,Asset_Type_ID,Asset_Group_ID from asset_owner_snapshot where account_id in (select account_id from account_tenancy where tenancy_id in ("+tenancyId+"))) as aos1" 
							+" inner join (select * from asset where serial_number = '"+vin+"') as a ON a.serial_number = aos1.serial_number"
							+" inner join (select * from asset_type where Asset_Type_Code in (select Asset_Type_Code from LLPremiumModels)) at ON aos1.Asset_Type_ID = at.Asset_Type_ID";
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet=null;
		String result = "invalid";
		HashMap<String, String> resultMap = null;
		try{
			iLogger.info("getPremiumStatus select Query : "+selectQuery);
			ConnectMySQL connectionObj = new ConnectMySQL();
			con = connectionObj.getConnection();
			statement = con.createStatement();
			
			
			resultSet = statement.executeQuery(selectQuery);
			while(resultSet.next())
			{
				if(resultSet.getString("serial_number") == null || resultSet.getString("serial_number") == ""){
					return result;
				}
				result = resultSet.getString("status");
			}
			iLogger.info("premum status : "+result);
			
		}catch(Exception ex)
		{
			
			ex.printStackTrace();
			fLogger.fatal("Exception occurred while getPremiumStatus in LLPremiumServiceImpl: "+ex.getMessage());
			
		}finally{
			if(resultSet!=null)
				try {
					resultSet.close();
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

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public void setMongoPremiumStatus(List<HashMap<String, String>> premiumMDAMapList){
		
		URL url = null;
		String connIP = null;
		String connPort = null;
		iLogger.info("cam into MDA redis update");
		String jsonDataString = null;
		//String moolDaUrl = "http://10.210.196.206:26030/MoolDA/LLIPremiumService/setLLIPremiumStatus";
		Properties prop = null;
		try {
			prop = CommonUtil.getDepEnvProperties();
			connIP = prop.getProperty("MDA_ServerIP");
			connPort = prop.getProperty("MDA_ServerPort");
			iLogger.info("setMongoPremiumStatus: MoolDa_ServerIP:" + connIP + " :: MDA_ServerPort:" + connPort);
		} catch (Exception e) {
			fLogger.fatal("setMongoPremiumStatus: : "
					+ "Exception in getting Server Details for Moolda Layer from properties file: " + e);
		}
		String moolDaUrl = "http://" + connIP + ":" + connPort + "/MoolDA/LLIPremiumService/setLLIPremiumStatus";
		
		try {
			
			
			try {
				jsonDataString = new ObjectMapper().writer().writeValueAsString(premiumMDAMapList);
				
				iLogger.info("MDA redis update jsonString: "+jsonDataString);
			} catch (IOException e) {
				fLogger.fatal("Exception occurred while converting to json string from premium flag set json data: "+e.getMessage());
				e.printStackTrace();
			}
			iLogger.info("MDA redis update url: "+moolDaUrl);
			url = new URL(moolDaUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "text/plain");
			OutputStream os = (OutputStream) conn.getOutputStream();
			os.write(jsonDataString.getBytes());
			os.flush();

			if (conn.getResponseCode() != 200) {
				iLogger.info("Response status failed for MDA redis update");
//				iLogger.info("Calling PremiumMachinesDataProducer for publishing the data to kafka topic -------> START");
//				new PremiumMachinesKafkaDataProducer(jsonDataString);
//				iLogger.info("Calling PremiumMachinesDataProducer for publishing the data to kafka topic  -------> END");
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			// Reading data's from url
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			String out = "";
			while ((output = br.readLine()) != null) {
				out += output;
			}
			conn.disconnect();
			
			iLogger.info("Result after sending data to MDA: "+out);
			
			if(out.equalsIgnoreCase("failure")){
				iLogger.info("Response received from MDA report redis update response is failure");
				iLogger.info("Calling PremiumMachinesDataProducer for publishing the data to kafka topic -------> START");
				new PremiumMachinesKafkaDataProducer(jsonDataString);
				iLogger.info("Calling PremiumMachinesDataProducer for publishing the data to kafka topic  -------> END");
			}
			
			
		} catch (Exception e) {
			iLogger.info("Calling PremiumMachinesDataProducer from catch block for publishing the data to kafka topic -------> START");
			new PremiumMachinesKafkaDataProducer(jsonDataString);
			iLogger.info("Calling PremiumMachinesDataProducer from catch block for publishing the data to kafka topic  -------> END");
			fLogger.error("Exception occured while updating data to MDA redis" +e.getMessage() );
			e.printStackTrace();
		}
	}
	
	public List<String> getPremiumModelsList(){
		String selectQuery = "select Asset_Type_Code from LLPremiumModels";
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet=null;
		List<String> modelList = new ArrayList<>();
		try{
		
		ConnectMySQL connectionObj = new ConnectMySQL();
		con = connectionObj.getConnection();
		statement = con.createStatement();
		
		
		resultSet = statement.executeQuery(selectQuery);
		while(resultSet.next())
		{
			if(resultSet.getString("Asset_Type_Code") != null){
				modelList.add(resultSet.getString("Asset_Type_Code"));
			}
		}
		iLogger.info("premum machines models List : "+modelList.toString());
		
		}catch(Exception ex)
		{
		
		ex.printStackTrace();
		fLogger.fatal("Exception occurred while getPremiumModels in LLPremiumServiceImpl: "+ex.getMessage());
		
		}finally{
			if(resultSet!=null)
				try {
					resultSet.close();
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
			
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return modelList;
		}
	
	public Map<String , String>  getPremiumModelsNamesList(int profileID){
		String selectQuery = "select at.Asset_Type_Name , at.Asset_Type_Code from LLPremiumModels lp  , asset_type at where lp.Asset_Type_Code = at.Asset_Type_Code and at.Asset_Group_ID = "+profileID;
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet=null;
		Map<String , String> modelMap = new HashMap<>();
		try{
		
		ConnectMySQL connectionObj = new ConnectMySQL();
		con = connectionObj.getConnection();
		statement = con.createStatement();
		
		
		resultSet = statement.executeQuery(selectQuery);
		while(resultSet.next())
		{
			String modelcodes = null;
			String modelNames = null;
			if(resultSet.getString("Asset_Type_Code") != null){
				modelcodes = resultSet.getString("Asset_Type_Code");
			}
			if(resultSet.getString("Asset_Type_Name") != null){
				modelNames = resultSet.getString("Asset_Type_Name");
			}
			
			modelMap.put(modelcodes, modelNames);
		}
		iLogger.info("premium machines models List : "+modelMap.toString());
		
		}catch(Exception ex)
		{
		
		ex.printStackTrace();
		fLogger.fatal("Exception occurred while getPremiumModelsNamesList in LLPremiumServiceImpl: "+ex.getMessage());
		
		}finally{
			if(resultSet!=null)
				try {
					resultSet.close();
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
			
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return modelMap;
		}


	public LinkedHashMap<Integer ,String> getDelearNames(){ //CR426.sn
		String selectQuery = "select a.account_name ,t.tenancy_id , a.Account_Code from tenancy t inner join account_tenancy at on at.tenancy_id = t.tenancy_id inner join account a on a.account_id = at.account_id where t.tenancy_type_id = 3 and a.status =1";
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet=null;
		LinkedHashMap<Integer, String> customerMap =  new LinkedHashMap<Integer, String>();
		try{
		
		ConnectMySQL connectionObj = new ConnectMySQL();
		con = connectionObj.getConnection();
		statement = con.createStatement();
		
		iLogger.info("getDelearNames List query : "+selectQuery);
		resultSet = statement.executeQuery(selectQuery);
		while(resultSet.next())
		{
			int tenancyId = 0;
			String accountName = null;
			String accountCode = null;
			if(resultSet.getString("Tenancy_ID") != null){
				tenancyId = resultSet.getInt("Tenancy_ID");
			}
			if(resultSet.getString("account_name") != null){
				accountName = resultSet.getString("account_name").split("-")[0];
			}
			if(resultSet.getString("Account_Code") != null){
				accountCode = resultSet.getString("Account_Code");
			}
			accountName = accountName+"-" +accountCode;
			customerMap.put(tenancyId, accountName.trim());
			
		}
	//	iLogger.info("delear Listt : "+customerMap.toString());
		
		}catch(Exception ex)
		{
		
		ex.printStackTrace();
		fLogger.fatal("Exception occurred while getDelearNames in LLPremiumServiceImpl: "+ex.getMessage());
		
		}finally{
			if(resultSet!=null)
				try {
					resultSet.close();
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
			
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return customerMap;
		}

	public List<LinkedHashMap<String, String>> getSubPremiumReport(String startDate , String endDate,String dateType){
//		 String query = "select come.Serial_Number ,Profile ,Model , Zone ,Dealer_Name,CustomerName,Customer_Mobile ,Comm_City,Comm_District,Comm_State, Comm_Address,Installed_date,Pkt_Created_TS,Pkt_Recd_TS, Roll_Off_Date , lAT ,lON ,Renewal_date ,Renew_state,lp.LLPremStartdate,lp.LLPremEnddate from LLPremiumSubs lp ,"
//				+ " com_rep_oem_enhanced come where lp.Serial_Number = come.Serial_Number and LLPremStartdate >= '"+startDate+"' and LLPremStartdate <= '"+endDate+"' " ;
		
//		String query = " select max(lp.UpdatedOn) ,come.Serial_Number ,Profile ,Model , Zone ,Dealer_Name,CustomerName,Customer_Mobile ,Comm_City,Comm_District,Comm_State, Comm_Address,Installed_date,Pkt_Created_TS,Pkt_Recd_TS, Roll_Off_Date , lAT ,lON ,Renewal_date ,Renew_state,lp.LLPremStartdate,lp.LLPremEnddate from LLPremiumSubs lp , com_rep_oem_enhanced come where lp.Serial_Number = come.Serial_Number and NoOfyears = 1 and LLPremStartdate >= '"+startDate+"' and LLPremStartdate <= '"+endDate+"' group by lp.Serial_Number";
		
		String query = "SELECT " + 
		        "    lp.serial_number," + 
		        "    MAX(lp.UpdatedOn) AS updated_on," + 
		        "    DATE_FORMAT(MAX(lp.LLPremStartdate), '%Y-%m-%d') AS LLPremStartdate," + 
		        "    DATE_FORMAT(MAX(lp.LLPremEnddate), '%Y-%m-%d') AS LLPremEnddate," + 
		        "    ANY_VALUE(Model) AS Model," + 
		        "    ANY_VALUE(Profile) AS Profile," + 
		        "    ANY_VALUE(Zone) AS Zone," + 
		        "    ANY_VALUE(Dealer_Name) AS Dealer_Name," + 
		        "    ANY_VALUE(CustomerName) AS CustomerName," + 
		        "    ANY_VALUE(Customer_Mobile) AS Customer_Mobile," + 
		        "    ANY_VALUE(Comm_City) AS Comm_City," + 
		        "    ANY_VALUE(Comm_District) AS Comm_District," + 
		        "    ANY_VALUE(Comm_State) AS Comm_State," + 
		        "    ANY_VALUE(Comm_Address) AS Comm_Address," + 
		        "    ANY_VALUE(Installed_date) AS Installed_date," + 
		        "    ANY_VALUE(Pkt_Created_TS) AS Pkt_Created_TS," + 
		        "    ANY_VALUE(Pkt_Recd_TS) AS Pkt_Recd_TS," + 
		        "    ANY_VALUE(Roll_Off_Date) AS Roll_Off_Date," + 
		        "    ANY_VALUE(lAT) AS lAT," + 
		        "    ANY_VALUE(lON) AS lON," + 
		        "    ANY_VALUE(Renewal_date) AS Renewal_date," + 
		        "    ANY_VALUE(Renew_state) AS Renew_state," + 
		        "    DATE_FORMAT(ANY_VALUE(com.SaleDate), '%Y-%m-%d') AS SaleDate, "+
		        "    DATE_FORMAT(ANY_VALUE(lp.LLPremPunchingDate), '%Y-%m-%d') AS LLPremPunchingDate" + 
		        " FROM" + 
		        "    LLPremiumSubs lp," + 
		        "    (SELECT " + 
		        "        serial_number, MAX(UpdatedOn) AS updatedOn" + 
		        "    FROM" + 
		        "        LLPremiumSubs" + 
		        "    GROUP BY serial_number) lp1," + 
		        "    com_rep_oem_enhanced com" + 
		        " WHERE" + 
		        "    lp.Serial_Number = com.Serial_Number" + 
		        "        AND lp.serial_number = lp1.serial_number" + 
		        "        AND lp.UpdatedOn = lp1.updatedOn" + 
		        "        AND lp.NoOfyears > 0";

		if (startDate != null && endDate != null && !endDate.isEmpty() && !startDate.isEmpty() && endDate.compareTo(startDate) >= 0) {
		    if ("LLpunchingDate".equalsIgnoreCase(dateType)) {
		        query += "  AND lp.LLPremPunchingDate >= '" + startDate + "' " +
		                 "  AND lp.LLPremPunchingDate <= '" + endDate + "' ";
		    } else if ("LLpremDate".equalsIgnoreCase(dateType)) {
		        query += "  AND lp.LLPremStartdate >= '" + startDate + "' " +
		                 "  AND lp.LLPremStartdate <= '" + endDate + "' ";
		    }
		}

		query += "  GROUP BY lp.serial_number";

		Connection con = null;
		Statement statement = null;
		ResultSet resultSet=null;
		List<LinkedHashMap<String, String>> subLLReportList =  new ArrayList<>();
		try{
		
		ConnectMySQL connectionObj = new ConnectMySQL();
		con = connectionObj.getConnection();
		statement = con.createStatement();
		
		iLogger.info("getSubPremiumReport List query : "+query);
		resultSet = statement.executeQuery(query);
		while(resultSet.next())
		{
			LinkedHashMap<String, String> customerMap =  new LinkedHashMap<String, String>();
			
			if(resultSet.getString("Serial_Number") != null){
				customerMap.put("Serial_Number", resultSet.getString("Serial_Number"));
			}else{
				customerMap.put("Serial_Number", "NA");
			}
			if(resultSet.getString("Profile") != null){
				customerMap.put("Profile", resultSet.getString("Profile"));
			}else{
				customerMap.put("Profile", "NA");
			}
			
			if(resultSet.getString("Model") != null){
				customerMap.put("Model", resultSet.getString("Model"));
			}else{
				customerMap.put("Model", "NA");
			}
			if(resultSet.getString("Zone") != null){
				customerMap.put("Zone", resultSet.getString("Zone"));
			}else{
				customerMap.put("Zone", "NA");
			}
			
			if(resultSet.getString("Dealer_Name") != null){
				customerMap.put("Dealer_Name", resultSet.getString("Dealer_Name"));
			}else{
				customerMap.put("Dealer_Name", "NA");
			}
			if(resultSet.getString("CustomerName") != null){
				customerMap.put("CustomerName", resultSet.getString("CustomerName"));
			}else{
				customerMap.put("CustomerName", "NA");
			}
			if(resultSet.getString("Customer_Mobile") != null){
				customerMap.put("Customer_Mobile", resultSet.getString("Customer_Mobile"));
			}else{
				customerMap.put("Customer_Mobile", "NA");
			}
			
			
			
			if(resultSet.getString("Comm_City") != null){
				customerMap.put("VIN City", resultSet.getString("Comm_City"));
			}else{
				customerMap.put("VIN City", "NA");
			}
			if(resultSet.getString("Comm_District") != null){
				customerMap.put("VIN District", resultSet.getString("Comm_District"));
			}else{
				customerMap.put("VIN District", "NA");
			}
			if(resultSet.getString("Comm_State") != null){
				customerMap.put("VIN State", resultSet.getString("Comm_State"));
			}else{
				customerMap.put("VIN State", "NA");
			}if(resultSet.getString("Comm_Address") != null){
				customerMap.put("VIN Address", resultSet.getString("Comm_Address"));
			}else{
				customerMap.put("VIN Address", "NA");
			}
			
			
			if(resultSet.getString("Installed_date") != null){
				customerMap.put("Installed_date", resultSet.getString("Installed_date").split("\\.")[0]);
			}else{
				customerMap.put("Installed_date", "NA");
			}
			if(resultSet.getString("Pkt_Created_TS") != null){
				customerMap.put("Pkt_Created_TS", resultSet.getString("Pkt_Created_TS").split("\\.")[0]);
			}else{
				customerMap.put("Pkt_Created_TS", "NA");
			}
			
			if(resultSet.getString("Pkt_Recd_TS") != null){
				customerMap.put("Pkt_Recd_TS", resultSet.getString("Pkt_Recd_TS").split("\\.")[0]);
			}else{
				customerMap.put("Pkt_Recd_TS", "NA");
			}
			if(resultSet.getString("Roll_Off_Date") != null){
				customerMap.put("Roll_Off_Date", resultSet.getString("Roll_Off_Date").split("\\.")[0]);
			}else{
				customerMap.put("Roll_Off_Date", "NA");
			}
			
			if(resultSet.getString("LAT") != null){
				customerMap.put("LAT", resultSet.getString("LAT"));
			}else{
				customerMap.put("LAT", "NA");
			}
			if(resultSet.getString("LON") != null){
				customerMap.put("LON", resultSet.getString("LON"));
			}else{
				customerMap.put("LON", "NA");
			}
			
			if(resultSet.getString("Renewal_date") != null){
				customerMap.put("Renewal_date", resultSet.getString("Renewal_date").split("\\.")[0]);
			}else{
				customerMap.put("Renewal_date", "NA");
			}
			if(resultSet.getString("Renew_state") != null){
				customerMap.put("Renew status", resultSet.getString("Renew_state"));
			}else{
				customerMap.put("Renew status", "NA");
			}
			
			if(resultSet.getString("LLPremStartdate") != null){
				customerMap.put("LLPremStartdate", resultSet.getString("LLPremStartdate").split("\\.")[0]);
			}else{
				customerMap.put("LLPremStartdate", "NA");
			}
			if(resultSet.getString("LLPremEnddate") != null){
				customerMap.put("LLPremEnddate", resultSet.getString("LLPremEnddate").split("\\.")[0]);
			}else{
				customerMap.put("LLPremEnddate", "NA");
			}
			
			if(resultSet.getString("LLPremPunchingDate") != null){
				customerMap.put("LLPremPunchingDate", resultSet.getString("LLPremPunchingDate").split("\\.")[0]);
			}else{
				customerMap.put("LLPremPunchingDate", "NA");
			}
			if(resultSet.getString("SaleDate")!=null)
			{
				customerMap.put("SaleDate", resultSet.getString("SaleDate").split("\\.")[0]);
			}
			else
			{
				customerMap.put("SaleDate", "NA");
			}
		
			subLLReportList.add(customerMap);
		}
		
		}catch(Exception ex)
		{
		
		ex.printStackTrace();
		fLogger.fatal("Exception occurred while getDelearNames in LLPremiumServiceImpl: "+ex.getMessage());
		
		}finally{
			if(resultSet!=null)
				try {
					resultSet.close();
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
			
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return subLLReportList;
		}
	
	//Sending VIN list to MDAReports whose Premium Flag is one
	public  List<String>  vinsListForMDAReportsIMPL(String startDate){
		String selectQuery = "select Serial_Number from asset where Serial_Number in ( SELECT Serial_Number FROM LLPremiumSubs where  LLPremStartDate = '"+startDate+"' ) and PremFlag = '1'";
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet=null;
        List<String> vinsList =  new ArrayList<String>();
		try{
		
		ConnectMySQL connectionObj = new ConnectMySQL();
		con = connectionObj.getConnection();
		statement = con.createStatement();
		
		
		resultSet = statement.executeQuery(selectQuery);
		while(resultSet.next())
		{
			StringBuilder sb = new StringBuilder();
			if(resultSet.getString("Serial_Number") != null){
				vinsList.add(resultSet.getString("Serial_Number"));
			}
				
			
		}
		iLogger.info("Sending VIN list to MDAReports whose Premium Flag is one "+vinsList.toString());
		
		}catch(Exception ex)
		{
		
		ex.printStackTrace();
		fLogger.fatal("Exception occurred while Sending VIN list to MDAReports whose Premium Flag is one in LLPremiumServiceImpl: "+ex.getMessage());
		
		}finally{
			if(resultSet!=null)
				try {
					resultSet.close();
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
			
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return vinsList ;
		}


}
