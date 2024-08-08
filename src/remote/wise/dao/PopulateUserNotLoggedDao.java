/*
 * ME10008770 : Dhiraj Kumar : 20230809 : Not logged in report Issue - Query modifications
 * ME100011945 : Dhiraj Kumar : 20240612 : Modified table truncate query.
 */

package remote.wise.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class PopulateUserNotLoggedDao {
	Logger fLogger = FatalLoggerClass.logger;
	Logger iLogger = InfoLoggerClass.logger;
	ConnectMySQL connFactory = new ConnectMySQL();

	public void deleteDataFromTable() {
		String deleteQuery = "delete from userNotLoggedDetails";
		try (Connection connection = connFactory.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(deleteQuery);) {
			statement.executeUpdate(deleteQuery);
		} catch (Exception e) {
			fLogger.fatal("deleteDataFromTable()::issue while deleting data in DB "
					+ e.getMessage());
		}

	}
	//ME10008770.sn
	public String truncateDataFromTable() {
	    //String query = "TRUNCATE TABLE userNotLoggedDetails_tmp";//
	    //ME100011945.sn
	    String createQuery = "CREATE TABLE userNotLoggedDetails_tmp2 like userNotLoggedDetails_tmp";
	    String swapQuery = "RENAME TABLE userNotLoggedDetails_tmp to userNotLoggedDetails_old, userNotLoggedDetails_tmp2 to userNotLoggedDetails_tmp";
	    String dropQuery = "DROP TABLE userNotLoggedDetails_old";
	    //ME100011945.en
	    String status = "FAILURE";
	    try (Connection connection = connFactory.getConnection();
		    Statement statement = connection
			    .createStatement()) {
		//statement.executeUpdate(query);//ME100011945.o
		statement.executeUpdate(createQuery);//ME100011945.n
		statement.executeUpdate(swapQuery);//ME100011945.n
		statement.executeUpdate(dropQuery);//ME100011945.n
		status="SUCCESS";
	    } catch (Exception e) {
		fLogger.fatal("truncateDataFromTable()::issue while deleting data in DB "
			+ e.getMessage());
	    }
	    return status;
	}
	//ME10008770.en

	public List<LinkedHashMap<String, Object>> getDataFromTable() {
		List<LinkedHashMap<String, Object>> responseList = new ArrayList<LinkedHashMap<String, Object>>();
		String countQuery = "select count(*) From (select  GROUP_CONCAT(a.contact_id) as contact_id, group_concat(a.first_name) as first_name, group_concat(CONCAT(COALESCE(a.last_name, 'null'))) as last_name, group_concat(a.primary_moblie_number) as primary_moblie_number, c.serial_number, any_value(d.mapping_code) as customercode, e.zonalcode,e.zone, e.dealer_name,e.DealerCode,e.installed_date , e.roll_off_date ,e.renewal_date , e.renew_state,e.model,e.profile from contact a, account_contact b , asset_owner_snapshot c, account d, com_rep_oem_enhanced e, account f  where a.contact_id not in (select contact_id from contactActivityLog_Report) and a.contact_id not in (select contact_id from APPloggeduser) and a.status=1 and c.account_id=d.account_id and d.mapping_code=f.mapping_code and b.account_id=f.account_id and a.role_id in (7,8) and c.account_type ='CUSTOMER' and a.contact_id = b.contact_id  and c.serial_number=e.serial_number AND c.serial_number NOT IN (SELECT serial_number FROM LoggedUsers) and c.serial_number NOT IN (SELECT serial_number FROM APPloggeduser) group by c.serial_number ) aa";

		try (Connection connection = connFactory.getConnection();
				Statement statement1 = connection.createStatement();
				ResultSet countRS = statement1.executeQuery(countQuery);
				Statement statement = connection.createStatement();	) {
			int count = 0;
			int loopFlag = 0;

			while (countRS.next()) {
				count = countRS.getInt(1);
			}
			LinkedHashMap<String, Object> tableMap = null;
			while (loopFlag < count) {
				//String selectQuery = "select  GROUP_CONCAT(a.contact_id) as contact_id, group_concat(a.first_name) as first_name, group_concat(CONCAT(COALESCE(a.last_name, 'null'))) as last_name, group_concat(a.primary_moblie_number) as primary_moblie_number, c.serial_number, any_value(d.mapping_code) as customercode, e.zonalcode,e.zone, e.dealer_name,e.DealerCode,e.installed_date , e.roll_off_date ,e.renewal_date , e.renew_state,e.model,e.profile from contact a, account_contact b , asset_owner_snapshot c, account d, com_rep_oem_enhanced e, account f  where a.contact_id not in (select contact_id from contactActivityLog_Report) and a.contact_id not in (select contact_id from APPloggeduser) and a.status=1 and c.account_id=d.account_id and d.mapping_code=f.mapping_code and b.account_id=f.account_id and a.role_id in (7,8) and c.account_type ='CUSTOMER' and a.contact_id = b.contact_id  and c.serial_number=e.serial_number AND c.serial_number NOT IN (SELECT serial_number FROM LoggedUsers) and c.serial_number NOT IN (SELECT serial_number FROM APPloggeduser) group by c.serial_number limit "+loopFlag+"  , 1000";//ME10008770.o
				//ME10008770.sn
				String selectQuery = "select  GROUP_CONCAT(a.contact_id) as contact_id, group_concat(a.first_name) as first_name,"
						+ " group_concat(CONCAT(COALESCE(a.last_name, 'null'))) as last_name, group_concat(a.primary_moblie_number) as primary_moblie_number,"
						+ " c.serial_number, any_value(d.mapping_code) as customercode, e.zonalcode,e.zone, e.dealer_name,e.DealerCode,e.installed_date ,"
						+ " e.roll_off_date ,e.renewal_date , e.renew_state,e.model,e.profile from contact a, account_contact b , asset_owner_snapshot c,"
						+ " account d, com_rep_oem_enhanced e  where a.contact_id not in (select contact_id from contactActivityLog_Report)"
						+ " and a.contact_id not in (select contact_id from APPloggeduser) and a.status=1 and c.account_id=d.account_id"
						+ " and b.account_id=d.account_id and a.role_id in (7,8) and c.account_type ='CUSTOMER'"
						+ " and a.contact_id = b.contact_id  and c.serial_number=e.serial_number AND d.mapping_code=e.bp_code"
						+ " AND c.serial_number NOT IN (SELECT serial_number FROM LoggedUsers)"
						+ " and c.serial_number NOT IN (SELECT serial_number FROM APPloggeduser) group by c.serial_number limit "+loopFlag+" , 1000";
				//ME10008770.en
				ResultSet rs = statement.executeQuery(selectQuery);
				iLogger.info("records range "+loopFlag+" out of "+count+" :: selectQuery " + selectQuery);
				while (rs.next()) {
					tableMap = new LinkedHashMap<String, Object>();
					tableMap.put("contact_id", rs.getString("contact_id"));
					tableMap.put("first_name", rs.getString("first_name"));
					if (((String) (rs.getString("last_name"))).isEmpty())
						tableMap.put("last_name", "null");
					else
						tableMap.put("last_name", rs.getString("last_name"));
					tableMap.put("primary_moblie_number", rs.getString("primary_moblie_number"));
					tableMap.put("serial_number", rs.getString("serial_number"));
					tableMap.put("customercode", rs.getString("customercode"));
					tableMap.put("zonalcode", rs.getString("zonalcode"));
					tableMap.put("zone", rs.getString("zone"));
					tableMap.put("dealer_name", rs.getString("dealer_name"));
					// ---------------------------------------------------------------------------
					tableMap.put("installed_date", rs.getString("installed_date"));
					tableMap.put("roll_off_date", rs.getString("roll_off_date"));
					tableMap.put("renewal_date", rs.getString("renewal_date"));
					tableMap.put("renew_state", rs.getString("renew_state"));
					tableMap.put("model", rs.getString("model"));
					tableMap.put("prodProfile", rs.getString("profile"));
					tableMap.put("DealerCode", rs.getString("DealerCode"));
					responseList.add(tableMap);
				}
				loopFlag = loopFlag + 1000;
			}

		} catch (Exception e) {
			e.printStackTrace();
			fLogger.info("Issue at fetchDataFromDB_AMS : " + e.getMessage());
		}
		iLogger.info("responseList" +responseList.toString());
		return responseList;
	}

	//public void insertDataIntoTable(List<LinkedHashMap<String, Object>> partialResultList) {//ME10008770.o
	public String insertDataIntoTable(List<LinkedHashMap<String, Object>> partialResultList) {//ME10008770.n
		String status ="SUCCESS";//ME10008770.n
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		//String insertQuery = "insert into userNotLoggedDetails (contact_id,first_name,last_name,primary_moblie_number,serial_number,customercode,zonalcode,zone,dealer_name,installed_date,roll_off_date,renewal_date,renew_state,model,prodProfile,DealerCode) values(";//ME10008770.o
		String insertQuery = "insert into userNotLoggedDetails_tmp (contact_id,first_name,last_name,primary_moblie_number,serial_number,customercode,zonalcode,zone,dealer_name,installed_date,roll_off_date,renewal_date,renew_state,model,prodProfile,DealerCode) values(";//ME10008770.n
		for (LinkedHashMap<String, Object> map : partialResultList) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				insertQuery = insertQuery + "'" + entry.getValue() + "'" + ",";
			}
			if (insertQuery.endsWith(","))
				insertQuery = insertQuery
						.substring(0, insertQuery.length() - 1);
			insertQuery += "),(";
		}
		if (insertQuery.endsWith("("))
			insertQuery = insertQuery.substring(0, insertQuery.length() - 2);
		iLogger.info("updateDB() insertQuery():: " + insertQuery);
		try (Connection connection = connFactory.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(insertQuery);) {
			statement.executeUpdate(insertQuery);
			

		} catch (Exception e) {
			System.out.println("insertQuery : "+insertQuery);
			fLogger.fatal("insertQuery : "+insertQuery);
			fLogger.fatal("updateDB()::issue while updating DB "
					+ e.getMessage());
			status="FAILURE";//ME10008770.n
		}
		return status;//ME10008770.n
	}

	//ME10008770.sn
	public String swapDataFromTable() {
		String status="SUCCESS";
		String tableRenameQuery = "RENAME TABLE userNotLoggedDetails TO userNotLoggedDetails_old, "
				+ "userNotLoggedDetails_tmp TO userNotLoggedDetails, " + "userNotLoggedDetails_old TO userNotLoggedDetails_tmp";
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection
						.createStatement()) {
		statement.execute(tableRenameQuery);
		}catch (Exception e) {
			fLogger.fatal("updateDB()::issue while updating DB "
					+ e.getMessage());
			status="FAILURE";
		}
		iLogger.info("userNotLoggedDetails_tmp renamed to userNotLoggedDetails");
		return status;
	}
	//ME10008770.en
}
