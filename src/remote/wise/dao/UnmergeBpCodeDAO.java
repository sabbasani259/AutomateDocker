/*
 * JCB417: Prasanna :20230607 : Changes for updating tenancy table
 * LL-147 : Sai Divya :20250429 : Traceability for BP code un-merging
 */
package remote.wise.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class UnmergeBpCodeDAO {
	Logger fLogger = FatalLoggerClass.logger;
	Logger iLogger = InfoLoggerClass.logger;

	public String updateMappingCodeForCorrespondingAccountCode(
			String accountCode,String userID) {
		ConnectMySQL connFactory = new ConnectMySQL();
		String response = "Success";
		List<String> mapping_codes = new ArrayList<String>();
		  String currentTS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//LL-147.n
		  String mappingCodeQuery="select mapping_code from  account where Account_Code='"+accountCode+"'";//LL-147.n
		String updateQueryForMappingCodes = "update account set mapping_code="
				+ "'" + accountCode + "'" + " where Account_Code=" + "'"
				+ accountCode + "'";
		String updateQueryForMACustomer = "update account set MAFlag=0 where Account_Code="
				+ "'" + accountCode + "'";
		String UpdateQuearyForMappingCodeTenancy = "update tenancy set mapping_code='"+accountCode+"' where Tenancy_Code='"+accountCode+"'" ;//JCB417.n
		iLogger.info("updateQueryForMappingCodes : "+updateQueryForMappingCodes);
		iLogger.info("updateQueryForMACustomer : "+updateQueryForMACustomer);
		iLogger.info("updateQueryForMappingCodeTenancy : "+UpdateQuearyForMappingCodeTenancy);//JCB417.n
		iLogger.info("mappingCodeQuery : "+mappingCodeQuery);//LL-147.n
		try (Connection connection = connFactory.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(updateQueryForMappingCodes);
				PreparedStatement statement1 = connection
						.prepareStatement(updateQueryForMACustomer);
				PreparedStatement statement2 = connection
						.prepareStatement(UpdateQuearyForMappingCodeTenancy);
				PreparedStatement statement3= connection.prepareStatement(mappingCodeQuery);) {//LL-147.n
			//LL-147.sn
			try (ResultSet resultSet = statement3.executeQuery()) {
			        while (resultSet.next()) {
			          
			        String  mappingCode = resultSet.getString("mapping_code");
	                    mapping_codes.add(mappingCode);
			        }
			    }
			//LL-147.en
			statement.executeUpdate(updateQueryForMappingCodes);
			statement1.executeUpdate(updateQueryForMACustomer);//JCB417.n
			statement2.executeUpdate(UpdateQuearyForMappingCodeTenancy);//JCB417.n
			//LL-147.sn
			  String preparedInsertQuery = "INSERT INTO account_code_unmerge_log_data (Account_Code, Mapping_Code, Updated_On, Updated_By) VALUES (?, ?, ?, ?)";
	            iLogger.info("preparedInsertQuery: " + preparedInsertQuery);
	            try (PreparedStatement pst = connection.prepareStatement(preparedInsertQuery)) {
	                for (int i = 0; i < mapping_codes.size(); i++) {
	                    pst.setString(1, accountCode);
	                    pst.setString(2, mapping_codes.get(i));
	                    pst.setString(3, currentTS);
	                    pst.setString(4, userID);
	                    pst.addBatch();
	                }
	                pst.executeBatch();
	                iLogger.info("Data inserted into account_code_unmerge_log_data successfully.");
	                return response ;//JCB417.n
	            } catch (SQLException e) {
	                fLogger.fatal("Error while inserting data into account_code_unmerge_log_data: " + e.getMessage());
	            }//LL-147.en
		   return response ;//JCB417.n
			
		} catch (Exception e) {
			response = "failure";
			fLogger.fatal("updateMappingCodeForCorrespondingAccountCode()::issue while updating DB "
					+ e.getMessage());
		}
		return response;
	}

	public boolean searchAccountCode(String accountCode) {
		String searchQuery = "select Account_Code  from account  where  Account_Code="
				+ "'" + accountCode + "'";
		boolean found = false;
		iLogger.info("searchQuery : "+searchQuery);
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(searchQuery);) {

			while (rs.next()) {
				found = true;
			}

		} catch (SQLException se) {
			fLogger.fatal("issue with query " + searchQuery + se.getMessage());
		} catch (Exception e) {
			fLogger.fatal("searchAccountCode() issue " + e.getMessage());
		}
		return found;
	}
}
