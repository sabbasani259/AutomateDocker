package remote.wise.service.webservice;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

@Path("/InsertIntoContactActivityLog")
public class InsertIntoContactActivityLog {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String testMethod() {
		String status = "FAILURE";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		// String turncateQuery = "truncate table contactActivityLog_Report ";
		String countQuery = "select count(DISTINCT Contact_ID ) from contact_activity_log where login_date > '2022-01-01'  and login_date < CURDATE()-1";

		// iLogger.info("truncate querry :: " +turncateQuery);
		int result = 0;
		ConnectMySQL connectPool = new ConnectMySQL();
		try (Connection connection = connectPool.getConnection();
				Statement statement1 = connection.createStatement();
				Statement statement2 = connection.createStatement();
				Statement statement3 = connection.createStatement();
				Statement updateStament = connection.createStatement();) {
			int count = 0;
			int loopFlag = 0;
			ResultSet countRS = statement1.executeQuery(countQuery);
			while (countRS.next()) {
				count = countRS.getInt(1);
			}
			HashMap<String, String> contactAvtyReportMap = new HashMap<>();
			String contactActivityLogReportQuerry = "select * from contactActivityLog_Report";
			ResultSet contactActivityLogReportRS = statement1.executeQuery(contactActivityLogReportQuerry);
			while (contactActivityLogReportRS.next()) {
				String contactId = contactActivityLogReportRS.getString("contact_id");
				String loginId = contactActivityLogReportRS.getString("loginDate");
				contactAvtyReportMap.put(contactId, loginId);
			}
			while (loopFlag < count) {
				String query = " select contact_id , max(login_date) from contact_activity_log where login_date > '2022-01-01'  and login_date < CURDATE()-1  group by Contact_ID limit "
						+ loopFlag + "," + "1000";

				ResultSet contactActvtyLogsRS = statement1.executeQuery(query);
				while (contactActvtyLogsRS.next()) {
					String contactId = contactActvtyLogsRS.getString("contact_id");
					String loginDate = contactActvtyLogsRS.getString("max(login_date)");
					if (contactAvtyReportMap.containsKey(contactId)) {
						String updatequery = "update contactActivityLog_Report set loginDate= '" + loginDate
								+ "' where contact_id= '" + contactId + "'";
						int val = updateStament.executeUpdate(updatequery);
						if (val > 0) {
							result = result + val;
						}

					} else {
						String insertQuery = " insert into contactActivityLog_Report(contact_id ,loginDate) values('"
								+ contactId + "','" + loginDate + "')";
						int val = updateStament.executeUpdate(insertQuery);
						if (val > 0) {
							result = result + val;
						}
					}

				}

				iLogger.info("insert status for records range " + loopFlag + " out of " + count + " :: " + result);
				loopFlag = loopFlag + 1000;
			}
			status = "insert or update status for records of  " + count + " :: " + result;
			iLogger.info("insert or update status for records of  " + count + " :: " + result);
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Error in connecting Database" + e.getMessage());
		}
		return status;
	}

}
