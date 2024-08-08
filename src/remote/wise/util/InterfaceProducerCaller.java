package remote.wise.util;

import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.Qhandler.InterfaceDataProducer;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 
 * @author DH20313904
 * ME100030804 : Dhiraj Kumar : 20220826 : Incorrect SAP data (Roll off, Personality, Gate out Sale and D2C sale) 
 * 		   		 being sent to Mobile App 
 *
 */
public class InterfaceProducerCaller {

	public void invokeInterfaceProducer(String serialNumber, String stage) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		ConnectMySQL connFactory = new ConnectMySQL();

		HashMap<String, String> InterfacePoducerDataPayloadMap = new HashMap<String, String>();
		//Get serial number if machine number provided
		serialNumber = getVinFromMachineNumber(serialNumber);
		InterfacePoducerDataPayloadMap.put("TXN_KEY", "InterfaceProducerData" + "_" + serialNumber);
		InterfacePoducerDataPayloadMap.put("serialNumber", serialNumber);
		String interfaceStage = stage.trim().toUpperCase();
		
		switch (interfaceStage) {
		case "ROLLOFF":
			iLogger.info("Updating payload by reading WISE DB for "  + interfaceStage + "real time flow");
			String query = "Select Rolloff_Date, llFlag from asset where serial_number = '" + serialNumber + "'";
			InterfacePoducerDataPayloadMap.put("profile", "NA");
			InterfacePoducerDataPayloadMap.put("model", "NA");
			InterfacePoducerDataPayloadMap.put("llPlusFlag", "NA");
			InterfacePoducerDataPayloadMap.put("dealerCode", "NA");
			InterfacePoducerDataPayloadMap.put("dealerName", "NA");
			InterfacePoducerDataPayloadMap.put("customerCode", "NA");
			InterfacePoducerDataPayloadMap.put("customerName", "NA");
			InterfacePoducerDataPayloadMap.put("zonalCode", "NA");
			try (Connection connection = connFactory.getConnection();
					Statement statement = connection.createStatement();
					ResultSet rs = statement.executeQuery(query)) {
				String rollOffDate = null;
				String llFlag = null;
				while (rs.next()) {
					rollOffDate = String.valueOf(rs.getTimestamp("Rolloff_Date"));
					llFlag = rs.getString("llFlag");
				}
				InterfacePoducerDataPayloadMap.put("rollOffDate", rollOffDate);
				if (llFlag == null || llFlag.isEmpty() || llFlag.equalsIgnoreCase("null"))
					llFlag = "NA";
				InterfacePoducerDataPayloadMap.put("llPlusFlag", llFlag);
			} catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Exception occurred while fetching data for " +  stage + " real time flow", e);
			}
			break;

		case "PERSONALITY":
			iLogger.info("Updating payload by reading WISE DB for "  + interfaceStage + "real time flow");
			String query2 = "SELECT ag.asseet_group_name, at.asset_type_name, a.Rolloff_Date, a.llFlag from asset_group ag, asset_type at, products p, asset a"
					+ " WHERE a.serial_number = '" + serialNumber + "'" + " AND a.product_id = p.product_id"
					+ " AND p.asset_group_id = ag.asset_group_id" + " AND p.asset_type_id = at.asset_type_id";
			InterfacePoducerDataPayloadMap.put("dealerCode", "NA");
			InterfacePoducerDataPayloadMap.put("dealerName", "NA");
			InterfacePoducerDataPayloadMap.put("customerCode", "NA");
			InterfacePoducerDataPayloadMap.put("customerName", "NA");
			InterfacePoducerDataPayloadMap.put("zonalCode", "NA");
			try (Connection connection = connFactory.getConnection();
					Statement statement = connection.createStatement();
					ResultSet rs2 = statement.executeQuery(query2)) {

				String assetGroupName = null;
				String assetTypeName = null;
				String rollOffDate = null;
				String llFlag = null;
				while (rs2.next()) {
					assetGroupName = rs2.getString("asseet_group_name");
					assetTypeName = rs2.getString("asset_type_name");
					rollOffDate = String.valueOf(rs2.getTimestamp("Rolloff_Date"));
					llFlag = rs2.getString("llFlag");
				}
				InterfacePoducerDataPayloadMap.put("profile", assetGroupName);
				InterfacePoducerDataPayloadMap.put("model", assetTypeName);
				InterfacePoducerDataPayloadMap.put("rollOffDate", rollOffDate);
				if (llFlag == null || llFlag.isEmpty() || llFlag.equalsIgnoreCase("null"))
					llFlag = "NA";
				InterfacePoducerDataPayloadMap.put("llPlusFlag", llFlag);
			} catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Exception occurred while fetching data for " +  stage + " real time flow", e);
			}
			break;

		case "GATEOUT":
		case "D2CSALE":
			iLogger.info("Updating payload by reading WISE DB for "  + interfaceStage + "real time flow");
			String query3 = "SELECT ag.asseet_group_name," + 
					"    at.asset_type_name," + 
					"    a.Rolloff_Date," + 
					"    aos.Account_Type," + 
					"    acc.account_name," +
					"	 acc.account_code," +
				    "	 a.llFlag" +
					" FROM" + 
					"    asset_group ag," + 
					"    asset_type at," + 
					"    products p," + 
					"    asset a," + 
					"    asset_owner_snapshot aos" + 
					"        INNER JOIN" + 
					"    account acc ON aos.account_id = acc.account_id" + 
					" WHERE" + 
					"    a.serial_number = '" + serialNumber + "'" + 
					"        AND a.product_id = p.product_id" + 
					"        AND p.asset_group_id = ag.asset_group_id" + 
					"        AND p.asset_type_id = at.asset_type_id" + 
					"        AND aos.serial_number = '" + serialNumber + "'";
			try (Connection connection = connFactory.getConnection();
					Statement statement = connection.createStatement();
					ResultSet rs3 = statement.executeQuery(query3)) {
				String assetGroupName = null;
				String assetTypeName = null;
				String rollOffDate = null;
				String customerCode = null;
				String customerName = null;
				String dealerCode = null;
				String dealerName = null;
				String zonalCode = null;
				String llFlag = null;
				while(rs3.next()) {
					assetGroupName = rs3.getString("asseet_group_name");
					assetTypeName = rs3.getString("asset_type_name");
					rollOffDate = String.valueOf(rs3.getTimestamp("Rolloff_Date"));
					llFlag = rs3.getString("llFlag");
					if(rs3.getString("Account_Type").equalsIgnoreCase("Customer")) {
						customerName = rs3.getString("Account_Name");
						customerCode = rs3.getString("Account_Code");
					}
					else if(rs3.getString("Account_Type").equalsIgnoreCase("Dealer")) {
						dealerName = rs3.getString("Account_Name");
						dealerCode = rs3.getString("Account_Code");
					}
					else if(rs3.getString("Account_Type").equalsIgnoreCase("OEM RO")) {
						zonalCode = rs3.getString("Account_Code");
					}
				}
				if (customerName == null || customerName.isEmpty() || customerName.equalsIgnoreCase("null"))
					customerName = "NA";
				if (customerCode == null || customerCode.isEmpty() || customerCode.equalsIgnoreCase("null"))
					customerCode = "NA";
				if (llFlag == null || llFlag.isEmpty() || llFlag.equalsIgnoreCase("null"))
					llFlag = "NA";
				InterfacePoducerDataPayloadMap.put("profile", assetGroupName);
				InterfacePoducerDataPayloadMap.put("model", assetTypeName);
				InterfacePoducerDataPayloadMap.put("rollOffDate", rollOffDate);
				InterfacePoducerDataPayloadMap.put("llPlusFlag", llFlag);
				InterfacePoducerDataPayloadMap.put("dealerCode", dealerCode);
				InterfacePoducerDataPayloadMap.put("dealerName", dealerName);
				InterfacePoducerDataPayloadMap.put("customerCode", customerCode);
				InterfacePoducerDataPayloadMap.put("customerName", customerName);
				InterfacePoducerDataPayloadMap.put("zonalCode", zonalCode);
			}catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Exception occurred while fetching data for " +  stage + " real time flow", e);
			}
			break;
		default:
			break;
		}

		iLogger.info("Calling InterfaceDataProducer for publishing the data to kafka topic for " + interfaceStage + " -------> START");
		new InterfaceDataProducer(InterfacePoducerDataPayloadMap.get("TXN_KEY"), InterfacePoducerDataPayloadMap);
		iLogger.info("Calling InterfaceDataProducer for publishing the data to kafka topic for " + interfaceStage + " -------> END");
	}
	
	public String getVinFromMachineNumber(String vin) {
		String serialNum = null;
		Logger fLogger = FatalLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement()) {
			String query1 = "SELECT Serial_Number from asset where serial_number='" + vin + "'";
			ResultSet rs1 = statement.executeQuery(query1);
			while (rs1.next()) {
				serialNum = rs1.getString("Serial_Number");
			}
			if (serialNum == null) {
				//its machine number
				vin = vin.replaceFirst("^0+(?!$)", "");
				String query2 = "SELECT Serial_Number from asset where machine_number='" + vin + "'";
				ResultSet rs2 = statement.executeQuery(query2);
				while (rs2.next()) {
					serialNum = rs2.getString("Serial_Number");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("getVinFromMachineNumber:Exception in getting serial number"
					+ e.getMessage());
		}
		return serialNum;
	}
}
