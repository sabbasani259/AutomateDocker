/**
 * JCB6337 : Dhiraj Kumar : 20230405 : Tenancy Bridge population logic into WISE
 */

package remote.wise.service.implementation;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;

import remote.wise.handler.SendEmailWithKafka;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

public class TenancyBridgeImpl {
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	int initialTenancyBridgeSize = 0;
	int finalTenancyBridgeSize = 0;
	Properties prop = CommonUtil.getDepEnvProperties();
	String tenancyErrorFile = prop.getProperty("TenancyErrorFile");
	String emailId = prop.getProperty("TenancyEmailId");

	public boolean createErrorFile() {
		boolean status = true;
		File file = new File(tenancyErrorFile);
		try {
			status = file.createNewFile();
			if (status) {
				iLogger.info("Tmp error file is created.");
			} else {
				iLogger.info("Tmp error file already exists.");
			}
		} catch (Exception e) {
			fLogger.fatal("createErrorFile: Exception occurred:" + e.getMessage());
		}
		return status;
	}

	public String setTenancyBridge() {

		String response = "FAILURE";
		int count = 0;
		String emailBody = null;
		String emailSubject = "Tenanacy Bridge Run Update";
		Timestamp createdTimestamp = new Timestamp(new Date().getTime());
		String transactionTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdTimestamp.getTime());
		while (!response.equalsIgnoreCase("SUCCESS")) {
			response = populateTenancyBridge();
			count++;
			if (count >= 3)
				break;
		}
		if (!response.equalsIgnoreCase("SUCCESS")) {
			//Update failure in txt file
			iLogger.info("Unable to update tenancy_bridge after " + count + " retries.. creating error file at..!!!");
			System.out.println("Unable to update tenancy_bridge after " + count + " retries.. creating error file at..!!!");
			createErrorFile();
			emailBody = "Hi,\n\nIssue occurred while updating tenancy bridge at "+transactionTime+". Please check ASAP..!!!\n\n"
					+ "Regards,\nLiveLinkIndia";
		} else {
			emailBody = "Hi,\n\nTenancy Bridge update completed at "+transactionTime+". \nPrevious size:" + initialTenancyBridgeSize
					+ "\nUpdated size:" + finalTenancyBridgeSize+"\n\nRegards,\nLiveLinkIndia";
		}
		//send confirmation mail
		
		SendEmailWithKafka sendEmail = new SendEmailWithKafka();
		iLogger.info("send Email to: emailID= " + emailId);
		String result = sendEmail.sendTenancyMail(emailId, emailSubject, emailBody, transactionTime);

		iLogger.info("sendTenancyMail result: " + result);

		if (result.equalsIgnoreCase("FAILURE")) {
			iLogger.info("sendTenancyMail failed");
		}
		iLogger.info("sendTenancyMail success");
		return response;
	}

	public String populateTenancyBridge() {

		String response = "FAILURE";

		ConnectMySQL factory = new ConnectMySQL();
		String selectQuery = "SELECT Tenancy_ID, Parent_Tenancy_ID, Tenancy_Type_ID FROM tenancy";
		Map<Integer, List<Integer>> tenancyMap = new TreeMap<>();
		try (Connection conn = factory.getConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(selectQuery)) {
			while (rs.next()) {
				int childId = rs.getInt("Tenancy_ID");
				int parentId = rs.getInt("Parent_Tenancy_ID");
				int tenancyTypeId = rs.getInt("Tenancy_Type_ID");
				//1.Create tenancy map from tenancy table
				tenancyMap.put(childId, Arrays.asList(parentId, tenancyTypeId));
			}

			//2. Truncate tenancy_bridge_tmp table
			String tableTruncateQuery = "TRUNCATE TABLE tenancy_bridge_tmp";
			statement.execute(tableTruncateQuery);

			int maxBatchCount = 1000;
			int batchCount = 0;
			int[] insertCount = null;
			int totalInsertedCount = 0;
			for (Integer childTenancyId : tenancyMap.keySet()) {
				Integer parentTenacyId = tenancyMap.get(childTenancyId).get(0);
				Integer tenancyTypeId = tenancyMap.get(childTenancyId).get(1);

				//2. Create map of parent ids for the child tenancy id
				Map<Integer, Integer> parentIdsMap = new HashMap<>();
				for (int level = 0; level <= tenancyTypeId; level++) {
					if (level == 0)
						parentIdsMap.put(childTenancyId, level);
					else {
						if (parentTenacyId != null && parentTenacyId != 0) {
							parentIdsMap.put(parentTenacyId, level);
							if (tenancyMap.get(parentTenacyId) != null)
								parentTenacyId = tenancyMap.get(parentTenacyId).get(0);
						}
					}
				}

				//3. Create insert query for the tenancy_bridge_tmp 
				String insertQuery = "INSERT INTO tenancy_bridge_tmp VALUES ";
				boolean firstRow = true;
				for (int id : parentIdsMap.keySet()) {
					if (firstRow) {
						insertQuery = insertQuery + " (" + id + "," + childTenancyId + "," + parentIdsMap.get(id)
								+ ",null, null)";
						firstRow = false;
					} else {
						insertQuery = insertQuery + ", (" + id + "," + childTenancyId + "," + parentIdsMap.get(id)
								+ ",null, null)";
					}
				}

				//5. Now add to insert batch
				statement.addBatch(insertQuery);
				batchCount++;
				if (batchCount % maxBatchCount == 0) {
					insertCount = statement.executeBatch();
					totalInsertedCount += Arrays.stream(insertCount).sum();
					iLogger.info("populateTenancyBridge:Inserted Count:" + Arrays.stream(insertCount).sum());
					response = "SUCCESS";
				}
			}
			//6. Execute remaining queries
			insertCount = statement.executeBatch();
			totalInsertedCount += Arrays.stream(insertCount).sum();
			iLogger.info("populateTenancyBridge:Inserted Count:" + Arrays.stream(insertCount).sum());
			response = "SUCCESS";
			iLogger.info("populateTenancyBridge:Total Row Inserted Count:" + totalInsertedCount + ":" + response);

			//7. Existing count of rows in tenancy_bridge
			String tenancyBridgeSizeQuery = "SELECT count(*) AS COUNT FROM tenancy_bridge";
			ResultSet rs2 = statement.executeQuery(tenancyBridgeSizeQuery);

			if (rs2.next()) {
				initialTenancyBridgeSize = rs2.getInt("COUNT");
			}
			iLogger.info("Initial size of tenancy_bride:" + initialTenancyBridgeSize);
			//8. Move tenancy_bridge_tmp table data to tenancy_bridge
			if (response.equalsIgnoreCase("SUCCESS")) {
				String tableRenameQuery = "RENAME TABLE tenancy_bridge TO tenancy_bridge_old, "
						+ "tenancy_bridge_tmp TO tenancy_bridge, " + "tenancy_bridge_old TO tenancy_bridge_tmp";
				statement.execute(tableRenameQuery);
				iLogger.info("tenancy_bridge_tmp renamed to tenancy_bridge");
			}
			//9. Final count of rows in tenancy_bridge
			ResultSet rs3 = statement.executeQuery(tenancyBridgeSizeQuery);
			if (rs3.next()) {
				finalTenancyBridgeSize = rs3.getInt("COUNT");
			}
			iLogger.info("Final size of tenancy_bride:" + finalTenancyBridgeSize);
			//10. Clear tenancy map
			tenancyMap.clear();

		} catch (SQLException e) {
			e.printStackTrace();
			fLogger.fatal("populateTenancyBridge: SQLException occurred:" + e.getMessage());
			response = "FAILURE";
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("populateTenancyBridge: Exception occurred:" + e.getMessage());
			response = "FAILURE";
		}
		return response;
	}

}
