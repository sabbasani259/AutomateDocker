/**
 * JCB417: Prasanna :20230607 : Changes for updating tenancy table
 */
package remote.wise.service.implementation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import com.google.gson.reflect.TypeToken;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;



import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;

/**
 * @author ROOPN5
 *
 */
public class MultipleBPcodeMappingImpl {

	public List<HashMap<String,Object>> getMultipleBPcodeData(String accountCode, int pageNum){



		List<HashMap<String, Object>> respList = new ArrayList<HashMap<String,Object>>();
		Logger fLogger = FatalLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String selectQuery=null;
		int startLimit=0;
		int endLimit=0;

		startLimit=pageNum*50;
		endLimit=50;

		try{
			if(accountCode!=null){
				selectQuery="Select a.* from account a,account_tenancy at,tenancy t,tenancy_type tt where a.Account_ID=at.Account_ID and at.Tenancy_ID=t.Tenancy_ID and " +
						"t.Tenancy_Type_ID=tt.Tenancy_Type_ID and tt.Tenancy_Type_ID=4 and a.mapping_code='"+accountCode+"' group by a.mapping_code";
			}
			else{
				selectQuery="Select a.* from account a,account_tenancy at,tenancy t,tenancy_type tt where a.Account_ID=at.Account_ID and at.Tenancy_ID=t.Tenancy_ID and " +
						"t.Tenancy_Type_ID=tt.Tenancy_Type_ID and tt.Tenancy_Type_ID=4 group by a.mapping_code";

				selectQuery=selectQuery+" limit "+startLimit+","+endLimit+"";
			}



			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(selectQuery);

			while(rs.next()){
				HashMap<String, Object> respObj = new HashMap<String, Object>();

				respObj.put("Account_ID", rs.getString("Account_ID"));
				respObj.put("Account_Name", rs.getString("Account_Name"));
				respObj.put("mapping_code", rs.getString("mapping_code"));


				respList.add(respObj);
			}
		}

		catch(Exception e){
			fLogger.fatal("MultipleBPcodeMappingRESTService:getMultipleBPcodeData:: Exception Caught:"+e.getMessage());
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("MultipleBPcodeMappingRESTService:getMultipleBPcodeData:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("MultipleBPcodeMappingRESTService:getMultipleBPcodeData:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("MultipleBPcodeMappingRESTService:getMultipleBPcodeData:: Exception Caught:"+e.getMessage());
				}
			}
		}

		return respList;

	}

	public List<HashMap<String, Object>> getMappedAccountDataForLLCode(
			String lLCode) {



		List<HashMap<String, Object>> respList = new ArrayList<HashMap<String,Object>>();
		Logger fLogger = FatalLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;



		try{
			//String selectQuery="Select * from account where mapping_code='"+lLCode+"'";

			String selectQuery="Select a.* from account a,account_tenancy at,tenancy t,tenancy_type tt where a.Account_ID=at.Account_ID and at.Tenancy_ID=t.Tenancy_ID and " +
					"t.Tenancy_Type_ID=tt.Tenancy_Type_ID and tt.Tenancy_Type_ID=4 and a.mapping_code='"+lLCode+"' group by a.Account_Code";

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(selectQuery);

			while(rs.next()){
				HashMap<String, Object> respObj = new HashMap<String, Object>();

				respObj.put("Account_ID", rs.getString("Account_ID"));
				respObj.put("Account_Code", rs.getString("Account_Code"));
				respObj.put("Account_Name", rs.getString("Account_Name"));
				respObj.put("mapping_code", rs.getString("mapping_code"));


				respList.add(respObj);
			}
		}

		catch(Exception e){
			fLogger.fatal("MultipleBPcodeMappingRESTService: getMappedAccountDataForLLCode:: Exception Caught:"+e.getMessage());
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("MultipleBPcodeMappingRESTService: getMappedAccountDataForLLCode:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("MultipleBPcodeMappingRESTService: getMappedAccountDataForLLCode:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("MultipleBPcodeMappingRESTService: getMappedAccountDataForLLCode:: Exception Caught:"+e.getMessage());
				}
			}
		}

		return respList;

	}

	
	public String setMappedAccountDataForLLCode(
			LinkedHashMap<String, String> fields) {

		String result="SUCCESS";

		Logger fLogger = FatalLoggerClass.logger;

		Logger iLogger = InfoLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultSet rs1 = null;

		ListToStringConversion conversion = new ListToStringConversion();
		String LLCode=fields.get("LLCode");

		List<String> mappingAccCodeList=Arrays.asList(fields.get("mappingAccCodeList").split(","));
		String mappingAccCodeListAsString=conversion.getStringList(mappingAccCodeList).toString();

		String userID=new CommonUtil().getUserId(fields.get("loginID"));

		if(userID==null){
			return "Invalid loginID";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date currDate=new Date();
		String currentTS=sdf.format(currDate);
		String Tenancy_Code=null;

		List<Integer> tenancyIdlist=new ArrayList<Integer>();
		PreparedStatement pst= null;

		PreparedStatement actPst= null;
		PreparedStatement tenancyPst= null;

		try{

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();

			//STEP 1::Update the account table

			//String accoutUpdateQuery="update account set mapping_code='"+LLCode+"' where Account_Code in ("+mappingAccCodeListAsString+")";

			//DF20181030-KO369761-Bug Fix-updating using mapping code.
			String accoutUpdateQuery="update account set mapping_code=? where mapping_code=?";

			actPst=prodConnection.prepareStatement(accoutUpdateQuery);

			for(int i=0;i<mappingAccCodeList.size();i++){
				actPst.setString(1, LLCode);
				actPst.setString(2, mappingAccCodeList.get(i));
				actPst.addBatch();
			}

			actPst.executeBatch();

			actPst.clearBatch();

			//iLogger.info("MultipleBPcodeMappingRESTService::setMappedAccountDataForLLCode" +"accoutUpdateQuery::"+accoutUpdateQuery);

			//int accountCount=statement.executeUpdate(accoutUpdateQuery);

			//iLogger.info("MultipleBPcodeMappingRESTService::setMappedAccountDataForLLCode" +"accoutUpdateQuery count::"+accountCount);


			//STEP 2::Update the tenancy table

			String query="Select t.Tenancy_Code from account_tenancy at,account a,tenancy t where a.Account_ID=at.Account_ID and at.Tenancy_ID =t.Tenancy_ID and a.Account_Code='"+LLCode+"'";

			iLogger.info("MultipleBPcodeMappingRESTService::setMappedAccountDataForLLCode" +"Tenancy_Code query::"+query);

			if(statement.isClosed()){
				statement= prodConnection.createStatement();
			}

			rs=statement.executeQuery(query);

			if(rs.next()){
				Tenancy_Code=rs.getString("Tenancy_Code");
			}

			iLogger.info("MultipleBPcodeMappingRESTService::setMappedAccountDataForLLCode" +"Tenancy_Code::"+Tenancy_Code);
			String query1="Select at.Tenancy_ID from account_tenancy at,account a where a.Account_ID=at.Account_ID and a.Account_Code in ("+mappingAccCodeListAsString+")";

			iLogger.info("MultipleBPcodeMappingRESTService::setMappedAccountDataForLLCode" +"Tenancy Id query::"+query1);

			if(statement.isClosed()){
				statement= prodConnection.createStatement();
			}
			rs1=statement.executeQuery(query1);

			while(rs1.next()){
				tenancyIdlist.add(rs1.getInt("Tenancy_ID"));
			}



			if(tenancyIdlist.size()>0 && Tenancy_Code!=null){


				/*	String tenancyIdlistAsString=conversion.getStringList(tenancyIdlist).toString();

				iLogger.info("MultipleBPcodeMappingRESTService::setMappedAccountDataForLLCode" +"tenancyIdlist::"+tenancyIdlistAsString);

				String tenancyUpdatequery="Update tenancy set mapping_code='"+Tenancy_Code+"' where Tenancy_ID in("+tenancyIdlistAsString+"')";

				iLogger.info("MultipleBPcodeMappingRESTService::setMappedAccountDataForLLCode" +"tenancyUpdatequery::"+tenancyUpdatequery);

				int tenancyCount=statement.executeUpdate(tenancyUpdatequery);

				iLogger.info("MultipleBPcodeMappingRESTService::setMappedAccountDataForLLCode" +"tenancyUpdatequery count::"+tenancyCount);*/

				String tenancyUpdatequery="update tenancy set mapping_code=? where Tenancy_ID=?";

				tenancyPst=prodConnection.prepareStatement(tenancyUpdatequery);

				for(int i=0;i<tenancyIdlist.size();i++){

					tenancyPst.setString(1, Tenancy_Code);
					tenancyPst.setInt(2, tenancyIdlist.get(i));
					tenancyPst.addBatch();
				}

				tenancyPst.executeBatch();

				tenancyPst.clearBatch();

			}
			//STEP 3::Update the Logging table

			String preparedInsertQuery="Insert into account_mapping_log_data(Account_Mapping_Code,Tenancy_Mapping_Code,Account_Code,Status,Updated_By,Updated_On) "+
					"values(?,?,?,?,?,?)";

			pst = prodConnection.prepareStatement(preparedInsertQuery);

			for(int i=0;i<mappingAccCodeList.size();i++){

				pst.setString(1, LLCode);
				pst.setString(2, Tenancy_Code);
				pst.setString(3, mappingAccCodeList.get(i));
				pst.setInt(4, 1);
				pst.setString(5, userID);
				pst.setString(6, currentTS);

				pst.addBatch();
			}

			pst.executeBatch();

			pst.clearBatch();
			//CR431.sn
			// STEP 4: Update MAlertSubsriberGroup for each AssetID
			String selectQuery = "SELECT AssetID FROM MAlertSubsriberGroup WHERE AssetID IN " +
			                     "(SELECT aos.Serial_Number FROM asset_owner_snapshot aos " +
			                     "JOIN account a ON aos.Account_ID = a.Account_ID WHERE a.mapping_code = ?)";
			iLogger.info(selectQuery);
			PreparedStatement selectStmt = null;
			ResultSet resultSet = null;
			List<String> assetIDs = new ArrayList<>();

			try {
			    // Step 1: Retrieve AssetIDs
			    selectStmt = prodConnection.prepareStatement(selectQuery);
			    selectStmt.setString(1, LLCode);
			    resultSet = selectStmt.executeQuery();

			    while (resultSet.next()) {
			        String assetID = resultSet.getString("AssetID");
			        assetIDs.add(assetID);
			    }

			    iLogger.info("Total AssetIDs found: " + assetIDs.size());

			    // Step 2: Retrieve Contact_ID
			    String selectContactQuery = "SELECT ac.Contact_ID " +
			                                "FROM account_contact ac " +
			                                "JOIN account a ON ac.Account_ID = a.Account_ID " +
			                                "JOIN contact c ON ac.contact_id = c.contact_id " +
			                                "WHERE a.mapping_code = ? AND c.status = 1 AND a.status = 1 " +
			                                "ORDER BY a.account_id DESC";

			    selectStmt = prodConnection.prepareStatement(selectContactQuery);
			    selectStmt.setString(1, LLCode);
			    resultSet = selectStmt.executeQuery();

			    String contactID = null;
			    if (resultSet.next()) {
			        contactID = resultSet.getString("Contact_ID");
			        iLogger.info("Contact_ID found: " + contactID);
			    }

			    int totalRowsAffected = 0;

			    // Step 3: Update MAlertSubsriberGroup for each assetID with Subscriber3 and optionally SMS1 and EMAIL1
			    for (String assetID : assetIDs) {
			        // Step 3.1: Extract Subscriber3
			        String selectSubscriber3Query = "SELECT JSON_UNQUOTE(JSON_EXTRACT(SubscriberGroup, '$.Subscriber3')) AS Subscriber3 " +
			                                        "FROM MAlertSubsriberGroup " +
			                                        "WHERE AssetID = ?";
			        selectStmt = prodConnection.prepareStatement(selectSubscriber3Query);
			        selectStmt.setString(1, assetID);
			        resultSet = selectStmt.executeQuery();

			        String subscriber3 = null;
			        if (resultSet.next()) {
			            subscriber3 = resultSet.getString("Subscriber3");
			            iLogger.info("Subscriber3 found for AssetID " + assetID + ": " + subscriber3);
			        }

			        // Step 3.2: Convert Subscriber3 JSON to Map and extract SMS1 and EMAIL1
			        if (subscriber3 != null) {
			            try {
			                ObjectMapper objectMapper = new ObjectMapper();

			           
			                
			              //  HashMap<String,Object> subscriber3Map = objectMapper.readValue(subscriber3, new TypeReference<Map<String, String>>(){});
			                Map<String, Object> subscriber3Map = objectMapper.readValue(subscriber3, Map.class);


			                // Extract SMS1 and EMAIL1 values from the Map
			                String sms1 = (String) subscriber3Map.get("SMS1");
			                String email1 = (String) subscriber3Map.get("EMAIL1");

			                iLogger.info("SMS1: " + sms1);
			                iLogger.info("EMAIL1: " + email1);

			                if (contactID != null) {
			                    String updateQuery = "UPDATE wise.MAlertSubsriberGroup " +
			                                         "SET SubscriberGroup = JSON_SET(SubscriberGroup, '$.Subscriber3', " +
			                                         "'{\"SMS2\": \"" + contactID + "\", \"EMAIL2\": \"" + contactID + "\"";

			                    if (sms1 != null) {
			                        updateQuery += ", \"SMS1\": \"" + sms1 + "\"";
			                    }
			                    else
			                    {
			                    	updateQuery += ", \"SMS1\": \"" + null + "\"";
			                    }

			                    if (email1 != null) {
			                        updateQuery += ", \"EMAIL1\": \"" + email1 + "\"}'";
			                    }
			                    else {
			                        updateQuery += ", \"EMAIL1\": \"" + null + "\"}'";

			                    }

			                    // Complete the WHERE clause
			                    updateQuery += ") WHERE AssetID = ?";

			                    PreparedStatement updateStmt = prodConnection.prepareStatement(updateQuery);
			                    updateStmt.setString(1, assetID);
			                    iLogger.info(updateQuery);

			                    int rowsAffected = updateStmt.executeUpdate();
			                    totalRowsAffected += rowsAffected;
			                    iLogger.info("Updated SubscriberGroup for AssetID: " + assetID + ", rows affected: " + rowsAffected);
			                }
			            } catch (IOException e) {
			                fLogger.error("Error converting Subscriber3 JSON to Map: " + e.getMessage());
			            }
			        }
			    }

			    iLogger.info("Total rows affected: " + totalRowsAffected);
			} catch (Exception e) {
			    fLogger.fatal("Exception caught in setMappedAccountDataForLLCode: " + e.getMessage());
			    return "FAILURE";
			}
			//CR431.en

			

		}

		catch(Exception e){
			fLogger.fatal("MultipleBPcodeMappingRESTService: setMappedAccountDataForLLCode:: Exception Caught:",e);
			return "FAILURE";
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("MultipleBPcodeMappingRESTService: setMappedAccountDataForLLCode:: Exception Caught:"+e.getMessage());
			}

			try { 
				if (rs1 != null)
					rs1.close(); 
			} catch (Exception e) {
				fLogger.fatal("MultipleBPcodeMappingRESTService: setMappedAccountDataForLLCode:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("MultipleBPcodeMappingRESTService: setMappedAccountDataForLLCode:: Exception Caught:"+e.getMessage());
				}
			}

			if(actPst!=null){
				try {
					actPst.close();
				} catch (SQLException e) {
					fLogger.fatal("MultipleBPcodeMappingRESTService: setMappedAccountDataForLLCode:: Exception Caught:"+e.getMessage());
				}
			}

			if(tenancyPst!=null){
				try {
					tenancyPst.close();
				} catch (SQLException e) {
					fLogger.fatal("MultipleBPcodeMappingRESTService: setMappedAccountDataForLLCode:: Exception Caught:"+e.getMessage());
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					fLogger.fatal("MultipleBPcodeMappingRESTService: setMappedAccountDataForLLCode:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("MultipleBPcodeMappingRESTService: setMappedAccountDataForLLCode:: Exception Caught:"+e.getMessage());
				}
			}
		}

		return result;
	}


	public HashMap<String, Object> getVINDetails(String loginID,
			String vin) {

		HashMap<String, Object> respObj = new HashMap<String,Object>();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String UserID=null;
		String query=null;
		String account_type=null;
		String DealerName=null;
		String CustomerName=null;
		String BPCode=null;

		ConnectMySQL connMySql = new ConnectMySQL();

		//STEP 1:: validate userId

		UserID=new CommonUtil().getUserId(loginID);
		//UserID=loginID; //for testing

		if(UserID==null){
			respObj.put("Error", "Invalid loginID");
			return respObj; 
		}

		if(vin==null){
			respObj.put("Error", "vin is mandatory");
			return respObj;
		}

		//STEP 2:: Validate VIN against the user

		String serialNumber=new CommonUtil().validateVINAgainstUserId(vin, UserID);

		if(serialNumber==null){
			respObj.put("Error", "The given VIN "+vin+" is not under the given user "+UserID+" hierarchy");
//			bLogger.debug("Error The given VIN "+vin+" is not under the given user "+UserID+" hierarchy");
			return respObj;
		}

		//STEP 3:: Get the VIN details

		try{
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();

			//DF20181030-KO369761-Checking if machine is with customer or not.
			rs = statement.executeQuery("select * from asset_owner_snapshot where serial_number='"+serialNumber+"' and account_type='Customer'");
			if(!rs.next()){
				respObj.put("Error", "The given VIN "+vin+" is not with the customer");
//				bLogger.debug("Error The given VIN "+vin+" is not with the customer");
				return respObj;
			}

			query ="select aos.account_type,a.Account_Name,a.mapping_code from account a,asset_owner_snapshot aos,asset aa where aos.Serial_Number=aa.Serial_Number and aos.Serial_Number='"+serialNumber+"' and aos.Account_ID=a.Account_ID order by aos.ownership_start_date";


			iLogger.info("MultipleBPcodeMappingRESTService::getVINDetails::VIN Query: "+query);

			statement = prodConnection.createStatement();
			rs = statement.executeQuery(query);
			while(rs.next()){
				account_type=rs.getString("account_type");

				if(account_type!=null){

					if(account_type.equalsIgnoreCase("Dealer")){
						DealerName = rs.getString("Account_Name");

					}
					if(account_type.equalsIgnoreCase("Customer")){
						CustomerName = rs.getString("Account_Name");
						BPCode = rs.getString("mapping_code");

					}

				}

			}

			respObj.put("DealerName", DealerName);
			respObj.put("CustomerName", CustomerName);
			respObj.put("BPCode", BPCode);
			respObj.put("VIN", serialNumber);

		} 

		catch (SQLException e) {

			fLogger.fatal("MultipleBPcodeMappingRESTService::getVINDetails::: SQL error getting VIN data: "
					+ serialNumber
					+ "Message: "
					+ e.getMessage());
			e.printStackTrace();
		} 

		catch(Exception e)
		{
			fLogger.fatal("MultipleBPcodeMappingRESTService::getVINDetails: Fatal error getting VIN data: "
					+ serialNumber
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

		return respObj;
	}

	public List<HashMap<String, Object>> downloadDetails(String loginID, String bPCode) {

		List<HashMap<String, Object>> respObjList = new ArrayList<HashMap<String,Object>>();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String UserID=null;
		String query=null;
		
		ConnectMySQL connMySql = new ConnectMySQL();

		//STEP 1:: validate userId

		UserID=new CommonUtil().getUserId(loginID);

		if(UserID==null){
			HashMap<String, Object> respObj = new HashMap<String,Object>();

			respObj.put("Error", "Invalid loginID");

			respObjList.add(respObj);
			return respObjList; 
		}

		if(bPCode==null){
			HashMap<String, Object> respObj = new HashMap<String,Object>();
			respObj.put("Error", "BPCode is mandatory");
			respObjList.add(respObj);
			return respObjList; 
		}



		//STEP 3:: Get the VIN under the given BP code.

		try{
			prodConnection = connMySql.getConnection();

			query ="select aa.Serial_Number,aa.Customer,aa.custcode,bb.Dealer from " +
					"(select aos.Serial_Number,aos.account_type,a.Account_Name as Customer,a.mapping_code as custcode from " +
					"account a,asset_owner_snapshot aos,asset aa where " +
					"aos.Serial_Number=aa.Serial_Number and  a.mapping_code='"+bPCode+"'  and aos.Account_ID=a.Account_ID order by aos.Serial_Number) aa " +
					"inner join "+
					"(select aos.Serial_Number,aos.account_type,a.Account_Name as Dealer,a.mapping_code as dealercode from " +
					"account a,asset_owner_snapshot aos,asset aa where " +
					"aos.Serial_Number=aa.Serial_Number and " +
					"aos.serial_number in (select Serial_Number from asset_owner_snapshot ao, account ac where ac.mapping_code='"+bPCode+"' and ao.Account_ID=ac.Account_ID) " +
					"and aos.Account_ID=a.Account_ID and aos.account_type='Dealer' order by aos.Serial_Number)bb " +
					"on aa.Serial_Number=bb.Serial_Number";


			iLogger.info("MultipleBPcodeMappingRESTService::downloadDetails::VIN Query: "+query);

			statement = prodConnection.createStatement();
			rs = statement.executeQuery(query);
			while(rs.next()){
				HashMap<String, Object> respObj = new HashMap<String,Object>();
				respObj.put("DealerName", rs.getString("Dealer"));
				respObj.put("CustomerName", rs.getString("Customer"));
				respObj.put("BPCode", rs.getString("custcode"));
				respObj.put("VIN", rs.getString("Serial_Number"));
				respObjList.add(respObj);
			}


		} 

		catch (SQLException e) {

			fLogger.fatal("MultipleBPcodeMappingRESTService::downloadDetails::: SQL error getting bPCode data: "
					+ bPCode
					+ "Message: "
					+ e.getMessage());
			e.printStackTrace();
		} 

		catch(Exception e)
		{
			fLogger.fatal("MultipleBPcodeMappingRESTService::downloadDetails: Fatal error getting bPCode data: "
					+ bPCode
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

		return respObjList;
	}
	
	// Shajesh : 2021-01-14 : BP Code unmerge at VIN level
	public String updateMappingCodeByVin(List<String> vinNumberList, String userID) {
	    Logger fLogger = FatalLoggerClass.logger;
	    Logger iLogger = InfoLoggerClass.logger;
	    String response = "FAILURE";
	    ResultSet rs = null;
	    String accountDetailQuery = null;
	    String updateQueryForMappingCodes = null;
	    String updateQueryForMappingCodeinTenancy = null;
	    Connection connection = null;
	    Statement statement = null;
	    List<String> accountIdsList;
	    List<Integer> tenancyIdList;
	    List<String> account_codes = new ArrayList<String>();
	    List<String> mapping_codes = new ArrayList<String>();
	    String acc_code = null;
	    String map_code = null;
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	    Date currDate = new Date();
	    String currentTS = sdf.format(currDate);

	    if (vinNumberList != null && !vinNumberList.isEmpty()) {
	        try {
	            accountIdsList = searchAccountIdByVin(vinNumberList);
	            iLogger.info("List of account ID associated with the VIN -----> :: " + accountIdsList);
	            ConnectMySQL connFactory = new ConnectMySQL();
	            connection = connFactory.getConnection();
	            statement = connection.createStatement();
	            if (accountIdsList.size() == 0) {
	                iLogger.info("There is no AccountID associated with this VIN");
	                throw new CustomFault("Provide a valid VIN! There is no AccountID associated with this VIN");
	            } else {
	                ListToStringConversion conversion = new ListToStringConversion();
	                String accountIdAsString = conversion.getStringList(accountIdsList).toString();
	                tenancyIdList = new CommonUtil().getTenancyIdListByAcountIds(accountIdAsString);
	                accountDetailQuery = "Select * from account where account_id in(" + accountIdAsString + ")";
	                iLogger.info("Fetching account details for corresponding account ID -----> :: " + accountDetailQuery);
	                rs = statement.executeQuery(accountDetailQuery);
	                while (rs.next()) {
	                    acc_code = rs.getString("Account_Code");
	                    account_codes.add(acc_code);
	                    map_code = rs.getString("mapping_code");
	                    mapping_codes.add(map_code);
	                }
	                String account_code = conversion.getStringList(account_codes).toString();
	                String mapping_code = conversion.getStringList(mapping_codes).toString();
	                String accountId = conversion.getStringList(accountIdsList).toString();
	                String tenancyId = conversion.getIntegerListString(tenancyIdList).toString();

	                iLogger.info("accountId ----> :: " + accountId);
	                iLogger.info("account_code related to the corresponding account_id ----> :: " + account_code);
	                iLogger.info("mapping_code related to the corresponding account_id ----> :: " + mapping_code);
	                if (account_code.equals(mapping_code)) {
	                    iLogger.info(":: There is no data for De-Merging :: -> Account Code and Mapping Code are same ");
	                    response = "There is no data for De-Merging";
	                } else {
	                    updateQueryForMappingCodes = "update account set mapping_code= account_code where account_code in (" + account_code + ") AND Account_ID in (" + accountId + ")";
	                    iLogger.info("updateQueryForMappingCodes with account code if they are different :: " + updateQueryForMappingCodes);
	                    PreparedStatement statement1 = connection.prepareStatement(updateQueryForMappingCodes);
	                    statement1.executeUpdate(updateQueryForMappingCodes);

	                    updateQueryForMappingCodeinTenancy = "update tenancy set mapping_code= tenancy_code where tenancy_code in (" + account_code + ") AND tenancy_id in (" + tenancyId + ")";
	                    iLogger.info("updateQueryForMappingCodeinTenancy with account code if they are different :: " + updateQueryForMappingCodeinTenancy);
	                    PreparedStatement statement2 = connection.prepareStatement(updateQueryForMappingCodeinTenancy);
	                    statement2.executeUpdate(updateQueryForMappingCodeinTenancy);

	                    iLogger.info("Update Mapping_code with Account_Code : De-Merged Successfully");
	                    response = "SUCCESS";
	                }
	            }
	           // LL-147 : Sai Divya : Traceability for BP code un-merging .sn
	            iLogger.info("mapping_codes" +mapping_codes.size());
	            iLogger.info("account_codes" +account_codes.size());
	            // STEP 3::Update the Logging table only if response is SUCCESS
	            if ("SUCCESS".equals(response)) {
	                String preparedInsertQuery = "INSERT INTO account_code_unmerge_log_data (Account_Code, Mapping_Code, Updated_On, Updated_By) VALUES (?, ?, ?, ?)";
	                iLogger.info("preparedInsertQuery: " + preparedInsertQuery);
	                try (PreparedStatement pst = connection.prepareStatement(preparedInsertQuery)) {
	                    for (int i = 0; i < mapping_codes.size(); i++) {
	                        pst.setString(1, account_codes.get(i));
	                        pst.setString(2, mapping_codes.get(i));
	                        pst.setString(3, currentTS);
	                        pst.setString(4, userID);
	                        pst.addBatch();
	                    }
	                    pst.executeBatch();
	                    iLogger.info("Data inserted into account_code_unmerge_log_data successfully.");
	                } catch (SQLException e) {
	                    fLogger.fatal("Error while inserting data into account_code_unmerge_log_data: " + e.getMessage());
	                }
	            }//LL-147 : Sai Divya : Traceability for BP code un-merging .en
	        } catch (Exception e) {
	            fLogger.fatal("updateMappingCodeForCorrespondingVIN::issue while updating DB: Provide a valid VIN! There is no AccountID associated with this VIN " + e.getMessage());
	        } finally {
	            try {
	                if (rs != null) rs.close();
	                if (statement != null) statement.close();
	                if (connection != null) connection.close();
	            } catch (SQLException e) {
	                fLogger.fatal("Error while closing resources: " + e.getMessage());
	            }
	        }
	    }

	    return response;
	}

		private List<String> searchAccountIdByVin(List<String> vinNumbers)
				throws CustomFault {
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			List<String> account_id = new ArrayList<String>();
			ListToStringConversion conversion = new ListToStringConversion();
			String vinNumbersAsString = conversion.getStringList(vinNumbers).toString();
			iLogger.info("vinNumbersAsString :---> :"+vinNumbersAsString);
			String searchQuery = "select Account_ID  from asset_owner_snapshot where  serial_number in (" + vinNumbersAsString + ") AND Account_Type ='Customer'";
			iLogger.info("searchQuery for account ID for the Inout VIN ----> :: " + searchQuery);
			ConnectMySQL connFactory = new ConnectMySQL();
			try {
				Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(searchQuery);
				String result = "";
				while (rs.next()){
									if (rs.getString("Account_ID") != null) 
									{
									   result = rs.getString("Account_ID");
						               account_id.add(result);
									}
								}

			} catch (SQLException se) {
				fLogger.fatal("issue with query " + searchQuery + se.getMessage());
			} catch (Exception e) {
				fLogger.fatal("searchVinNumber() issue " + e.getMessage());
			}

			return account_id;
		}

}
