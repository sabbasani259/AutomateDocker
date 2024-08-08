package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.wipro.mda.AssetSubscriptionDetails;
import remote.wise.EAintegration.Qhandler.SubscriptionDataProducer;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.RetrofitmentReportResponseContract;
import remote.wise.util.BillingSubscriptionHistory;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;

/**
 * @author Dhiraj Kumar
 * @since 20230505:CR352
 * CR352 : 20230505 : Dhiraj K : Retrofitment Changes
 */
public class RetrofitmentImpl {

	Logger infoLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;

	public String doRetrofitment(HashMap<String, String> inputObj) {

		String status = "SUCCESS";
		String vin = inputObj.get("vin");
		String retrofitPeriod = inputObj.get("retrofitPeriod");
		String retrofitStartDate = inputObj.get("retrofitStartDate");
		String retrofitEndDate = inputObj.get("retrofitEndDate");
		String roleName = inputObj.get("roleName");
		String encodedUserId = inputObj.get("loginId");
		String tenancyIdListString = inputObj.get("tenancyIdList");

		String userID = null;
		int retrofitStatus = 0;
		Date renewalDate = null;
		Date installDate = null;
		String dealerCode = null;
		int productId = 0;
		boolean isUnderSubscription = false;

		//1. Validate login Id
		if (null != encodedUserId) {
			userID = new CommonUtil().getUserId(encodedUserId);
			if (null == userID) {
				infoLogger.info("User Id is invalid");
				status = "FAILURE:User Id is invalid";
				return status;
			}
		} else {
			infoLogger.info("User Id is invalid");
			status = "FAILURE:User Id is invalid";
			return status;
		}

		//2. Validate vin with tenancy ids list
		List<Integer> accountIds = new CommonUtil().getAccountIdsForVinFromTenancyIds(vin, tenancyIdListString);
		if (accountIds.isEmpty()) {
			infoLogger.info("FAILURE:User do not have ownership to machine");
			status = "FAILURE:User do not have ownership to machine";
			return status;
		}

		//4. Check if user is SA or not
		//CR469:20408644:Sai Divya  roleName Changed from JCB Admin to JCB Account 		
		if (!roleName.equalsIgnoreCase("Super Admin") && !roleName.equalsIgnoreCase("Dealer")
				&& !roleName.equalsIgnoreCase("JCB Account") && !roleName.equalsIgnoreCase("Dealer Admin")) {
			infoLogger.info("FAILURE:User do not have rights to proceed further");
			status = "FAILURE:User do not have rights to proceed further";
			return status;
		}
		//5. Insert data in required table for the vin
		Date currentDate = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDateStr = dateFormat.format(currentDate);
		String retrofitMonth = currentDateStr.split("-")[0] + currentDateStr.split("-")[1];
		String selectVinQuery = "SELECT a.serial_number, a.Renewal_Date, a.install_date, a.Retrofit_Flag, ac.mapping_code, a.product_id FROM asset a "
				+ " INNER JOIN asset_owner_snapshot aos on a.serial_number=aos.serial_number "
				+ " INNER JOIN account ac on ac.account_id=aos.account_id "
				+ " WHERE aos.account_type='Dealer' and ac.status=1 and a.serial_number like '%" + vin + "%'";

		infoLogger.info("selectVinQuery:" + selectVinQuery);
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection conn = connFactory.getConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(selectVinQuery)) {

			while (rs.next()) {
				vin = rs.getString("serial_number");
				retrofitStatus = rs.getInt("Retrofit_Flag");
				renewalDate = rs.getDate("Renewal_Date");
				installDate = rs.getDate("Install_Date");
				dealerCode = rs.getString("mapping_code");
				productId = rs.getInt("product_id");
			}
			if (dealerCode == null || dealerCode.equalsIgnoreCase("null") || dealerCode.isEmpty()) {
				infoLogger.info("FAILURE:VIN " + vin + " do not have Dealer.");
				status = "FAILURE:VIN: " + vin + " do not have Dealer.";
				return status;
			}

			if (productId == 0) {
				infoLogger.info("FAILURE:Product details not available for the VIN " + vin);
				status = "FAILURE:Product details not available for the VIN " + vin;
				return status;
			}

			if (installDate == null) {
				infoLogger.info("FAILURE:VIN " + vin + " is not installed yet from SAP.");
				status = "FAILURE:VIN " + vin + " is not installed yet from SAP.";
				return status;
			}
			
//			if (retrofitStatus == 1) {
//				infoLogger.info("FAILURE:VIN " + vin + " is already retrofitted.");
//				status = "FAILURE:VIN " + vin + " is already retrofitted.";
//				return status;
//			}
			
			if (renewalDate.after(currentDate)) {
				isUnderSubscription = true;
				infoLogger.info("INFO : VIN " + vin + " still in subscription.");
			}

			conn.setAutoCommit(false);
			conn.setSavepoint();
			// 5.1. Insert data in asset_retrofitment_data
			String insertQuery1 = "INSERT INTO asset_retrofitment_data (vin,retrofitPeriod, updatedOn, updatedBy, retrofitStartDate, retrofitEnddate, retrofitMonth) VALUES('" + vin + "'," + retrofitPeriod
					+ ",'" + currentDateStr + "','" + userID + "','" + retrofitStartDate + "','" + retrofitEndDate
					+ "'," + retrofitMonth + ")";
			st.execute(insertQuery1);

			if (isUnderSubscription) {
				String updateQuery = "UPDATE asset SET Retrofit_Flag=1 WHERE serial_number='" + vin + "'";
				st.execute(updateQuery);
				conn.commit();
			} else {
				// 5.2. Update data in asset table
				String updateQuery = "UPDATE asset SET Retrofit_Flag=1, Renewal_Flag=1, Renewal_Date='"
						+ retrofitEndDate + "' WHERE serial_number='" + vin + "'";
				st.execute(updateQuery);

				// 5.3. Insert data in ARD
				String insertQuery2 = "INSERT INTO asset_renewal_data VALUES('" + vin + "'," + retrofitPeriod + ",'"
						+ currentDateStr + "','" + userID + "','" + retrofitStartDate + "','" + retrofitEndDate + "')";
				st.execute(insertQuery2);

				// 5.4 Remove existing asset service details
				String deleteQuery = "DELETE FROM asset_service_schedule WHERE serialNumber='" + vin + "'";
				st.execute(deleteQuery);

				conn.commit();

				//CR463.sn
				HashMap<String, String> payload = new HashMap<>();
				String txnKey =  "SubcriptionData_"+ vin;
				payload.put("serialNumber", vin);
				payload.put("renewalEndDate", retrofitEndDate);
				new SubscriptionDataProducer(txnKey,payload);
				//CR463.en
				// 5.5 Add service schedule for the machine
				String response = new InstallationDateDetailsImpl().setAssetserviceSchedule(vin, retrofitStartDate,
						dealerCode, "", "", true);
				if (!response.contains("SUCCESS")) {
					conn.rollback();
					infoLogger.info("Failure occured in setting up Service Schedule for VIN " + vin);
					status = "FAILURE:Failure occured in setting up Service Schedule for VIN " + vin;
					return status;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = "FAILURE";
			fLogger.fatal("Exception occurred : " + e.getMessage());
		}
		if (isUnderSubscription) {
			infoLogger.info("VIN already in subscription. So no update at MDA, Billability or EP");
		}
		else {
			if (status.equalsIgnoreCase("SUCCESS")) {
				//6. Update data to EPServices
				try (Connection conn = connFactory.getEdgeProxyConnection(); Statement st = conn.createStatement()) {
					String selectQuery = "select * from device_status_info where vin_no='" + vin + "'";
					ResultSet resultSet = st.executeQuery(selectQuery);

					if (resultSet.next()) {
						String updateQuery = "UPDATE device_status_info SET Renewal_Flag=" + 1 + " where vin_no='" + vin
								+ "'";
						st.executeUpdate(updateQuery);
					} else {
						//Update renewal flag of the VIN
						String insertQuery = "INSERT INTO device_status_info(vin_no,Renewal_Flag) VALUES ('" + vin + "', "
								+ 1 + ")";
						st.execute(insertQuery);
					}
				} catch (Exception e) {
					e.printStackTrace();
					fLogger.fatal("Exception occurred to update device_status_info" + e.getMessage());
				}
				//7. Send data to MoolDA
				new AssetSubscriptionDetails().setAssetSubscriptionDetails(vin);

				//8. Send data to BillingModule
				new BillingSubscriptionHistory().updateSubsHistory(vin, retrofitStartDate + " 00:00:00",
						retrofitEndDate + " 00:00:00");
				
				//CR463.sn
				//9. Send Data to Mobile App
				HashMap<String, String> payload = new HashMap<>();
				String txnKey =  "SubcriptionData_"+ vin;
				payload.put("serialNumber", vin);
				payload.put("renewalEndDate", retrofitEndDate);
				new SubscriptionDataProducer(txnKey,payload);
				//CR463.en
			}
		}
		return status;
	}

	public HashMap<String, String> getRetrofitmentSubscription(HashMap<String, String> inputObj) throws CustomFault {

		HashMap<String, String> response = new HashMap<String, String>();
		String vin = inputObj.get("vin");
		String encodedUserId = inputObj.get("loginId");
		String userID = null;
		//1. Validate login Id
		if (null != encodedUserId) {
			userID = new CommonUtil().getUserId(encodedUserId);
			if (null == userID) {
				infoLogger.info("User Id is invalid");
				throw new CustomFault("FAILURE:User Id is invalid");
			}
			
		}else {
			infoLogger.info("FAILURE:User Id is invalid");
			throw new CustomFault("FAILURE:User Id is invalid");
		}
		//2. validate ownership of vin with userId
		String serialNum = new CommonUtil().validateVINAgainstUserId(vin, userID);
		if (!serialNum.equalsIgnoreCase(vin)) {
			infoLogger.info("FAILURE:VIN is not in ownership with " + userID);
			throw new CustomFault("FAILURE:VIN is not in ownership with " + userID);
		}

		response.put("retrofitFlag", "0");

		//3. Get subscription for vin
		String selectQuery = "SELECT retrofitStartDate, retrofitEndDate from asset_retrofitment_data where vin='" + vin
				+ "' order by updatedOn desc limit 1";
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection conn = connFactory.getConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(selectQuery)) {
			if (rs.next()) {
				response.put("retrofitStartDate",
						new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("retrofitStartDate")));
				response.put("retrofitEndDate",
						new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("retrofitEndDate")));
				response.put("retrofitFlag", "1");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put("retrofitFlag", "0");
			fLogger.fatal("Exception occurred" + e.getMessage());
		}

		return response;
	}

	public List<RetrofitmentReportResponseContract> getRetrofitmentReport(HashMap<String, String> inputObj) {

		String startDate = inputObj.get("startDate");
		String endDate = inputObj.get("endDate");
		endDate = endDate + " 23:59:59";
		String roleName = inputObj.get("roleName");
		String encodedUserId = inputObj.get("loginId");
		String tenancyIdListString = inputObj.get("tenancyIdList");
		String userID = new CommonUtil().getUserId(encodedUserId);
		;
		//1. Validate login Id
		if (null == userID) {
			infoLogger.info("FAILURE:User Id is invalid");
			return null;
		}
		String startMonth = startDate.split("-")[0] + startDate.split("-")[1];
		String endMonth = endDate.split("-")[0] + endDate.split("-")[1];

		//2. Validate vin with tenancy ids list
		List<Integer> accountIds = new CommonUtil().getAccountIdsForTenancyIds(tenancyIdListString);
		String accountIdsString = new ListToStringConversion().getIntegerListString(accountIds).toString();

		//3. Check if user is SA/Dealer or not
		//CR469:20408644:Sai Divya  roleName Changed from JCB Admin to JCB Account 
		if (!roleName.equalsIgnoreCase("Super Admin") && !roleName.equalsIgnoreCase("Dealer")
				&& !roleName.equalsIgnoreCase("JCB Account") && !roleName.equalsIgnoreCase("Dealer Admin")) {
			infoLogger.info("FAILURE:User do not have rights to proceed further");
			return null;
		}

		List<RetrofitmentReportResponseContract> responseList = new ArrayList<>();
		//4. Get data from DB
		String reportQuery = "SELECT " + "    ard.vin AS VIN," + "    ard.retrofitStartDate AS RetrofitStartDate,"
				+ "    ard.retrofitEndDate AS RetrofitEndDate," + "    ca.Mobile AS CustomerMobile,"
				+ "    ca.account_name AS CustomerName," + "    da.account_name AS DealerName,"
				+ "    za.account_name AS Zone," + "    ag.asseet_group_name AS Profile,"
				+ "    at.asset_type_name AS Model " + "FROM" + "    asset_retrofitment_data ard"
				+ "        INNER JOIN " + "	asset_owner_snapshot aos on ard.vin=aos.serial_number"
				+ "        INNER JOIN" + "    asset a ON a.serial_number = aos.serial_number" + "        LEFT JOIN"
				+ "    asset_owner_snapshot zonal" + "        INNER JOIN"
				+ "    account za ON zonal.account_id = za.account_id ON a.serial_number = zonal.serial_number"
				+ "        AND zonal.account_type = 'OEM RO'" + "        LEFT JOIN" + "    asset_owner_snapshot dealer"
				+ "        INNER JOIN"
				+ "    account da ON dealer.account_id = da.account_id ON a.serial_number = dealer.serial_number"
				+ "        AND dealer.account_type = 'Dealer'" + "        LEFT JOIN" + "    asset_owner_snapshot cust"
				+ "        INNER JOIN"
				+ "    account ca ON cust.account_id = ca.account_id ON a.serial_number = cust.serial_number"
				+ "        AND cust.account_type = 'Customer'" + "        JOIN"
				+ "    account ownerAcc ON a.primary_owner_id = ownerAcc.account_id" + "        INNER JOIN"
				+ "    products p ON a.product_id = p.product_id" + "        INNER JOIN"
				+ "    asset_type at ON at.asset_type_id = p.asset_type_id" + "        INNER JOIN"
				+ "    asset_group ag ON ag.asset_group_id = p.asset_group_id " + "WHERE" + "    aos.account_id IN ("
				+ accountIdsString + ")" + "        AND ard.updatedOn >= '" + startDate + "'"
				+ "        AND ard.updatedOn < '" + endDate + "'" + "        AND ard.retrofitMonth in (" + startMonth
				+ "," + endMonth + ")";
		infoLogger.info("Report Query:" + reportQuery);
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection conn = connFactory.getConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(reportQuery)) {
			while (rs.next()) {
				RetrofitmentReportResponseContract rep = new RetrofitmentReportResponseContract();
				rep.setVin(rs.getString("VIN"));
				rep.setRetrofitStartDate(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("RetrofitStartDate")));
				rep.setRetrofitEndDate(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("RetrofitEndDate")));
				rep.setCustomerMobileNo(rs.getString("CustomerMobile"));
				rep.setCustomerName(rs.getString("CustomerName"));
				rep.setDealerName(rs.getString("DealerName"));
				rep.setProfile(rs.getString("Profile"));
				rep.setModel(rs.getString("Model"));
				rep.setZone(rs.getString("Zone"));

				responseList.add(rep);
			}

		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception occurred" + e.getMessage());
		}
		return responseList;
	}

	public HashMap<String, String> validateEligibilty(HashMap<String, String> inputObj) {

		String status = "VALID:Vin is eligible for Retrofitment";
		HashMap<String, String> response = new HashMap<>();
		String vin = inputObj.get("vin");
		String roleName = inputObj.get("roleName");
		String encodedUserId = inputObj.get("loginId");
		String tenancyIdListString = inputObj.get("tenancyIdList");

		String userID = null;
		int retrofitStatus = 0;
		Date renewalDate = null;
		Date installDate = null;
		String dealerCode = null;
		int productId = 0;
		String retrofitEndDate = "";

		//1. Validate login Id
		if (null != encodedUserId) {
			userID = new CommonUtil().getUserId(encodedUserId);
			if (null == userID) {
				infoLogger.info("User Id is invalid");
				status = "INVALID:User Id is invalid";
				response.put("status", status);
				return response;
			}
		} else {
			infoLogger.info("User Id is invalid");
			status = "INVALID:User Id is invalid";
			response.put("status", status);
			return response;
		}

		//2. Validate vin with tenancy ids list
		List<Integer> accountIds = new CommonUtil().getAccountIdsForVinFromTenancyIds(vin, tenancyIdListString);
		if (accountIds.isEmpty()) {
			infoLogger.info("INVALID:User do not have ownership to machine");
			status = "INVALID:User do not have ownership to machine";
			response.put("status", status);
			return response;
		}

		//4. Check if user is SA or not
		//CR469:20408644:Sai Divya  roleName Changed from JCB Admin to JCB Account 
		if (!roleName.equalsIgnoreCase("Super Admin") && !roleName.equalsIgnoreCase("Dealer")
				&& !roleName.equalsIgnoreCase("JCB Account") && !roleName.equalsIgnoreCase("Dealer Admin")) {
			infoLogger.info("INVALID:User do not have rights to proceed further");
			status = "INVALID:User do not have rights to proceed further";
			response.put("status", status);
			return response;
		}
		//5. Get data from required table for the vin
		Date currentDate = Calendar.getInstance().getTime();

		String selectVinQuery = "SELECT a.serial_number, a.Renewal_Date, a.install_date, a.Retrofit_Flag, ac.mapping_code, a.product_id FROM asset a "
				+ " INNER JOIN asset_owner_snapshot aos on a.serial_number=aos.serial_number "
				+ " INNER JOIN account ac on ac.account_id=aos.account_id "
				+ " WHERE aos.account_type='Dealer' and ac.status=1 and a.serial_number like '%" + vin + "%'";

		infoLogger.info("selectVinQuery:" + selectVinQuery);
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection conn = connFactory.getConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(selectVinQuery)) {

			while (rs.next()) {
				vin = rs.getString("serial_number");
				retrofitStatus = rs.getInt("Retrofit_Flag");
				renewalDate = rs.getDate("Renewal_Date");
				installDate = rs.getDate("Install_Date");
				dealerCode = rs.getString("mapping_code");
				productId = rs.getInt("product_id");
			}
			if (dealerCode == null || dealerCode.equalsIgnoreCase("null") || dealerCode.isEmpty()) {
				infoLogger.info("FAILURE:VIN " + vin + " do not have Dealer.");
				status = "FAILURE:VIN: " + vin + " do not have Dealer.";
				response.put("status", status);
				return response;
			}

			if (productId == 0) {
				infoLogger.info("FAILURE:Product details not available for the VIN " + vin);
				status = "INVALID:Product details not available for the VIN " + vin;
				response.put("status", status);
				return response;
			}

			if (installDate == null) {
				infoLogger.info("FAILURE:VIN " + vin + " is not installed yet from SAP.");
				status = "INVALID:VIN " + vin + " is not installed yet from SAP.";
				response.put("status", status);
				return response;
			}
			
//			if (retrofitStatus == 1) {
//				infoLogger.info("FAILURE:VIN " + vin + " is already retrofitted.");
//				status = "INVALID:VIN " + vin + " is already retrofitted.";
//				response.put("status", status);
//				return response;
//			}
			
			if (renewalDate.after(currentDate)) {
				infoLogger.info("INFO:VIN " + vin + " still in subscription.");
					retrofitEndDate=new SimpleDateFormat("yyyy-MM-dd").format(renewalDate);
			}
			
		}catch (Exception e) {
			e.getMessage();
			status = "INVALID:Exception occurred : " + e.getMessage();
			fLogger.fatal(status);
			response.put("status", status);
			return response;
		}
		response.put("status", status);
		response.put("retrofitEndDate", retrofitEndDate);
		
		return response;
	}

}
